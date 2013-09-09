package com.proptiger.data.repo;

import javax.annotation.PostConstruct;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.data.util.PropertyReader;
import com.proptiger.exception.ProAPIException;

/**
 * Every solr related Dao should extend this class to execute solr query.
 * @author Rajeev Pandey
 *
 */
public class SolrDao {

	private static Logger logger = LoggerFactory.getLogger(SolrDao.class);
	@Autowired
	protected PropertyReader propertyReader;
	
	private HttpSolrServer httpSolrServer = new HttpSolrServer("http://localhost:8983/solr/");
	
	@PostConstruct
	private void init(){
		//httpSolrServer = new HttpSolrServer(propertyReader.getRequiredProperty("solr.server.url"));
	}
	/**
	 * This method takes a SolrQuery and execute that, and if any exception occures then it wrapps
	 * that exception in  ProAPIException and throw back to the caller. 
	 * @param query
	 * @return
	 */
	protected QueryResponse executeQuery(SolrQuery query){
		try {
                        System.out.println("*****************");
			return httpSolrServer.query(query);
		} catch (Exception e) {
                    System.out.println("Execption: "+e.getMessage());
                    System.out.println("error in running query");
			logger.error("Could not run Solr query", e);
            throw new ProAPIException("Could not run Solr query", e);
		}
	}
}
