package com.proptiger.data.service.marketplace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.enums.DataVersion;
import com.proptiger.data.enums.EntityType;
import com.proptiger.data.enums.ListingCategory;
import com.proptiger.data.enums.Status;
import com.proptiger.data.enums.resource.ResourceType;
import com.proptiger.data.enums.resource.ResourceTypeAction;
import com.proptiger.data.model.Listing;
import com.proptiger.data.model.ListingAmenity;
import com.proptiger.data.model.ListingPrice;
import com.proptiger.data.model.ProjectPhase;
import com.proptiger.data.model.Property;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.marketplace.ListingDao;
import com.proptiger.data.service.ProjectPhaseService;
import com.proptiger.data.service.PropertyService;
import com.proptiger.exception.BadRequestException;
import com.proptiger.exception.ResourceAlreadyExistException;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 * @author Rajeev Pandey
 *
 */
@Service
public class ListingService {
    private static Logger         logger = LoggerFactory.getLogger(ListingService.class);
    @Autowired
    private PropertyService       propertyService;

    @Autowired
    private ListingPriceService   listingPriceService;

    @Autowired
    private ListingDao            listingDao;

    @Autowired
    private ProjectPhaseService   projectPhaseService;

    @Autowired
    private ListingAmenityService listingAmenityService;
    /**
     * Create a new listing, apply some validations before create.
     * 
     * @param listing
     * @param userId
     * @return
     */
    @Transactional
    public Listing createListing(Listing listing, Integer userId) {
        preCreateValidation(listing, userId);
        Listing created = null;
        try {
            created = listingDao.saveAndFlush(listing);
        }
        catch (PersistenceException e) {
            logger.error("error while creating listing {}", e);
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new ResourceAlreadyExistException("Listing already exists");
            }
            throw new ResourceAlreadyExistException("Listing could not be created");
        }
        if (listing.getCurrentListingPrice() != null) {
            ListingPrice listingPriceCreated = listingPriceService.createListingPrice(
                    listing.getCurrentListingPrice(),
                    listing);

            // save listing again with current listing price id
            created.setCurrentPriceId(listingPriceCreated.getId());
            created = listingDao.saveAndFlush(created);

            created.setCurrentListingPrice(listingPriceCreated);
        }

        if (listing.getListingAmenities() != null && listing.getListingAmenities().size() > 0) {
            List<ListingAmenity> amenities = new ArrayList<ListingAmenity>(listing.getListingAmenities().size());
            for (ListingAmenity la : listing.getListingAmenities()) {
                ListingAmenity listingAmenity = new ListingAmenity();
                listingAmenity.setListingId(listing.getId());
                listingAmenity.setProjectAmenityId(la.getProjectAmenityId());
                amenities.add(listingAmenity);
            }
            List<ListingAmenity> createdAmenity = listingAmenityService.createListingAmenities(amenities);
            listing.setListingAmenities(createdAmenity);
        }
        return created;
    }

    /**
     * Validate Listing data before creation
     * 
     * @param listing
     * @param userId
     */
    private void preCreateValidation(Listing listing, Integer userId) {
        // only no phase supported as of now
        listing.setPhaseId(null);
        if (listing.getPropertyId() == null) {
            // TODO create option with verified flag as false and set that id,
            // first find option based on data in other info

            // throwing exception as of now
            throw new BadRequestException("Property Id is mandatory");
        }
        Property property = propertyService.getProperty(listing.getPropertyId());
        if (listing.getPhaseId() == null) {
            // add Logical phase id
            List<ProjectPhase> projectPhase = projectPhaseService.getPhaseDetailsFromFiql(
                    new FIQLSelector().addAndConditionToFilter("phaseType==" + EntityType.Logical),
                    property.getProjectId(),
                    DataVersion.Website);
            if (projectPhase != null && projectPhase.size() > 0) {
                listing.setPhaseId(projectPhase.get(0).getPhaseId());
            }
            else {
                throw new BadRequestException("Logical phase not found for project id " + property.getProjectId());
            }
        }
        else {
            // no validation for invalidation phase id
        }

        if (listing.getTowerId() != null) {
            // check if tower id exists, else exception
        }

        if (listing.getFloor() < 0) {
            throw new BadRequestException("Invalid floor");
        }
        validateListingCategory(listing);
        // TODO need to update agent id in updatedBy field
        listing.setUpdatedBy(userId);
        listing.setSellerId(userId);
        listing.setBookingStatusId(null);
        listing.setStatus(Status.Active);
    }

    /**
     * Primary listing creation not allowed
     * 
     * @param listingCategory
     */
    private void validateListingCategory(Listing listing) {
        if (listing.getListingCategory() == null) {
            // default listing category to be resale
            listing.setListingCategory(ListingCategory.Resale);
        }
        else if (listing.getListingCategory() == ListingCategory.Primary) {
            throw new BadRequestException("Primary listing category not allowed");
        }
    }

    /**
     * Get all active listing of user
     * 
     * @param userId
     * @return
     */
    public List<Listing> getListings(Integer userId, FIQLSelector selector) {
        List<Listing> listings = listingDao.findListings(userId, DataVersion.Website, Status.Active);
        if (listings.size() > 0) {
            List<Integer> listingPriceIds = new ArrayList<>();
            for (Listing l : listings) {
                listingPriceIds.add(l.getCurrentPriceId());
            }
            List<ListingPrice> listingPrices = listingPriceService.getListingPrices(listingPriceIds);
            Map<Integer, ListingPrice> map = new HashMap<Integer, ListingPrice>();
            for (ListingPrice lp : listingPrices) {
                map.put(lp.getId(), lp);
            }
            for (Listing l : listings) {
                l.setCurrentListingPrice(map.get(l.getCurrentPriceId()));
            }
            populateListingAmenities(listings);
        }
        return listings;
    }

    /**
     * Get a listing of user by id
     * 
     * @param userId
     * @param listingId
     * @return
     */
    public Listing getListing(Integer userId, Integer listingId, FIQLSelector selector) {
        Listing listing = listingDao.findListing(listingId, userId, DataVersion.Website, Status.Active);
        if (listing == null) {
            throw new ResourceNotAvailableException(ResourceType.LISTING, ResourceTypeAction.GET);
        }
        if (listing.getCurrentPriceId() != null) {
            List<ListingPrice> listingPrices = listingPriceService.getListingPrices(Arrays.asList(listing
                    .getCurrentPriceId()));
            if (listingPrices.size() > 0) {
                listing.setCurrentListingPrice(listingPrices.get(0));
            }
        }
        populateListingAmenities(Arrays.asList(listing));
        return listing;
    }

    /**
     * Delete a listing created by user
     * 
     * @param userId
     * @param listingId
     * @return
     */
    public Listing deleteListing(Integer userId, Integer listingId) {
        Listing listing = listingDao.findListing(listingId, userId,DataVersion.Website, Status.Active);
        if (listing == null) {
            throw new ResourceNotAvailableException(ResourceType.LISTING, ResourceTypeAction.DELETE);
        }
        listing.setStatus(Status.Inactive);
        listing = listingDao.saveAndFlush(listing);
        return listing;
    }

    /**
     * Fetch all listing amenities and set that in corresponding listing object
     * 
     * @param listings
     */
    public void populateListingAmenities(List<Listing> listings) {
        List<Integer> listingIds = new ArrayList<>();
        for (Listing l : listings) {
            listingIds.add(l.getId());
        }
        List<ListingAmenity> listingAmenities = listingAmenityService.getListingAmenities(listingIds);
        if (listingAmenities.size() > 0) {
            Map<Integer, List<ListingAmenity>> listingIdToAmenitiesMap = createListingToAmenitiesMap(listingAmenities);
            for (Listing l : listings) {
                l.setListingAmenities(listingIdToAmenitiesMap.get(l.getId()));
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
