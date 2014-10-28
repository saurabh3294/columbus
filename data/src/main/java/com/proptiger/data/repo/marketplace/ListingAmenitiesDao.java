package com.proptiger.data.repo.marketplace;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.core.model.cms.ListingAmenity;

/**
 * @author Rajeev Pandey
 * 
 */
public interface ListingAmenitiesDao extends JpaRepository<ListingAmenity, Integer> {

    @Query("select LA from ListingAmenity LA join fetch LA.amenity LAA join fetch LAA.amenityMaster where LA.listingId in ?1")
    List<ListingAmenity> findByListingIdIn(List<Integer> id);

    @Modifying
    @Transactional
    @Query("delete from ListingAmenity LA where LA.id in (?1)")
    void removeByIds(List<Integer> alreadyPresentListingAminityIds);
}
