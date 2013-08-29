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
    private HttpSolrServer httpSolrServer;
    
    public PropertyDao(){
        try{
            httpSolrServer = new HttpSolrServer("http://localhost:8983/solr/");
        }catch(Exception e){
            //TODO
        }
    }
    
    public HttpSolrServer getServerObject()
    {
        return httpSolrServer;
    }
    public Object getProperties() throws SolrServerException {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("*:*");
        solrQuery.add("facet","true");
        solrQuery.add("facet.field","CITY");
        solrQuery.setRows(10);
        
        return httpSolrServer.query(solrQuery).getBeans(Property.class);
        
    }
}
