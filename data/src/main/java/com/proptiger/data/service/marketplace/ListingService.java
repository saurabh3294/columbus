package com.proptiger.data.service.marketplace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.core.enums.DataVersion;
import com.proptiger.core.enums.EntityType;
import com.proptiger.core.enums.ListingCategory;
import com.proptiger.core.enums.ResourceType;
import com.proptiger.core.enums.ResourceTypeAction;
import com.proptiger.core.enums.Status;
import com.proptiger.core.exception.BadRequestException;
import com.proptiger.core.exception.ResourceAlreadyExistException;
import com.proptiger.core.exception.ResourceNotAvailableException;
import com.proptiger.core.model.cms.Listing;
import com.proptiger.core.model.cms.ListingAmenity;
import com.proptiger.core.model.cms.ListingPrice;
import com.proptiger.core.model.cms.Property;
import com.proptiger.core.model.user.User;
import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.core.pojo.response.PaginatedResponse;
import com.proptiger.data.model.ProjectPhase;
import com.proptiger.data.repo.PropertyDao;
import com.proptiger.data.repo.marketplace.ListingDao;
import com.proptiger.data.service.ProjectPhaseService;
import com.proptiger.data.service.PropertyService;
import com.proptiger.data.service.user.UserService;
import com.proptiger.data.util.JsonUtil;

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

    @Autowired
    private UserService           userService;

    public Listing getListingByListingId(Integer listingId) {
        return listingDao.findOne(listingId);
    }

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
        // got Property object so set this as null
        listing.setProperty(null);
        ListingPrice currentListingPrice = listing.getCurrentListingPrice();
        /*
         * Since we made currentListingPrice non transactional due to join
         * needed in custom query. so currentListingPrice will be saved manually
         * using listingPriceService.
         */
        listing.setCurrentListingPrice(null);
        try {
            listing = listingDao.saveAndFlush(listing);
        }
        catch (PersistenceException e) {
            logger.error("error while creating listing {}", e);
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new ResourceAlreadyExistException("Listing already exists");
            }
            throw new ResourceAlreadyExistException("Listing could not be created");
        }
        if (currentListingPrice != null) {
            ListingPrice listingPriceCreated = listingPriceService.createListingPrice(currentListingPrice, listing);

            // save listing again with current listing price id
            listing.setCurrentPriceId(listingPriceCreated.getId());
            listing = listingDao.saveAndFlush(listing);

            listing.setCurrentListingPrice(listingPriceCreated);
        }

        List<ListingAmenity> amenities = listingAmenityService.createListingAmenities(property.getProjectId(), listing);
        listing.setListingAmenities(amenities);
        return listing;
    }

    public PaginatedResponse<Listing> putListing(Listing listing, Integer userIdentifier, Integer listingId) {
        listing.setId(listingId);
        Listing listingInDB = listingDao.findById(listingId);

        if (!listingInDB.getSellerId().equals(userIdentifier)) {
            throw new BadRequestException("you can change only your listings");
        }

        Property property = listingInDB.getProperty();
        listing.setProperty(null);

        if (listing.getFloor() != null) {
            listingInDB.setFloor(listing.getFloor());
        }

        if (listing.getJsonDump() != null) {
            listingInDB.setJsonDump(listing.getJsonDump());
        }

        List<ListingAmenity> listingAmenities = listingAmenityService.getListingAmenities(Collections
                .singletonList(listingId));

        List<Integer> alreadyPresentListingAminityIds = new ArrayList<Integer>();

        if (listingAmenities != null) {
            for (ListingAmenity listingAmenity : listingAmenities) {
                alreadyPresentListingAminityIds.add(listingAmenity.getId());
            }
            listingAmenityService.removeAminities(alreadyPresentListingAminityIds);
        }

        List<ListingAmenity> amenities = listingAmenityService.createListingAmenities(property.getProjectId(), listing);
        listing.setListingAmenities(amenities);

        listingDao.save(listingInDB);

        return new PaginatedResponse<>(listing, 1);
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
        // need project id to fetch amenities, will make this null in
        // createListing method
        listing.setProperty(property);
        if (listing.getPhaseId() == null) {
            // add Logical phase id
            List<ProjectPhase> projectPhase = projectPhaseService.getPhaseDetailsFromFiql(
                    new FIQLSelector().addAndConditionToFilter("phaseType==" + EntityType.Logical),
                    property.getProjectId(),
                    DataVersion.Website,
                    null);
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
        listing.setDeleted(false);
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
    public PaginatedResponse<List<Listing>> getListings(Integer userId, FIQLSelector selector) {
        selector.applyDefSort("-id");

        List<Long> listingSize = listingDao.findListingsCount(userId, DataVersion.Website, Status.Active);

        List<Listing> listings;
        if (selector.getStart() > listingSize.get(0)) {
            return new PaginatedResponse<>(null, listingSize.get(0));
        }
        else {
            listings = listingDao.findListings(userId, DataVersion.Website, Status.Active, selector);
        }
        String fields = selector.getFields();
        if (fields != null) {
            if (fields.contains("listingAmenities")) {
                List<ListingAmenity> listingAmenities = listingAmenityService.getListingAmenitiesOfListings(listings);
                if (listingAmenities.size() > 0) {
                    Map<Integer, List<ListingAmenity>> listingIdToAmenitiesMap = createListingToAmenitiesMap(listingAmenities);
                    for (Listing l : listings) {
                        l.setListingAmenities(listingIdToAmenitiesMap.get(l.getId()));
                    }
                }
            }

            if (fields.contains("seller")) {
                Set<Integer> sellerIds = extractSellerIds(listings);
                Map<Integer, User> users = userService.getUsers(sellerIds);
                for (Listing l : listings) {
                    if (users.get(l.getSellerId()) != null) {
                        l.setSeller(users.get(l.getSellerId()));
                    }
                }
            }
        }

        // TODO due to explicit join would be fetched so if not asked then set
        // this to null, handle using FIQL
        if (fields == null || !fields.contains("property")) {
            for (Listing l : listings) {
                l.setProperty(null);
            }
        }
        return new PaginatedResponse<>(listings, listingSize.get(0));
    }

    private Set<Integer> extractSellerIds(List<Listing> listings) {
        Set<Integer> listingIds = new HashSet<Integer>();
        for (Listing listing : listings) {
            if (listing.getSellerId() != null) {
                listingIds.add(listing.getSellerId());
            }
        }
        return listingIds;
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
        String fields = selector.getFields();
        if (fields != null) {
            if (fields.contains("listingAmenities")) {
                List<ListingAmenity> listingAmenities = listingAmenityService.getListingAmenitiesOfListings(Arrays
                        .asList(listing));
                listing.setListingAmenities(listingAmenities);
            }
        }
        if (fields == null || !fields.contains("property")) {
            // due to explicit join in query it would be fetched so if not asked
            // then set this to null
            listing.setProperty(null);
        }
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
        listing.setDeleted(true);
        listing = listingDao.saveAndFlush(listing);
        return listing;
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

    public List<ListingPrice> getLatestListingPrice(List<Integer> propertyId) {
        List<Integer> listingPriceId = listingDao.getListingPriceIds(propertyId);
        List<ListingPrice> listingPrices = null;
        if (listingPriceId != null && !listingPriceId.isEmpty()) {
            listingPrices = listingDao.getListingPrice(listingPriceId);
        }
        return listingPrices;
    }

}
