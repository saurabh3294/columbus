/**
 * 
 */
package com.proptiger.data.repo.marketplace;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.marketplace.Lead;

/**
 * @author mandeep
 *
 */
public interface LeadDao extends PagingAndSortingRepository<Lead, Integer> {

}
