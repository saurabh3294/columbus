/**
 * 
 */
package com.proptiger.data.repo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
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

    public List<FacetField> getFacets(List<String> fields) {
        SolrQuery query = createSolrQuery(null);
        for (String field : fields) {
            query.addFacetField(FieldsMapLoader.getDaoFieldName(SolrResult.class, field, Field.class));
        }

        return solrDao.executeQuery(query).getFacetFields();
    }

    public Map<String, FieldStatsInfo> getStats(List<String> fields) {
        SolrQuery query = createSolrQuery(null);
        query.add("stats", "true");

        for (String field : fields) {
            query.add("stats.field", FieldsMapLoader.getDaoFieldName(SolrResult.class, field, Field.class));
        }

        return solrDao.executeQuery(query).getFieldStatsInfo();
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
                Property property = solrResults.get(0).getProperty();
                project.setMaxPricePerUnitArea(property.getPricePerUnitArea());
                project.setMaxSize(property.getSize());
                project.setMinPricePerUnitArea(property.getPricePerUnitArea());
                project.setMinSize(property.getSize());

                Set<String> unitTypes = new HashSet<String>();
                for (SolrResult solrResult : solrResults) {
                    Property propertyLocal = solrResult.getProperty();
                    unitTypes.add(propertyLocal.getUnitType());
                    if (propertyLocal.getPricePerUnitArea() != null) {
                        project.setMaxPricePerUnitArea(Math.max(project.getMaxPricePerUnitArea(),
                                propertyLocal.getPricePerUnitArea()));
                        project.setMinPricePerUnitArea(Math.min(project.getMinPricePerUnitArea(),
                                propertyLocal.getPricePerUnitArea()));
                    }

                    if (propertyLocal.getSize() != null) {
                        project.setMaxSize(Math.max(project.getMaxSize(), propertyLocal.getSize()));
                        project.setMinSize(Math.min(project.getMinSize(), propertyLocal.getSize()));
                    }
                }
                project.setPropertyUnitTypes(unitTypes);
                projects.add(project);
            }
        }

        return projects;
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
            FilterQueryBuilder.applyFilter(queryBuilder, selector.getFilters(), SolrResult.class);
            SortQueryBuilder.applySort(queryBuilder, selector.getSort(), SolrResult.class);
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
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        new PropertyDao().getPropertiesGroupedToProjects(selector);
        new PropertyDao().getStats(Collections.singletonList("bedrooms"));
    }
}
