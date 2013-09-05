/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import com.proptiger.exception.ProAPIException;
import java.util.Map;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mukand
 */
@Repository
public class GraphDao {
     private static Logger logger = LoggerFactory.getLogger("graph");
     private HttpSolrServer httpSolrServer = new HttpSolrServer("http://localhost:8983/solr/");
     
    public Object getProjectDistrubtionOnStatusOnBed(Map<String, String> params){
        SolrQuery solrQuery = new SolrQuery();
        
        //todo to handle null params or required params not found.
        int bedrooms = Integer.parseInt( params.get("bedroom_upper_limit") );
        solrQuery.setQuery( params.get("location_key")+":"+params.get("location_id") );
        solrQuery.setFilterQueries("BEDROOMS:[1 TO "+bedrooms+"]");
        solrQuery.add("group", "true");
        solrQuery.add("group.facet", "true");
        solrQuery.add("group.field", "PROJECT_ID");
        solrQuery.addFacetField("PROJECT_STATUS_BEDROOM");
        solrQuery.setFacet(true);
        
        try{
            return httpSolrServer.query(solrQuery).getResults();
        }catch(SolrServerException e){
            logger.error("Could not run Solr query", e);
            throw new ProAPIException("Could not run Solr query", e);
        }
        
    }
    
    public Object getProjectDistrubtionOnStatusOnMaxBed(Map<String, String> params){
        SolrQuery solrQuery = new SolrQuery();
        
        //todo to handle null params or required params not found.
        int bedrooms = Integer.parseInt( params.get("bedroom_upper_limit") );
        solrQuery.setQuery( params.get("location_key")+":"+params.get("location_id") );
        solrQuery.setFilterQueries("BEDROOMS:["+bedrooms+" TO *]");
        solrQuery.add("group", "true");
        solrQuery.add("group.facet", "true");
        solrQuery.add("group.field", "PROJECT_ID");
        solrQuery.addFacetField("PROJECT_STATUS");
        solrQuery.setFacet(true);
        
        try{
            return httpSolrServer.query(solrQuery).getResults();
        }catch(SolrServerException e){
            logger.error("Could not run Solr query", e);
            throw new ProAPIException("Could not run Solr query", e);
        }
        
    }
}
