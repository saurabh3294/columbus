package com.proptiger.data.repo.marketplace;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.Listing;

/**
 * @author Rajeev Pandey
 *
 */
public interface ListingDao extends JpaRepository<Listing, Integer>{

}
