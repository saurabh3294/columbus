package com.proptiger.data.repo;

import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.Builder;

@Repository
public class BuilderDaoImpl {
	@Autowired
	private SolrDao solrDao;

	Builder getBuilderById(int builderId){
		SolrQuery solrQuery = new SolrQuery();
		
		solrQuery.setQuery("*:*");
		solrQuery.addFilterQuery("DOCUMENT_TYPE:BUILDER");
		solrQuery.addFilterQuery("BUILDER_ID:"+builderId);
		
		System.out.println(solrQuery.toString());
		
		QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
		List<Builder> builders = queryResponse.getBeans(Builder.class);
		
		if(builders.size() > 0)
			return builders.get(0);
		
		return null;
	}
}
