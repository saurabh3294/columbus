/**
 * 
 */
package com.proptiger.data.repo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.filter.FieldsQueryBuilder;
import com.proptiger.data.model.filter.FilterQueryBuilder;
import com.proptiger.data.model.filter.PropertyRequestParams;
import com.proptiger.data.model.filter.SolrQueryBuilder;
import com.proptiger.data.model.filter.SortBy;
import com.proptiger.data.model.filter.SortOrder;
import com.proptiger.data.model.filter.SortQueryBuilder;
import com.proptiger.exception.ProAPIException;

/**
 * @author mandeep
 * 
 */
@Repository
public class PropertyDao {
	private static Logger logger = LoggerFactory.getLogger("property");
	
    private HttpSolrServer httpSolrServer = new HttpSolrServer("http://localhost:8983/solr/");

    public List<Property> getProperties(PropertyRequestParams propertyRequestParams) {
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

        try {
            QueryResponse queryResponse = httpSolrServer.query(solrQuery);
            return queryResponse.getBeans(Property.class);
//            List<SolrResult> solrResults = queryResponse.getBeans(SolrResult.class);
//            List<Property> properties = new ArrayList<Property>();
//            for (SolrResult solrResult : solrResults) {
//                properties.add(solrResult.getProperty());
//            }
//            return properties;
        } catch (SolrServerException e) {
        	logger.error("Could not run Solr query", e);
            throw new ProAPIException("Could not run Solr query", e);
        }
    }
    
	public static void main(String[] args) {
		PropertyRequestParams propertyFilter = new PropertyRequestParams();
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
