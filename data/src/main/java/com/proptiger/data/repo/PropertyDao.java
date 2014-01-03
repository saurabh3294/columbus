/**
 * 
 */
package com.proptiger.data.repo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.SolrResult;
import com.proptiger.data.model.filter.FieldsMapLoader;
import com.proptiger.data.model.filter.Operator;
import com.proptiger.data.model.filter.SolrQueryBuilder;
import com.proptiger.data.pojo.Paging;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.SortBy;
import com.proptiger.data.pojo.SortOrder;
import com.proptiger.data.service.pojo.SolrServiceResponse;
import com.proptiger.data.util.SolrResponseReader;
import com.proptiger.data.util.UtilityClass;

/**
 * @author mandeep
 * 
 */
@Repository
public class PropertyDao {
    @Autowired
    private SolrDao solrDao;

    // to make it autowired.
    private SolrResponseReader solrResponseReader = new SolrResponseReader();

    public List<Property> getProperties(int projectId) {
        SolrQuery solrQuery = createSolrQuery(null);
        solrQuery.addFilterQuery("PROJECT_ID:" + projectId);
        solrQuery.setSort("TYPE_ID", ORDER.asc);
        
        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
        List<Property> properties = queryResponse.getBeans(Property.class);
        return properties;        
    }

    public List<Property> getProperties(Selector selector) {
        List<SolrResult> solrResults = getSolrResultsForProperties(selector);
        List<Property> properties = new ArrayList<Property>();
        for (SolrResult solrResult : solrResults) {
            properties.add(solrResult.getProperty());
        }
        return properties;
    }

    public Map<String, List<Map<Object, Long>>> getFacets(List<String> fields, Selector propertySelector) {
        SolrQuery query = createSolrQuery(propertySelector);
        query.setFacetMinCount(1);
        query.setFacetLimit(-1);
        for (String field : fields) {
            query.addFacetField(FieldsMapLoader.getDaoFieldName(SolrResult.class, field));
        }

        Map<String, List<Map<Object, Long>>> resultMap = new HashMap<String, List<Map<Object, Long>>>();
        for (String field : fields) {
            resultMap.put(field, new ArrayList<Map<Object, Long>>());
            for (Count count : solrDao.executeQuery(query).getFacetField(FieldsMapLoader.getDaoFieldName(SolrResult.class, field)).getValues()) {
                HashMap<Object, Long> map = new HashMap<Object, Long>();
                map.put(count.getName(), count.getCount());
                resultMap.get(field).add(map);
            }
        }

        return resultMap;
    }

    public Map<String, FieldStatsInfo> getStats(List<String> fields, Selector propertySelector, List<String> facetFields) {
        SolrQuery query = createSolrQuery(propertySelector);
        query.add("stats", "true");

        for (String field : fields) {
            query.add("stats.field", FieldsMapLoader.getDaoFieldName(SolrResult.class, field));
        }
        if(facetFields != null){
        	for(String field: facetFields){
        		query.add("stats.facet", FieldsMapLoader.getDaoFieldName(SolrResult.class, field));
        	}
        }
        System.out.println(query.toString());
        Map<String, FieldStatsInfo> response = solrDao.executeQuery(query).getFieldStatsInfo();
        Map<String, FieldStatsInfo> resultMap = new HashMap<String, FieldStatsInfo>();
        for (String field : fields) {
            resultMap.put(field, response.get(FieldsMapLoader.getDaoFieldName(SolrResult.class, field)));
        }
        
        return resultMap;
    }

    public SolrServiceResponse<List<Project>> getPropertiesGroupedToProjects(Selector propertyListingSelector) {
        SolrQuery solrQuery = createSolrQuery(propertyListingSelector);
        solrQuery.add("group", "true");
        solrQuery.add("group.ngroups", "true");
        solrQuery.add("group.limit", "-1");
        solrQuery.add("group.field", "PROJECT_ID");

        List<Project> projects = new ArrayList<Project>();
        System.out.println(solrQuery.toString());
        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
        for (GroupCommand groupCommand : queryResponse.getGroupResponse().getValues()) {
            for (Group group : groupCommand.getValues()) {
                List<SolrResult> solrResults = convertSolrResult(group.getResult());
                Project project = solrResults.get(0).getProject();
                
                Set<String> unitTypes = new HashSet<String>();
                List<Property> properties = new ArrayList<Property>();
                Double resalePrice = null;
                for (SolrResult solrResult : solrResults) {
                    Property property = solrResult.getProperty();
                    Double pricePerUnitArea = property.getPricePerUnitArea();
                    Double size = property.getSize();
                    resalePrice = property.getResalePrice();
                    properties.add(property);
                    property.setProject(null);
                    unitTypes.add(property.getUnitType());
                    
                    System.out.println(resalePrice);
                    project.setMinPricePerUnitArea(UtilityClass.min(pricePerUnitArea, project.getMinPricePerUnitArea()));
                    project.setMaxPricePerUnitArea(UtilityClass.max(pricePerUnitArea, project.getMaxPricePerUnitArea()));
                    project.setMinSize(UtilityClass.min(size, project.getMinSize()));
                    project.setMaxSize(UtilityClass.max(size, project.getMaxSize()));
                    project.setMaxBedrooms(Math.max(property.getBedrooms(), project.getMaxBedrooms()));
                    project.addBedrooms(property.getBedrooms());
                    project.setMinResalePrice( UtilityClass.min( resalePrice, project.getMinResalePrice() ) );
                    project.setMaxResalePrice( UtilityClass.max( resalePrice, project.getMaxResalePrice() ) );
                    
                    if (project.getMinBedrooms() == 0) {
                        project.setMinBedrooms(property.getBedrooms());
                    }
                    else if (property.getBedrooms() != 0) {
                        project.setMinBedrooms(Math.min(property.getBedrooms(), project.getMinBedrooms()));
                    }

                    if (pricePerUnitArea != null && size != null) {
                        Double price = pricePerUnitArea * size;
                        project.setMinPrice(UtilityClass.min(price, project.getMinPrice()));
                        project.setMaxPrice(UtilityClass.max(price, project.getMaxPrice()));
                    }
                }
                project.setPropertyUnitTypes(unitTypes);
                project.setProperties(properties);
                projects.add(project);
                
            }
        }

        SolrServiceResponse<List<Project>> solrRes = new SolrServiceResponse<List<Project>>();
        solrRes.setTotalResultCount(queryResponse.getGroupResponse().getValues().get(0).getNGroups());
        solrRes.setResult(projects);

        return solrRes;
    }

    private List<SolrResult> convertSolrResult(SolrDocumentList result) {
        return new DocumentObjectBinder().getBeans(SolrResult.class, result);
    }

    private List<SolrResult> getSolrResultsForProperties(Selector selector) {
        SolrQuery solrQuery = createSolrQuery(selector);
        
        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
        List<SolrResult> solrResults = queryResponse.getBeans(SolrResult.class);
        return solrResults;
    }

    private SolrQuery createSolrQuery(Selector selector) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("*:*");
        solrQuery.addFilterQuery("DOCUMENT_TYPE:PROPERTY");
        
        if (selector != null) {
            Paging paging = selector.getPaging();
            if (paging != null) {
                solrQuery.setRows(paging.getRows());
                solrQuery.setStart(paging.getStart());
            }

//            Set<String> fieldsTemporarityAdded = new HashSet<String>();

//            // XXX - including price, size, bedrooms, unitTypes fields as needed
//            Set<String> fields = selector.getFields();
//            if (fields != null && fields.size() > 0) {
//                if ((fields.contains("maxPricePerUnitArea") || fields.contains("minPricePerUnitArea")) && !fields.contains("pricePerUnitArea")) {
//                    fields.add("pricePerUnitArea");
//                    fieldsTemporarityAdded.add("pricePerUnitArea");
//                }
//
//                if (fields.contains("maxSize") || fields.contains("minSize")) {
//                    fields.add("size");
//                }
//
//                if (fields.contains("minBedrooms") || fields.contains("maxBedrooms")) {
//                    fields.add("bedrooms");
//                }
//
//                if (fields.contains("propertyUnitTypes")) {
//                    fields.add("unitType");
//                }
//            }

            SolrQueryBuilder<SolrResult> queryBuilder = new SolrQueryBuilder<SolrResult>(solrQuery, SolrResult.class);

            if (selector.getSort() == null) {
                selector.setSort(new LinkedHashSet<SortBy>());
            }

            selector.getSort().addAll(getDefaultSort());
            queryBuilder.buildQuery(selector, null);
        }

        return solrQuery;
    }

    private Set<SortBy> getDefaultSort() {
        Set<SortBy> sortBySet = new LinkedHashSet<SortBy>();
        SortBy sortBy = new SortBy();
        sortBy.setField("assignedPriority");
        sortBy.setSortOrder(SortOrder.ASC);
        sortBySet.add(sortBy);

        sortBy = new SortBy();
        sortBy.setField("computedPriority");
        sortBy.setSortOrder(SortOrder.ASC);
        sortBySet.add(sortBy);

        sortBy = new SortBy();
        sortBy.setField("projectId");
        sortBy.setSortOrder(SortOrder.DESC);
        sortBySet.add(sortBy);

        sortBy = new SortBy();
        sortBy.setField("bedrooms");
        sortBy.setSortOrder(SortOrder.ASC);
        sortBySet.add(sortBy);

        sortBy = new SortBy();
        sortBy.setField("size");
        sortBy.setSortOrder(SortOrder.ASC);
        sortBySet.add(sortBy);
        
        return sortBySet;
    }

    public Map<String, Map<String, Integer>> getProjectDistrubtionOnStatusOnBed(Map<String, String> params) {
        SolrQuery solrQuery = new SolrQuery();

        // todo to handle null params or required params not found.
        int bedrooms = Integer.parseInt(params.get("bedroom_upper_limit"));
        String location_type = params.get("location_type").toUpperCase();

        solrQuery.setQuery(location_type + "_ID:" + params.get("location_id"));
        solrQuery.setFilterQueries("DOCUMENT_TYPE:PROPERTY AND BEDROOMS:[1 TO " + bedrooms + "]");
        solrQuery.add("group", "true");
        solrQuery.add("group.facet", "true");
        solrQuery.add("group.field", "PROJECT_ID");
        solrQuery.addFacetField("PROJECT_STATUS_BEDROOM");
        solrQuery.setFacet(true);
        solrQuery.add("wt", "json");

        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);

        return solrResponseReader.getFacetResults(queryResponse.getResponse());
    }

    public Map<String, Map<String, Integer>> getProjectDistrubtionOnStatusOnMaxBed(Map<String, String> params) {
        SolrQuery solrQuery = new SolrQuery();

        // todo to handle null params or required params not found.
        int bedrooms = Integer.parseInt(params.get("bedroom_upper_limit")) + 1;
        String location_type = params.get("location_type").toUpperCase();

        solrQuery.setQuery(location_type + "_ID:" + params.get("location_id"));
        solrQuery.setFilterQueries("DOCUMENT_TYPE:PROPERTY AND BEDROOMS:[" + bedrooms + " TO *]");
        solrQuery.add("group", "true");
        solrQuery.add("group.facet", "true");
        solrQuery.add("group.field", "PROJECT_ID");
        solrQuery.addFacetField("PROJECT_STATUS");
        solrQuery.setFacet(true);
        solrQuery.add("wt", "json");

        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);

        return solrResponseReader.getFacetResults(queryResponse.getResponse());

    }

    public Map<String, Map<String, Integer>> getProjectDistributionOnPrice(Map<String, Object> params) {
        SolrQuery solrQuery = new SolrQuery();

        // todo to handle null params or required params not found.
        String location_type = (String) params.get("location_type");
        location_type = location_type.toUpperCase();
        Double location_id = (Double) params.get("location_id");

        solrQuery.setQuery(location_type + "_ID:" + location_id.intValue());
        solrQuery.setFilterQueries("DOCUMENT_TYPE:PROPERTY AND UNIT_TYPE:Apartment");
        solrQuery.add("group", "true");
        solrQuery.add("group.facet", "true");
        solrQuery.add("group.field", "PROJECT_ID");
        solrQuery.addFacetField("PRICE_PER_UNIT_AREA");
        solrQuery.setFacetMinCount(1);
        solrQuery.setFacetSort("index");
        solrQuery.setFacetLimit(10000000);
        solrQuery.setFacet(true);
        solrQuery.add("wt", "json");

        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);

        return solrResponseReader.getFacetResults(queryResponse.getResponse());
    }
    
    public SolrResult getProperty(long propertyId) {
        SolrQuery solrQuery = createSolrQuery(null);
        solrQuery.addFilterQuery("DOCUMENT_TYPE:PROPERTY AND TYPE_ID:"+propertyId);
        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
        List<SolrResult> properties = queryResponse.getBeans(SolrResult.class);
        try{
            return properties.get(0);
        }catch(Exception e){
            return null;
        }
    }
    
    public List<SolrResult> getSimilarProperties(int distance, Double latitude, Double longitude, double minArea, 
            double maxArea, double minPrice, double maxPrice, String unitType, List<Object> projectStatus,
            int limit, List<Object> propertyIds, Double budget, int projectId){
        SolrQuery solrQuery = createSolrQuery(null);
        
        //TODO to remove the hardcoding the Property class variable Names like geo.
        SolrQueryBuilder<Property> propertySolrQueryBuilder = new SolrQueryBuilder(solrQuery, Property.class);
        
        if(minPrice > 0 && maxPrice > 0)
            propertySolrQueryBuilder.addRangeFilter("budget", minPrice, maxPrice);
        
        if(minArea > 0 && maxArea > 0)
            propertySolrQueryBuilder.addRangeFilter("size", minArea, maxArea);
        if(propertyIds.size() > 0)
        	propertySolrQueryBuilder.addNotEqualsFilter("projectIdBedroom", propertyIds);
        
        SolrQueryBuilder<Property> projectSolrQueryBuilder = new SolrQueryBuilder(solrQuery, Project.class);
        if(latitude != null && longitude != null)
        {
        	projectSolrQueryBuilder.addGeoFilter("geo", distance, latitude, longitude);
        	solrQuery.setSort("geodist()", ORDER.asc);
        }
        projectSolrQueryBuilder.addEqualsFilter("projectStatus", projectStatus);
        
        
        solrQuery.set("group", true);
		solrQuery.set("group.field", "PROJECT_ID_BEDROOM");
		solrQuery.set("group.main", true);
		solrQuery.set("group.limit", 1);
        if(budget != null)
			solrQuery.set("group.sort", "abs(sub(" + budget + ",BUDGET)) asc");
        
        solrQuery.addFilterQuery("DOCUMENT_TYPE:PROPERTY");
        solrQuery.addFilterQuery("UNIT_TYPE:"+unitType);
        solrQuery.setRows(limit);
        solrQuery.addFilterQuery("-PROJECT_ID:"+projectId);
        
        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
        List<SolrResult> properties = queryResponse.getBeans(SolrResult.class);
        
        return properties;
    }
    
    public List<SolrResult> getPropertiesOnProjectId(int projectId) {
        SolrQuery solrQuery = createSolrQuery(null);
        solrQuery.addFilterQuery("PROJECT_ID:" + projectId);
        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
        List<SolrResult> properties = queryResponse.getBeans(SolrResult.class);
        return properties;        
    }
    
    /**
     * This method will take selector Object. This method will get the count of project status and projects
     * locality wise based on the conditions provided in the selector object. This method will take count of 
     * distinct projects. 
     * @param selector
     * @return  
     */
    public Map<String, Map<String, Integer>> getProjectStatusCountAndProjectOnLocalityByCity(Selector selector){
    	SolrQuery solrQuery = createSolrQuery(selector);
    	    	
    	// bug in solr. in case of facet grouping, the negative value will not 
    	// work to get all data. Hence, providing random Max value.
    	solrQuery.setFacetLimit(100000);
    	solrQuery.setFacetMinCount(1);
    	solrQuery.addFacetField("LOCALITY_ID_PROJECT_STATUS");
    	solrQuery.addFacetField("LOCALITY_ID");
    	solrQuery.setFacet(true);
    	solrQuery.setRows(0);
    	
    	solrQuery.add("group", "true");
    	solrQuery.add("group.facet", "true");
    	solrQuery.add("group.field", "PROJECT_ID");
    	System.out.println(solrQuery.toString());
    	QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
    	    	
    	return solrResponseReader.getFacetResults(queryResponse.getResponse());	
    }
    
    /**
     * This method will accept the selector object and return the total number
     * of projects found based on selector conditions.
     * @param selector
     * @return int
     */
    public int getProjectCount(Selector selector){
    	SolrQuery solrQuery = createSolrQuery(selector);
    	
    	solrQuery.setRows(0);
    	
    	solrQuery.add("group", "true");
    	solrQuery.add("group.field", "PROJECT_ID");
    	solrQuery.add("group.ngroups", "true");
    	
    	return solrDao.executeQuery(solrQuery).getGroupResponse().getValues().get(0).getNGroups();	
    }
    
    public Map<String, Map<String, Map<String, FieldStatsInfo>>> getStatsFacetsAsMaps(Selector selector, List<String> fields,
			List<String> facet){
    	
		Map<String, FieldStatsInfo> stats = getStats(fields, selector, facet);
		Map<String, Map<String, Map<String, FieldStatsInfo>>> newStats = new HashMap<>();
		
		String fieldName, facetName;
		for( Map.Entry<String, FieldStatsInfo> entry : stats.entrySet() )
		{
			fieldName = entry.getKey();
			Map<String, Map<String, FieldStatsInfo>> facetsInfo = new HashMap<>();
			
			newStats.put(fieldName, facetsInfo);
			
			if(entry.getValue() == null || entry.getValue().getFacets() == null)
				continue;
						
			for(Map.Entry<String, List<FieldStatsInfo>> e : entry.getValue().getFacets().entrySet() )
			{
				facetName = e.getKey();
				List<FieldStatsInfo> details = e.getValue();
				Map<String, FieldStatsInfo> facetsMap = new HashMap<>();
				for(int i=0; i<details.size(); i++)
				{
					FieldStatsInfo fieldStatsInfo = details.get(i);
					if(fieldStatsInfo.getCount() > 0)
						facetsMap.put( fieldStatsInfo.getName() , fieldStatsInfo);
				}
				facetsInfo.put(facetName, facetsMap);
			}
		}
		
		return newStats;
	}
    
    public Map<String, Map<String, Map<String, FieldStatsInfo>>> getAvgPricePerUnitAreaBHKWise(String locationType, int locationId, String unitType){

		Selector selector = new Selector();

		Map<String, List<Map<String, Map<String, Object>>>> filter = new HashMap<String, List<Map<String,Map<String,Object>>>>();
    	List<Map<String, Map<String, Object>>> list = new ArrayList<>();
    	Map<String, Map<String, Object>> searchType = new HashMap<>();
    	Map<String, Object> filterCriteria = new HashMap<>();
    	
    	filterCriteria.put(locationType, locationId);
    	
    	if(unitType != null)
    		filterCriteria.put("unitType", unitType);
    	searchType.put(Operator.equal.name(), filterCriteria);
    	list.add(searchType);
    	filter.put(Operator.and.name(), list);
    	
    	selector.setFilters(filter);
    	selector.setPaging( new Paging(0, 0) );
    	
    	return getStatsFacetsAsMaps(selector,Arrays.asList("pricePerUnitArea"), Arrays.asList("bedrooms"));
    }
    
    public static void main(String[] args) {
        Selector selector = new Selector();
//        selector.setFilters("{\"and\":[{\"range\":{\"bedrooms\":{\"from\":\"2\",\"to\":\"3\"}}},{\"equal\":{\"bathrooms\":[2]}}]}");
        Set<String> fields = new HashSet<String>();
        fields.add("pricePerUnitArea");
        fields.add("bedrooms");
        // fields.add("unit_name");
        fields.add("unitType");
        selector.setFields(fields);

        Set<SortBy> sort = new HashSet<SortBy>();
        SortBy sortBy1 = new SortBy();
        sortBy1.setField("pricePerUnitArea");
        sortBy1.setSortOrder(SortOrder.ASC);

        SortBy sortBy2 = new SortBy();
        sortBy2.setField("bedrooms");
        sortBy2.setSortOrder(SortOrder.DESC);
        sort.add(sortBy1);
        sort.add(sortBy2);
//        selector.setSort(sort);
        
        Paging paging = new Paging(5,20);
        selector.setPaging(paging);
        ObjectMapper mapper = new ObjectMapper();

//        new PropertyDao().getPropertiesGroupedToProjects(selector);
//        new PropertyDao().getStats(Collections.singletonList("bedrooms"));
        PropertyDao propertyDao = new PropertyDao();
    }
}
