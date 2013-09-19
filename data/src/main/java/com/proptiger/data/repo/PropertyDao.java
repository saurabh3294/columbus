/**
 * 
 */
package com.proptiger.data.repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.beans.Field;
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
import com.proptiger.data.model.filter.FieldsQueryBuilder;
import com.proptiger.data.model.filter.FilterQueryBuilder;
import com.proptiger.data.model.filter.SolrQueryBuilder;
import com.proptiger.data.model.filter.SortQueryBuilder;
import com.proptiger.data.pojo.Paging;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.SortBy;
import com.proptiger.data.pojo.SortOrder;
import com.proptiger.data.util.SolrResponseReader;

/**
 * @author mandeep
 * 
 */
@Repository
public class PropertyDao {
    @Autowired
    private FilterQueryBuilder filterQueryBuilder;

    @Autowired
    private SolrDao solrDao;

    // to make it autowired.
    private SolrResponseReader solrResponseReader = new SolrResponseReader();

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
        for (String field : fields) {
            query.addFacetField(FieldsMapLoader.getDaoFieldName(SolrResult.class, field, Field.class));
        }

        Map<String, List<Map<Object, Long>>> resultMap = new HashMap<String, List<Map<Object, Long>>>();
        for (String field : fields) {
            resultMap.put(field, new ArrayList<Map<Object, Long>>());
            for (Count count : solrDao.executeQuery(query).getFacetField(FieldsMapLoader.getDaoFieldName(SolrResult.class, field, Field.class)).getValues()) {
                HashMap<Object, Long> map = new HashMap<Object, Long>();
                map.put(count.getName(), count.getCount());
                resultMap.get(field).add(map);
            }
        }

        return resultMap;
    }

    public Map<String, FieldStatsInfo> getStats(List<String> fields, Selector propertySelector) {
        SolrQuery query = createSolrQuery(propertySelector);
        query.add("stats", "true");

        for (String field : fields) {
            query.add("stats.field", FieldsMapLoader.getDaoFieldName(SolrResult.class, field, Field.class));
        }

        Map<String, FieldStatsInfo> response = solrDao.executeQuery(query).getFieldStatsInfo();
        Map<String, FieldStatsInfo> resultMap = new HashMap<String, FieldStatsInfo>();
        for (String field : fields) {
            resultMap.put(field, response.get(FieldsMapLoader.getDaoFieldName(SolrResult.class, field, Field.class)));
        }
        
        return resultMap;
    }

    public List<Project> getPropertiesGroupedToProjects(Selector propertyListingSelector) {
        SolrQuery solrQuery = createSolrQuery(propertyListingSelector);
        solrQuery.add("group", "true");
        solrQuery.add("group.field", "PROJECT_ID");

        List<Project> projects = new ArrayList<Project>();

        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
        for (GroupCommand groupCommand : queryResponse.getGroupResponse().getValues()) {
            for (Group group : groupCommand.getValues()) {
                List<SolrResult> solrResults = convertSolrResult(group.getResult());
                Project project = solrResults.get(0).getProject();

                Set<String> unitTypes = new HashSet<String>();
                for (SolrResult solrResult : solrResults) {
                    Property property = solrResult.getProperty();
                    Double pricePerUnitArea = property.getPricePerUnitArea();
                    Double size = property.getSize();
                    unitTypes.add(property.getUnitType());

                    project.setMinPricePerUnitArea(min(pricePerUnitArea, project.getMinPricePerUnitArea()));
                    project.setMaxPricePerUnitArea(max(pricePerUnitArea, project.getMaxPricePerUnitArea()));
                    project.setMinSize(min(size, project.getMinSize()));
                    project.setMaxSize(max(size, project.getMaxSize()));

                    if (pricePerUnitArea != null && size != null) {
                        Double price = pricePerUnitArea * size;
                        project.setMinPrice(min(price, project.getMinPrice()));
                        project.setMaxPrice(max(price, project.getMaxPrice()));
                    }
                }

                project.setPropertyUnitTypes(unitTypes);
                projects.add(project);
            }
        }

        return projects;
    }

    /**
     * Returns non zero max of given 2 numbers - null otherwise
     * @param a
     * @param b
     * @return
     */
    private Double max(Double a, Double b) {
        Double c = a;
        if (a == null || a == 0) {
            c = b;
        }
        else {
            if (b != null && b != 0) {
                c = Math.max(a, b);
            }
        }

        if (c != null && c == 0) {
            c = null;
        }

        return c;
    }

    /**
     * Returns non zero min of given 2 numbers - null otherwise
     * @param a
     * @param b
     * @return
     */
    private Double min(Double a, Double b) {
        Double c = a;
        if (a == null || a == 0) {
            c = b;
        }
        else {
            if (b != null && b != 0) {
                c = Math.min(a, b);
            }
        }

        if (c != null && c == 0) {
            c = null;
        }

        return c;
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

            SolrQueryBuilder queryBuilder = new SolrQueryBuilder(solrQuery);
            filterQueryBuilder.applyFilter(queryBuilder, selector.getFilters(), SolrResult.class);
            SortQueryBuilder.applySort(queryBuilder, selector.getSort(), SolrResult.class);
            
            // Current default relebanr order
            queryBuilder.addSort("DISPLAY_ORDER", SortOrder.ASC);
            queryBuilder.addSort("PROJECT_PRIORITY", SortOrder.ASC);
            queryBuilder.addSort("PROJECT_ID", SortOrder.DESC);
            queryBuilder.addSort("BEDROOMS", SortOrder.ASC);
            queryBuilder.addSort("SIZE", SortOrder.ASC);
            
            // XXX - including price and size fields
            if (selector.getFields() != null && selector.getFields().size() > 0) {
                if (selector.getFields().contains("maxPricePerUnitArea") || selector.getFields().contains("minPricePerUnitArea")) {
                    selector.getFields().add("pricePerUnitArea");
                }
                if (selector.getFields().contains("maxSize") || selector.getFields().contains("minSize")) {
                    selector.getFields().add("size");
                }
            }
            FieldsQueryBuilder.applyFields(queryBuilder, selector.getFields(), SolrResult.class);
        }

        return solrQuery;
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

    public static void main(String[] args) {
        Selector selector = new Selector();
        selector.setFilters("{\"and\":[{\"range\":{\"bedrooms\":{\"from\":\"2\",\"to\":\"3\"}}},{\"equal\":{\"bathrooms\":[2]}}]}");
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
        selector.setSort(sort);
        ObjectMapper mapper = new ObjectMapper();

        try {
            System.out.println(mapper.writeValueAsString(selector));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        new PropertyDao().getPropertiesGroupedToProjects(selector);
//        new PropertyDao().getStats(Collections.singletonList("bedrooms"));
        PropertyDao propertyDao = new PropertyDao();
        System.out.println(propertyDao.min(null, 76.9));
        System.out.println(propertyDao.min(87.9, 76.9));
        System.out.println(propertyDao.min(65.9, 76.9));
        System.out.println(propertyDao.min(null, null));
        System.out.println(propertyDao.min(null, 0.0));
        System.out.println(propertyDao.min(0.0, null));
        System.out.println(propertyDao.min(0.0, 0.0));
    }
}
