package com.proptiger.data.repo.marketplace;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.ListingAmenity;

/**
 * @author Rajeev Pandey
 *
 */
public interface ListingAmenitiesDao extends JpaRepository<ListingAmenity, Integer>{

    List<ListingAmenity> findByListingIdIn(List<Integer> id);

}
