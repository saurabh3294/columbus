/**
 * 
 */
package com.proptiger.data.repo;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.Property;
import com.proptiger.data.model.SolrResult;
import com.proptiger.data.model.filter.FieldsQueryBuilder;
import com.proptiger.data.model.filter.FilterQueryBuilder;
import com.proptiger.data.model.filter.PropertyFilter;
import com.proptiger.data.model.filter.SolrQueryBuilder;
import com.proptiger.data.model.filter.SortQueryBuilder;

/**
 * @author mandeep
 * 
 */
@Repository
public class PropertyDao {
    private HttpSolrServer httpSolrServer = new HttpSolrServer("http://www.proptiger.com:8983/solr/");

    public List<Property> getProperties(PropertyFilter propertyFilter) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("*:*");
        solrQuery.add("facet", "true");
        solrQuery.add("facet.field", "CITY");
        solrQuery.setRows(10);

        SolrQueryBuilder queryBuilder = new SolrQueryBuilder(solrQuery);
        FilterQueryBuilder.applyFilter(queryBuilder, propertyFilter.getFilters(), Property.class);
        SortQueryBuilder.applySort(queryBuilder, propertyFilter.getSort(), Property.class);
        FieldsQueryBuilder.applyFields(queryBuilder, propertyFilter.getFields(), Property.class);

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
            throw new RuntimeException("Could not run query", e);
        }
    }
    
    public static void main(String[] args) {
        PropertyFilter propertyFilter = new PropertyFilter();
        propertyFilter.setFilters("{\"and\":[{\"range\":{\"bedrooms\":{\"from\":\"2\",\"to\":\"3\"}}},{\"equal\":{\"bathrooms\":[2]}}]}");
        propertyFilter.setFields("price_per_unit_area,bedrooms,unit_name,unit_type");
        propertyFilter.setSort("[{\"price_per_unit_area\" : \"asc\"}, {\"bedrooms\" : \"desc\"}]");
        new PropertyDao().getProperties(propertyFilter);
    }
}
