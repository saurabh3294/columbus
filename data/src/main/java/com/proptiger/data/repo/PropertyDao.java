/**
 * 
 */
package com.proptiger.data.repo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.filter.FieldsQueryBuilder;
import com.proptiger.data.model.filter.FilterQueryBuilder;
import com.proptiger.data.model.filter.SolrQueryBuilder;
import com.proptiger.data.model.filter.SortQueryBuilder;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.SortBy;
import com.proptiger.data.pojo.SortOrder;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.data.util.SolrResponseReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;

/**
 * @author mandeep
 * 
 */
@Repository
public class PropertyDao extends SolrDao{
	
	@Autowired
	private PropertyReader propertyReader;
        
        private SolrResponseReader solrResponseReader = new SolrResponseReader();

	private static Logger logger = LoggerFactory.getLogger("property");

    public List<Property> getProperties(Selector propertyRequestParams) {
    	SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("*:*");
        solrQuery.add("facet", "true");
        solrQuery.add("facet.field", "CITY");
        solrQuery.addFilterQuery("DOCUMENT_TYPE:PROPERTY");
        solrQuery.setRows(propertyRequestParams.getRows());
        solrQuery.setStart(propertyRequestParams.getStart());

        SolrQueryBuilder queryBuilder = new SolrQueryBuilder(solrQuery);
        FilterQueryBuilder.applyFilter(queryBuilder, propertyRequestParams.getFilters(), Property.class);
        SortQueryBuilder.applySort(queryBuilder, propertyRequestParams.getSort(), Property.class);
        FieldsQueryBuilder.applyFields(queryBuilder, propertyRequestParams.getFields(), Property.class);

		QueryResponse queryResponse = executeQuery(solrQuery);
		return queryResponse.getBeans(Property.class);
		// List<SolrResult> solrResults =
		// queryResponse.getBeans(SolrResult.class);
		// List<Property> properties = new ArrayList<Property>();
		// for (SolrResult solrResult : solrResults) {
		// properties.add(solrResult.getProperty());
		// }
		// return properties;
    }
    public HashMap<String, HashMap<String, Integer>> getProjectDistrubtionOnStatusOnBed(Map<String, String> params){
        SolrQuery solrQuery = new SolrQuery();
        
        //todo to handle null params or required params not found.
        int bedrooms = Integer.parseInt( params.get("bedroom_upper_limit") );
        String location_type = params.get("location_type").toUpperCase();
        
        solrQuery.setQuery( location_type+"_ID:"+params.get("location_id") );
        solrQuery.setFilterQueries("DOCUMENT_TYPE:PROPERTY AND BEDROOMS:[1 TO "+bedrooms+"]");
        solrQuery.add("group", "true");
        solrQuery.add("group.facet", "true");
        solrQuery.add("group.field", "PROJECT_ID");
        solrQuery.addFacetField("PROJECT_STATUS_BEDROOM");
        solrQuery.setFacet(true);
        solrQuery.add("wt","json");
        System.out.println(solrQuery.toString());
        QueryResponse queryResponse = executeQuery(solrQuery);
        
        return solrResponseReader.getFacetResults(queryResponse.getResponse());
    }
    
    public HashMap<String, HashMap<String, Integer>> getProjectDistrubtionOnStatusOnMaxBed(Map<String, String> params){
        SolrQuery solrQuery = new SolrQuery();
        
        //todo to handle null params or required params not found.
        int bedrooms = Integer.parseInt( params.get("bedroom_upper_limit") );
        String location_type = params.get("location_type").toUpperCase();
        
        solrQuery.setQuery( location_type+":"+params.get("location_id") );
        solrQuery.setFilterQueries("DOCUMENT_TYPE:PROPERTY AND BEDROOMS:["+bedrooms+" TO *]");
        solrQuery.add("group", "true");
        solrQuery.add("group.facet", "true");
        solrQuery.add("group.field", "PROJECT_ID");
        solrQuery.addFacetField("PROJECT_STATUS");
        solrQuery.setFacet(true);
        solrQuery.add("wt","json");
        
        QueryResponse queryResponse = executeQuery(solrQuery);
        
        return solrResponseReader.getFacetResults(queryResponse.getResponse());
        
    }
    public Object getProjectDistributionOnPrice(Map<String, Map<String, String>> params){
        SolrQuery solrQuery = new SolrQuery();
        
        //todo to handle null params or required params not found.
        //String location_type = params.get("location_type").toUpperCase();
        
        solrQuery.setQuery( params.get("location_type")+":"+params.get("location_id") );
        solrQuery.setFilterQueries("DOCUMENT_TYPE:PROPERTY AND UNIT_TYPE:Apartment");
        solrQuery.add("group", "true");
        solrQuery.add("group.facet", "true");
        solrQuery.add("group.field", "PROJECT_ID");
        solrQuery.addFacetField("PRICE_PER_UNIT_AREA");
        solrQuery.setFacetMinCount(1);
        solrQuery.setFacetSort("index");
        solrQuery.setFacetLimit(10000000);
        solrQuery.setFacet(true);
        
        QueryResponse queryResponse = executeQuery(solrQuery);
        
        return queryResponse.getResults();
    }
    
    public static void main(String[] args) {
		Selector propertyFilter = new Selector();
		propertyFilter
				.setFilters("{\"and\":[{\"range\":{\"bedrooms\":{\"from\":\"2\",\"to\":\"3\"}}},{\"equal\":{\"bathrooms\":[2]}}]}");
		Set<String> fields = new HashSet<String>();
		fields.add("price_per_unit_area");
		fields.add("bedrooms");
		fields.add("unit_name");
		fields.add("unit_type");
		propertyFilter.setFields(fields);

		Set<SortBy> sort = new HashSet<SortBy>();
		SortBy sortBy1 = new SortBy();
		sortBy1.setField("price_per_unit_area");
		sortBy1.setSortOrder(SortOrder.ASC);

		SortBy sortBy2 = new SortBy();
		sortBy2.setField("bedrooms");
		sortBy2.setSortOrder(SortOrder.DESC);
		sort.add(sortBy1);
		sort.add(sortBy2);
		propertyFilter.setSort(sort);
		ObjectMapper mapper = new ObjectMapper();

		try {
			System.out.println(mapper.writeValueAsString(propertyFilter));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new PropertyDao().getProperties(propertyFilter);
	}
}
