/**
 * 
 */
package com.proptiger.data.repo;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.Property;
import com.proptiger.data.model.filter.FilterQueryBuilder;
import com.proptiger.data.model.filter.PropertyFilter;
import com.proptiger.data.model.filter.SolrQueryBuilder;

/**
 * @author mandeep
 * 
 */
@Repository
public class PropertyDao {
    private HttpSolrServer httpSolrServer;

    public PropertyDao() {
        try {
            httpSolrServer = new HttpSolrServer("http://localhost:8983/solr/");
        } catch (Exception e) {
            // TODO
        }
    }

    public HttpSolrServer getServerObject() {
        return httpSolrServer;
    }

    public Object getProperties(PropertyFilter propertyFilter) throws SolrServerException {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("*:*");
        solrQuery.add("facet", "true");
        solrQuery.add("facet.field", "CITY");
        solrQuery.setRows(10);

        new FilterQueryBuilder<Property, SolrQueryBuilder>().applyFilter(new SolrQueryBuilder(solrQuery),
                propertyFilter.getFilters());

        return httpSolrServer.query(solrQuery).getBeans(Property.class);

    }
}
