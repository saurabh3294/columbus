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
import org.apache.solr.client.solrj.response.QueryResponse;
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
     private SolrDao solrDao = new SolrDao();
         
    public Object getEnquiryDistributionOnLocality(Map<String, String> params){
        return new Object();
    }
    
    
}
