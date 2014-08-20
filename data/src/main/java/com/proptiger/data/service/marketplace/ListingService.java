package com.proptiger.data.service.marketplace;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
import com.proptiger.data.pojo.LimitOffsetPageRequest;
import com.proptiger.data.repo.PropertyDao;
import com.proptiger.data.repo.marketplace.ListingDao;
import com.proptiger.data.service.ProjectPhaseService;
import com.proptiger.data.service.PropertyService;
import com.proptiger.data.util.JsonUtil;
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

    @Autowired
    private PropertyDao           propertyDao;

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
        Property property = listing.getProperty();
        //got Property object so set this as null
        listing.setProperty(null);
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

        List<ListingAmenity> amenities = listingAmenityService.createListingAmenities(property.getProjectId(), created);
        created.setListingAmenities(amenities);
        return created;
    }

    /**
     * Validate Listing data before creation
     * 
     * @param listing
     * @param userId
     */
    private void preCreateValidation(Listing listing, Integer userId) {
        Property property = null;
        // only no phase supported as of now
        listing.setPhaseId(null);
        if (listing.getPropertyId() == null) {
            property = propertyService.createUnverifiedPropertyOrGetExisting(listing, userId);
            listing.setPropertyId(property.getPropertyId());
        }
        else {
            property = propertyService.getProperty(listing.getPropertyId());
        }
        //need project id to fetch amenities, will make this null in createListing method
        listing.setProperty(property);
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
            // no validation for phase id
        }

        if (listing.getTowerId() != null) {
            // TODO check if tower id exists, else exception
        }

        if (listing.getFloor() != null && listing.getFloor() < 0) {
            throw new BadRequestException("Invalid floor");
        }
        validateListingCategory(listing);
        // TODO need to update agent id in updatedBy field
        listing.setUpdatedBy(userId);
        listing.setSellerId(userId);
        listing.setBookingStatusId(null);
        listing.setStatus(Status.Active);
        if (listing.getJsonDump() != null && !listing.getJsonDump().isEmpty()) {
            if (!JsonUtil.isValidJsonString(listing.getJsonDump())) {
                throw new BadRequestException("Invalid json in jsonDump");
            }
        }
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
        Pageable pageable = new LimitOffsetPageRequest(selector.getStart(), selector.getRows(), new Sort(
                Direction.DESC,
                "id"));
        
        
        List<Listing> listings = listingDao.findListings(userId, DataVersion.Website, Status.Active, pageable);
        listingPriceService.populateListingPrices(listings, selector);
        listingAmenityService.populateListingAmenities(listings, selector);
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
        listingPriceService.populateListingPrices(Arrays.asList(listing), selector);
        listingAmenityService.populateListingAmenities(Arrays.asList(listing), selector);
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
        Listing listing = listingDao.findListing(listingId, userId, DataVersion.Website, Status.Active);
        if (listing == null) {
            throw new ResourceNotAvailableException(ResourceType.LISTING, ResourceTypeAction.DELETE);
        }
        listing.setStatus(Status.Inactive);
        listing = listingDao.saveAndFlush(listing);
        return listing;
    }
}
