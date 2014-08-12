package com.proptiger.data.service.marketplace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;

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

    @Autowired
    private PropertyService     propertyService;

    @Autowired
    private ListingPriceService listingPriceService;

    @Autowired
    private ListingDao          listingDao;

    @Autowired
    private ProjectPhaseService projectPhaseService;

    /**
     * Create a new listing
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
            throw new ResourceAlreadyExistException("Listing already exists");
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

        return created;
    }

    /**
     * Validate Listing data before creation
     * 
     * @param listing
     * @param userId
     */
    private void preCreateValidation(Listing listing, Integer userId) {
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

        validateListingCategory(listing.getListingCategory());
        listing.setUpdatedBy(userId);
        listing.setSellerId(userId);
        listing.setBookingStatusId(null);
        listing.setStatus(Status.Active);
    }

    private void validateListingCategory(ListingCategory listingCategory) {
        if (listingCategory == ListingCategory.Primary) {
            throw new BadRequestException("Primary listing category not allowed");
        }
    }

    public List<Listing> getListings(Integer userId) {
        List<Listing> listings = listingDao.findBySellerIdAndStatus(userId, Status.Active);
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
        return listings;
    }

    public Listing getListing(Integer userId, Integer listingId) {
        Listing listing = listingDao.findBySellerIdAndIdAndStatus(userId, listingId, Status.Active);
        if (listing == null) {
            throw new ResourceNotAvailableException(ResourceType.LISTING, ResourceTypeAction.DELETE);
        }
        if (listing.getCurrentPriceId() != null) {
            List<ListingPrice> listingPrices = listingPriceService.getListingPrices(Arrays.asList(listing
                    .getCurrentPriceId()));
            if (listingPrices.size() > 0) {
                listing.setCurrentListingPrice(listingPrices.get(0));
            }
        }
        return listing;
    }

    public Listing deleteListing(Integer userId, Integer listingId) {
        Listing listing = listingDao.findBySellerIdAndIdAndStatus(userId, listingId, Status.Active);
        if (listing == null) {
            throw new ResourceNotAvailableException(ResourceType.LISTING, ResourceTypeAction.DELETE);
        }
        listing.setStatus(Status.Inactive);
        listing = listingDao.saveAndFlush(listing);
        return listing;
    }
}
