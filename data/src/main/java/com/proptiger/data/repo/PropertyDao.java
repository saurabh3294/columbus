/**
 * 
 */
package com.proptiger.data.repo;

import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.Property;

/**
 * @author mandeep
 *
 */
@Repository
public interface PropertyDao extends SolrCrudRepository<Property, String> {
}
