package com.proptiger.data.service.marketplace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.core.exception.BadRequestException;
import com.proptiger.core.model.cms.AmenityMaster;
import com.proptiger.core.model.cms.Listing;
import com.proptiger.core.model.cms.ListingAmenity;
import com.proptiger.core.model.cms.ProjectCMSAmenity;
import com.proptiger.data.repo.marketplace.ListingAmenitiesDao;
import com.proptiger.data.service.AmenityMasterService;
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

    @Autowired
    private AmenityMasterService  amenityMasterService;

    public List<ListingAmenity> getListingAmenities(List<Integer> listingIds) {
        if(listingIds == null || listingIds.size() == 0){
            return new ArrayList<>();
        }
        return listingAmenitiesDao.findByListingIdIn(listingIds);
    }

    @Transactional
    public List<ListingAmenity> createListingAmenities(Integer projectId, Listing listing) {
        List<ListingAmenity> createdAmenity = new ArrayList<>();
        if (listing.getMasterAmenityIds() != null && listing.getMasterAmenityIds().size() > 0) {

            List<ProjectCMSAmenity> projectAmenities = projectAmenityService.getCMSAmenitiesByProjectIdAndAmenityIds(
                    projectId,
                    listing.getMasterAmenityIds());

            if (projectAmenities.size() < listing.getMasterAmenityIds().size()) {
                List<AmenityMaster> masterAmenities = amenityMasterService
                        .getMasterAmenities(listing.getMasterAmenityIds());
                /*
                 * if any id paased is not available in master amenity database,
                 * throw exception
                 */
                if (masterAmenities.size() != listing.getMasterAmenityIds().size()) {
                    throw new BadRequestException("Invalid amenity ids " + listing.getMasterAmenityIds());
                }
                Map<Integer, Boolean> masterAmenitiesMappedToProject = new HashMap<Integer, Boolean>();
                for (ProjectCMSAmenity projectAmenity : projectAmenities) {
                    masterAmenitiesMappedToProject.put((int) projectAmenity.getAmenityId(), Boolean.TRUE);
                }
                List<ProjectCMSAmenity> projectAmenitiesToCreate = new ArrayList<>();
                for (AmenityMaster amenityMaster : masterAmenities) {
                    if (masterAmenitiesMappedToProject.get(amenityMaster.getAmenityId()) == null) {
                        projectAmenitiesToCreate.add(ProjectCMSAmenity
                                .createUnverifiedProjectCMSAmenity(amenityMaster, projectId));
                    }
                }
                List<ProjectCMSAmenity> unverifiedProjectAmenities = projectAmenityService.createProjectAmenities(projectAmenitiesToCreate);
                projectAmenities.addAll(unverifiedProjectAmenities);
            }

            List<ListingAmenity> amenitiesToCreate = new ArrayList<ListingAmenity>(listing.getMasterAmenityIds().size());
            
            for (ProjectCMSAmenity projectAmenity : projectAmenities) {
                ListingAmenity listingAmenity = new ListingAmenity();
                listingAmenity.setListingId(listing.getId());
                listingAmenity.setProjectAmenityId((int) projectAmenity.getId());
                amenitiesToCreate.add(listingAmenity);
            }

            createdAmenity = listingAmenitiesDao.save(amenitiesToCreate);
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

    public void removeAminities(List<Integer> alreadyPresentListingAminityIds) {
        if (alreadyPresentListingAminityIds != null && !alreadyPresentListingAminityIds.isEmpty()) {
            listingAmenitiesDao.removeByIds(alreadyPresentListingAminityIds);            
        }
    }
}
