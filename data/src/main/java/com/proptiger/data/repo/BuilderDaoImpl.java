package com.proptiger.data.repo;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.Builder;
import com.proptiger.data.model.enums.DocumentType;
import com.proptiger.data.pojo.Selector;

/**
 * @author mukand
 * @author Rajeev Pandey
 *
 */
@Repository
public class BuilderDaoImpl {
	
	private static Logger logger = LoggerFactory.getLogger(BuilderDaoImpl.class);
	
	@Autowired
	private SolrDao solrDao;

	public Builder getBuilderById(int builderId){
		SolrQuery solrQuery = SolrDao.createSolrQuery(DocumentType.BUILDER);
		solrQuery.addFilterQuery("BUILDER_ID:"+builderId);
		
		logger.debug("Solr query for builder by id {}",solrQuery.toString());
		QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
		List<Builder> builders = queryResponse.getBeans(Builder.class);
		
		if(builders.size() > 0)
			return builders.get(0);
		
		return null;
	}

	/**
	 * Get popular builders
	 * @param selector
	 * @return
	 */
	public List<Builder> getPopularBuilders(Selector selector){
		SolrQuery solrQuery = SolrDao.createSolrQuery(DocumentType.BUILDER);
		return null;
	}
	
}
