package com.proptiger.data.service.marketplace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public List<ListingAmenity> getListingAmenities(List<Integer> listingIds) {
        return listingAmenitiesDao.findByListingIdIn(listingIds);
    }

    @Transactional
    public List<ListingAmenity> createListingAmenities(Listing listing) {
        List<ListingAmenity> createdAmenity = new ArrayList<>();
        if(listing.getListingAmenities() != null && listing.getListingAmenities().size() > 0){
            List<ListingAmenity> amenities = new ArrayList<ListingAmenity>(listing.getListingAmenities().size());
            Map<Integer, Boolean> amenitiesAddedForCreate = new HashMap<>();
            for (ListingAmenity la : listing.getListingAmenities()) {
                if(amenitiesAddedForCreate.get(la.getProjectAmenityId()) == null){
                    ListingAmenity listingAmenity = new ListingAmenity();
                    listingAmenity.setListingId(listing.getId());
                    listingAmenity.setProjectAmenityId(la.getProjectAmenityId());
                    amenities.add(listingAmenity);
                    amenitiesAddedForCreate.put(la.getProjectAmenityId(), Boolean.TRUE);
                }
                
            }
            createdAmenity = listingAmenitiesDao.save(amenities);
            listing.setListingAmenities(createdAmenity);
        }
        return createdAmenity;
    }
}
