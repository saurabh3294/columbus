package com.proptiger.data.repo;

import javax.annotation.PostConstruct;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpClientUtil;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.data.util.PropertyReader;
import com.proptiger.exception.ProAPIException;

/**
 * Solr dao provides method to execute solr query.
 * 
 * @author Rajeev Pandey
 * 
 */
@Component
public class SolrDao {
    private static Logger logger = LoggerFactory.getLogger(SolrDao.class);

    @Autowired
    protected PropertyReader propertyReader;

    private HttpSolrServer httpSolrServer;

    @PostConstruct
    private void init() {
        httpSolrServer = new HttpSolrServer(propertyReader.getRequiredProperty("solr.server.url"));
        HttpClientUtil.setBasicAuth((DefaultHttpClient) httpSolrServer.getHttpClient(), propertyReader.getRequiredProperty("solr.server.username"), propertyReader.getRequiredProperty("solr.server.password"));
    }

    /**
     * This method takes a SolrQuery and execute that, and if any exception
     * occures then it wrapps that exception in ProAPIException and throw back
     * to the caller.
     * 
     * @param query
     * @return
     */
    protected QueryResponse executeQuery(SolrQuery query) {
        try {
            return httpSolrServer.query(query);
        } catch (Exception e) {
            logger.error("Could not run Solr query", e);
            throw new ProAPIException("Could not run Solr query", e);
        }
    }
}
