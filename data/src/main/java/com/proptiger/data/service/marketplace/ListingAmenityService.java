package com.proptiger.data.service.marketplace;

import java.util.ArrayList;
import java.util.List;

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
    public List<ListingAmenity> getListingAmenitiesOfListings(List<Listing> listings) {
        List<Integer> listingIds = new ArrayList<>();
        for (Listing l : listings) {
            listingIds.add(l.getId());
        }
        return getListingAmenities(listingIds);
    }
}
