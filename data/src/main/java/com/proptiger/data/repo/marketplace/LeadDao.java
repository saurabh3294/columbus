/**
 * 
 */
package com.proptiger.data.repo.marketplace;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.marketplace.Lead;

/**
 * @author mandeep
 *
 */
public interface LeadDao extends PagingAndSortingRepository<Integer, Lead> {
    public List<Lead> findByclientId(int Id);
}
