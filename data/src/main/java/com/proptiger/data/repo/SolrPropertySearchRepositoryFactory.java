/**
 * 
 */
package com.proptiger.data.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.repository.support.SolrRepositoryFactory;

/**
 * @author mandeep
 *
 */
public class SolrPropertySearchRepositoryFactory {
    @Autowired
    private SolrOperations solrOperations;

    public SolrPropertyRepository create() {
      return new SolrRepositoryFactory(this.solrOperations).getRepository(SolrPropertyRepository.class);
    }
}
