package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.core.model.solr.DynamicSolrIndex;

public interface DynamicSolrIndexDao extends
		JpaRepository<DynamicSolrIndex, Integer> {

	public List<DynamicSolrIndex> findByEventGeneratedIdIn(
			List<Integer> eventGeneratedIds);
}
