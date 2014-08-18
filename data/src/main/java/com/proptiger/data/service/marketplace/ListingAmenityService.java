package com.proptiger.data.service.marketplace;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.model.Listing;
import com.proptiger.data.model.ListingAmenity;
import com.proptiger.data.repo.marketplace.ListingAmenitiesDao;

/**
 * @author Rajeev Pandey
 *
 */
@Service
public class ListingAmenityService {

    @Autowired
    private ListingAmenitiesDao listingAmenitiesDao;
    
    @Transactional
    public List<ListingAmenity> createListingAmenities(List<ListingAmenity> amenities){
        return listingAmenitiesDao.save(amenities);
    }
    
    public List<ListingAmenity> getListingAmenities(List<Integer> listingIds){
        return listingAmenitiesDao.findByListingIdIn(listingIds);
    }
}
