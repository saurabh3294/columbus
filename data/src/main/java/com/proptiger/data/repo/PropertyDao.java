/**
 * 
 */
package com.proptiger.data.repo;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.Property;
import com.proptiger.data.model.filter.FilterQueryBuilder;
import com.proptiger.data.model.filter.PropertyFilter;
import com.proptiger.data.model.filter.SolrQueryBuilder;
import com.proptiger.data.mvc.PropertyController;

/**
 * @author mandeep
 * 
 */
@Repository
public class PropertyDao {
    private HttpSolrServer httpSolrServer;

    public PropertyDao() {
        try {
            httpSolrServer = new HttpSolrServer("http://www.proptiger.com:8983/solr/");
        } catch (Exception e) {
            // TODO
        }
    }

    public HttpSolrServer getServerObject() {
        return httpSolrServer;
    }

    public List<Property> getProperties(PropertyFilter propertyFilter) throws SolrServerException {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("*:*");
        solrQuery.add("facet", "true");
        solrQuery.add("facet.field", "CITY");
        solrQuery.setRows(10);

        FilterQueryBuilder.applyFilter(new SolrQueryBuilder(solrQuery), propertyFilter.getFilters(), Property.class);
        return httpSolrServer.query(solrQuery).getBeans(Property.class);
    }
    
    public static void main(String[] args) throws SolrServerException {
        PropertyFilter propertyFilter = new PropertyFilter();
        propertyFilter.setFilters("{\"and\":[{\"range\":{\"bedrooms\":{\"from\":\"2\",\"to\":\"3\"}}},{\"equal\":{\"bathrooms\":[2]}}]}");
        new PropertyDao().getProperties(propertyFilter);
    }
}
