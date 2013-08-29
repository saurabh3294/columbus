/**
 * 
 */
package com.proptiger.data.repo;

import com.proptiger.data.model.Project;
import com.proptiger.data.model.Property;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

/**
 * @author mandeep
 *
 */
public class PropertyDao {
    private HttpSolrServer httpSolrServer = new HttpSolrServer("http://localhost:8983/solr/");
    
    public Object getProperties() throws SolrServerException {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("*:*");
        
        return httpSolrServer.query(solrQuery).getBeans(Property.class);
        
    }
}
