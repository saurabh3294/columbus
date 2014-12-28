package com.proptiger.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.core.model.solr.DynamicSolrIndex;
import com.proptiger.data.repo.DynamicSolrIndexDao;

@Service
public class DynamicSolrIndexService {
	
	@Autowired
	private DynamicSolrIndexDao dynamicSolrIndexDao;
	
	public List<DynamicSolrIndex> getSolrIndexingEventsOnEventId(List<Integer> eventGeneratedIds){
		List<DynamicSolrIndex> dynamicSolrIndexList = dynamicSolrIndexDao.findByEventGeneratedIdIn(eventGeneratedIds);
		
		return dynamicSolrIndexList;
	}
	
	@Transactional
	public List<DynamicSolrIndex> saveIndexing(List<DynamicSolrIndex> list){
		return dynamicSolrIndexDao.save(list);
	}
	
}
