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
import com.proptiger.data.model.ProjectCMSAmenity;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.marketplace.ListingAmenitiesDao;
import com.proptiger.data.service.ProjectAmenityService;

/**
 * @author Rajeev Pandey
 *
 */
@Service
public class ListingAmenityService {

    @Autowired
    private ListingAmenitiesDao   listingAmenitiesDao;

    @Autowired
    private ProjectAmenityService projectAmenityService;

    public List<ListingAmenity> getListingAmenities(List<Integer> listingIds) {
        return listingAmenitiesDao.findByListingIdIn(listingIds);
    }

    @Transactional
    public List<ListingAmenity> createListingAmenities(Integer projectId, Listing listing) {
        List<ListingAmenity> createdAmenity = new ArrayList<>();
        if (listing.getMasterAmenityIds() != null && listing.getMasterAmenityIds().size() > 0) {

            List<ProjectCMSAmenity> projectAmenities = projectAmenityService.getCMSAmenitiesByProjectIdAndAmenityIds(
                    projectId,
                    listing.getMasterAmenityIds());

            List<ListingAmenity> amenitiesToCreate = new ArrayList<ListingAmenity>(listing.getMasterAmenityIds()
                    .size());
            for (ProjectCMSAmenity projectAmenity : projectAmenities) {
                ListingAmenity listingAmenity = new ListingAmenity();
                listingAmenity.setListingId(listing.getId());
                listingAmenity.setProjectAmenityId((int) projectAmenity.getId());
                amenitiesToCreate.add(listingAmenity);
            }

            createdAmenity = listingAmenitiesDao.save(amenitiesToCreate);
            listing.setListingAmenities(createdAmenity);
        }
        return createdAmenity;
    }

    /**
     * Fetch all listing amenities and set that in corresponding listing object
     * 
     * @param listings
     */
    public void populateListingAmenities(List<Listing> listings, FIQLSelector selector) {
        // if asked for listingAmenities
        if (selector.getFields() != null && selector.getFields().contains("listingAmenities")
                && listings != null
                && listings.size() > 0) {
            List<Integer> listingIds = new ArrayList<>();
            for (Listing l : listings) {
                listingIds.add(l.getId());
            }
            List<ListingAmenity> listingAmenities = getListingAmenities(listingIds);
            if (listingAmenities.size() > 0) {
                Map<Integer, List<ListingAmenity>> listingIdToAmenitiesMap = createListingToAmenitiesMap(listingAmenities);
                for (Listing l : listings) {
                    l.setListingAmenities(listingIdToAmenitiesMap.get(l.getId()));
                }
            }
        }
    }

    private Map<Integer, List<ListingAmenity>> createListingToAmenitiesMap(List<ListingAmenity> listingAmenities) {
        Map<Integer, List<ListingAmenity>> listingIdToAmenitiesMap = new HashMap<>();
        if (listingAmenities != null) {
            for (ListingAmenity la : listingAmenities) {
                if (listingIdToAmenitiesMap.get(la.getListingId()) == null) {
                    listingIdToAmenitiesMap.put(la.getListingId(), new ArrayList<ListingAmenity>());
                }
                listingIdToAmenitiesMap.get(la.getListingId()).add(la);
            }
        }
        return listingIdToAmenitiesMap;
    }
}
