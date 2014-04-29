package com.proptiger.data.service.portfolio;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;
import javax.persistence.Table;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.init.ExclusionAwareBeanUtilsBean;
import com.proptiger.data.internal.dto.mail.ListingAddMail;
import com.proptiger.data.internal.dto.mail.ListingLoanRequestMail;
import com.proptiger.data.internal.dto.mail.ListingResaleMail;
import com.proptiger.data.internal.dto.mail.MailBody;
import com.proptiger.data.model.City;
import com.proptiger.data.model.Enquiry;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.ProjectDB;
import com.proptiger.data.model.ProjectPaymentSchedule;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.enums.DomainObject;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.model.portfolio.OverallReturn;
import com.proptiger.data.model.portfolio.Portfolio;
import com.proptiger.data.model.portfolio.PortfolioListing;
import com.proptiger.data.model.portfolio.PortfolioListingPaymentPlan;
import com.proptiger.data.model.portfolio.PortfolioListingPrice;
import com.proptiger.data.model.portfolio.enums.ReturnType;
import com.proptiger.data.model.resource.NamedResource;
import com.proptiger.data.model.resource.Resource;
import com.proptiger.data.repo.CityRepository;
import com.proptiger.data.repo.ForumUserDao;
import com.proptiger.data.repo.LocalityDao;
import com.proptiger.data.repo.ProjectDBDao;
import com.proptiger.data.repo.ProjectPaymentScheduleDao;
import com.proptiger.data.repo.portfolio.PortfolioListingDao;
import com.proptiger.data.repo.portfolio.PortfolioListingPriceDao;
import com.proptiger.data.service.CityService;
import com.proptiger.data.service.ImageService;
import com.proptiger.data.service.LocalityService;
import com.proptiger.data.service.ProjectService;
import com.proptiger.data.service.PropertyService;
import com.proptiger.data.util.Constants;
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.data.util.ResourceType;
import com.proptiger.data.util.ResourceTypeAction;
import com.proptiger.data.util.ResourceTypeField;
import com.proptiger.exception.ConstraintViolationException;
import com.proptiger.exception.DuplicateNameResourceException;
import com.proptiger.exception.InvalidResourceException;
import com.proptiger.exception.ProAPIException;
import com.proptiger.exception.ResourceAlreadyExistException;
import com.proptiger.exception.ResourceNotAvailableException;
import com.proptiger.mail.service.MailSender;
import com.proptiger.mail.service.MailTemplateDetail;
import com.proptiger.mail.service.MailType;
import com.proptiger.mail.service.TemplateToHtmlGenerator;

/**
 * This class provides CRUD operations over a property listing that is a
 * addressable entity
 * 
 * @author Rajeev Pandey
 * 
 */
@Service
public class PortfolioService extends AbstractService {

    private static final String       PROPTIGER_URL = "proptiger.url";
    private static Logger             logger        = LoggerFactory.getLogger(PortfolioService.class);
    @Autowired
    private PortfolioListingDao       portfolioListingDao;

    @Autowired
    private LeadGenerationService     leadGenerationService;

    @Autowired
    private PortfolioListingPriceDao  listingPriceDao;

    @Autowired
    private PropertyService           propertyService;

    @Autowired
    private ProjectDBDao              projectDBDao;

    @Autowired
    private LocalityDao               localityDao;

    @Autowired
    private ProjectPaymentScheduleDao paymentScheduleDao;

    @Autowired
    private PropertyReader            propertyReader;

    @Autowired
    private ForumUserDao              forumUserDao;

    @Autowired
    private MailSender                mailSender;

    @Autowired
    private TemplateToHtmlGenerator   mailBodyGenerator;

    @Autowired
    private CityRepository            cityRepository;

    @Autowired
    private ImageService              imageService;

    @Autowired
    private ProjectService            projectService;

    @Autowired
    private LocalityService           localityService;

    @Autowired
    private CityService               cityService;

    @Autowired
    private SubscriptionService       subscriptionService;

    /**
     * Get portfolio object for a particular user id
     * 
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public Portfolio getPortfolioByUserId(Integer userId) {
        logger.debug("Getting portfolio details for user id {}", userId);
        Portfolio portfolio = new Portfolio();
        List<PortfolioListing> listings = portfolioListingDao.findByUserIdAndDeletedFlagOrderByListingIdDesc(
                userId,
                false);
        // portfolio.setPortfolioListings(listings);
        updatePriceInfoInPortfolio(userId, portfolio, listings);
        // updatePaymentSchedule(listings);
        if (listings != null) {
            for (PortfolioListing l : listings) {
                portfolio.addListings(l.getId());
            }
        }

        return portfolio;
    }

    /**
     * Updates price information in Portfolio object
     * 
     * @param portfolio
     * @param listings
     */
    private void updatePriceInfoInPortfolio(Integer userId, Portfolio portfolio, List<PortfolioListing> listings) {
        BigDecimal originalValue = new BigDecimal(0);
        BigDecimal currentValue = new BigDecimal(0);
        if (listings != null) {
            for (PortfolioListing listing : listings) {
                originalValue = originalValue.add(new BigDecimal(listing.getTotalPrice()));
                listing.setCurrentPrice(getListingCurrentPrice(listing));
                currentValue = currentValue.add(new BigDecimal(listing.getCurrentPrice()));
            }
        }
        portfolio.setCurrentValue(currentValue);
        portfolio.setOriginalValue(originalValue);
        OverallReturn overallReturn = getOverAllReturn(originalValue, currentValue);
        portfolio.setOverallReturn(overallReturn);
    }

    /**
     * Calculates the overall return
     * 
     * @param originalValue
     * @param currentValue
     * @return
     */
    private OverallReturn getOverAllReturn(BigDecimal originalValue, BigDecimal currentValue) {
        OverallReturn overallReturn = new OverallReturn();
        BigDecimal changeAmt = currentValue.subtract(originalValue);
        overallReturn.setChangeAmount(changeAmt);
        if (originalValue.doubleValue() == 0.0D) {
            overallReturn.setChangePercent(new BigDecimal(0));
        }
        else {
            BigDecimal div = changeAmt.abs().divide(originalValue, 3, RoundingMode.HALF_DOWN);
            div = div.multiply(new BigDecimal(100));
            overallReturn.setChangePercent(div);
        }
        if (changeAmt.signum() < 0) {
            overallReturn.setReturnType(ReturnType.DECLINE);
        }
        else if (changeAmt.signum() > 0) {
            overallReturn.setReturnType(ReturnType.APPRECIATION);
        }
        else {
            overallReturn.setReturnType(ReturnType.NOCHANGE);
        }
        return overallReturn;
    }

    /**
     * Creating a logical entity Portfolio, that consists a list of
     * PortfolioListing objects
     * 
     * @param userId
     * @param portfolio
     * @return
     */
    public Portfolio createPortfolio(Integer userId, Portfolio portfolio) {
        logger.debug("Creating portfolio for user id {}", userId);
        List<PortfolioListing> presentListing = portfolioListingDao.findByUserIdAndDeletedFlagOrderByListingIdDesc(
                userId,
                false);
        List<PortfolioListing> toCreate = portfolio.getPortfolioListings();
        if (presentListing != null && presentListing.size() > 0) {
            logger.error("Portfolio exists for userid {}", userId);
            throw new ResourceAlreadyExistException("Portfolio exist for user id " + userId);
        }

        Portfolio created = new Portfolio();
        List<PortfolioListing> listings = createPortfolioListings(userId, toCreate);
        created.setPortfolioListings(listings);
        updatePriceInfoInPortfolio(userId, created, listings);
        return created;
    }

    /**
     * Creating list of PortfolioListing objects
     * 
     * @param userId
     * @param toCreateList
     * @return
     */
    private List<PortfolioListing> createPortfolioListings(Integer userId, List<PortfolioListing> toCreateList) {
        List<PortfolioListing> created = new ArrayList<>();
        if (toCreateList != null) {
            for (PortfolioListing toCreate : toCreateList) {
                created.add(createPortfolioListing(userId, toCreate));
            }
        }
        return created;
    }

    /**
     * This method update portfolio for user id. If no portfolio listing exist
     * then it will create portfolio listing, and if listing is already present
     * then it will update.
     * 
     * If any of the existing listing not passed to be updated in portfolio
     * object, then that listing will be deleted from database.
     * 
     * If any existing listing passed to be updated without passing id, then it
     * will be treated as new listing to create.
     * 
     * existing listings
     * 
     * @param userId
     * @param portfolio
     * @return
     */
    public Portfolio updatePortfolio(Integer userId, Portfolio portfolio) {
        logger.debug("Update portfolio details for user id {}", userId);
        List<PortfolioListing> presentListingList = portfolioListingDao.findByUserIdAndDeletedFlagOrderByListingIdDesc(
                userId,
                false);
        Portfolio updated = new Portfolio();
        if (presentListingList == null || presentListingList.size() == 0) {
            logger.debug("No portfolio listing exists for userid {}", userId);
            /*
             * create new portfolio
             */

            createPortfolioListings(userId, portfolio.getPortfolioListings());

        }
        else {
            updated = createOrUpdatePortfolioListings(userId, portfolio, presentListingList);
        }
        List<PortfolioListing> updatedListings = portfolioListingDao.findByUserIdAndDeletedFlagOrderByListingIdDesc(
                userId,
                false);
        updated.setPortfolioListings(updatedListings);
        /*
         * Updating price information in portfolio
         */
        updatePriceInfoInPortfolio(userId, updated, updatedListings);
        return updated;
    }

    /**
     * This method either create a new listing objects if not already present
     * otherwise updates a existing listing object
     * 
     * @param userId
     * @param toUpdatePortfolio
     * @param presentListingList
     * @return
     */
    @Transactional(rollbackFor = { ConstraintViolationException.class, DuplicateNameResourceException.class })
    private Portfolio createOrUpdatePortfolioListings(
            Integer userId,
            Portfolio toUpdatePortfolio,
            List<PortfolioListing> presentListingList) {
        logger.debug("Create or update portfolio details for user id {}", userId);
        /*
         * Either a new Listing will be created if not already present otherwise
         * will be updated
         */
        List<Integer> updatedOrCreatedListings = new ArrayList<Integer>();
        /*
         * Few listings already mapped with user id, there might be some new
         * listings to be created and few might need to update
         */
        Portfolio updatedPortfolio = new Portfolio();
        List<PortfolioListing> toUpdateList = toUpdatePortfolio.getPortfolioListings();
        for (PortfolioListing toUpdate : toUpdateList) {
            if (toUpdate.getId() == null) {
                /*
                 * Need to create new Listing, and adding that to portfolio
                 */
                PortfolioListing newListing = createPortfolioListing(userId, toUpdate);
                updatedOrCreatedListings.add(newListing.getId());
                updatedPortfolio.addPortfolioListings(newListing);
            }
            else {
                /*
                 * Check if toUpdate is already present in database, if present
                 * then update that otherwise create
                 */
                boolean isUpdated = false;
                for (PortfolioListing present : presentListingList) {
                    if (toUpdate.getId().equals(present.getId())) {
                        // need to update
                        present.update(toUpdate);
                        updatedOrCreatedListings.add(toUpdate.getId());
                        isUpdated = true;
                        updatedPortfolio.addPortfolioListings(present);
                        break;
                    }
                }

                if (!isUpdated) {
                    /*
                     * Requested PortfolioListing object (toUpdate) is not
                     * present in database, so creating new
                     */
                    PortfolioListing newListing = createPortfolioListing(userId, toUpdate);
                    updatedOrCreatedListings.add(newListing.getId());
                    updatedPortfolio.addPortfolioListings(newListing);
                }
            }

        }
        /*
         * delete listing from database.
         */
        for (PortfolioListing listingPresent : presentListingList) {
            if (!updatedOrCreatedListings.contains(listingPresent.getId())) {
                portfolioListingDao.delete(listingPresent.getId());
            }

        }
        return updatedPortfolio;
    }

    /**
     * Get all listing object for userId
     * 
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public List<PortfolioListing> getAllPortfolioListings(Integer userId) {
        logger.debug("Getting all portfolio listings for user id {}", userId);
        List<PortfolioListing> listings = portfolioListingDao.findByUserIdAndDeletedFlagOrderByListingIdDesc(
                userId,
                false);
        if (listings != null) {
            for (PortfolioListing listing : listings) {
                listing.setCurrentPrice(getListingCurrentPrice(listing));
                updateOtherSpecificData(listing);
            }
            updatePaymentSchedule(listings);
        }
        return listings;
    }

    /**
     * This method returns current value of listing and if that is 0 then it
     * will return total price as current price.
     * 
     * @param listing
     * @return
     */
    private double getListingCurrentPrice(PortfolioListing listing) {
        BigDecimal currentValue = new BigDecimal(0);
        Double size = listing.getListingSize();
        if (size == null) {
            size = 0.0D;
        }
        // currentValue = new BigDecimal(size *
        // getPropertyPricePerUnitArea(listing.getProperty()));
        Double pricePerUnitArea = listing.getProperty().getPricePerUnitAreaCms();
        if (pricePerUnitArea == null) {
            pricePerUnitArea = listing.getProperty().getPricePerUnitArea();
        }
        currentValue = new BigDecimal(size * pricePerUnitArea);

        if (currentValue.signum() == 0) {
            logger.debug("Current value not available for Listing {} and project type {}", listing.getId(), listing
                    .getProperty().getPropertyId());
            currentValue = new BigDecimal(listing.getTotalPrice());
        }
        else {
            if (listing.getOtherPrices() != null && !listing.getOtherPrices().isEmpty()) {
                for (PortfolioListingPrice listingPrice : listing.getOtherPrices()) {
                    currentValue = currentValue.add(new BigDecimal(listingPrice.getAmount()));
                }
            }
        }

        return currentValue.doubleValue();
    }

    /**
     * Updating derived data in listing objects
     * 
     * @param listing
     */
    private void updateOtherSpecificData(PortfolioListing listing) {
        Integer projectId = listing.getProperty().getProjectId();
        ProjectDB project = projectDBDao.findOne(projectId);
        if (project != null) {
            /*
             * Adding both property and project images
             */
            List<Image> propertyImages = imageService.getImages(DomainObject.property, null, listing.getTypeId());
            listing.setPropertyImages(propertyImages);
            List<Image> projectImages = imageService.getImages(DomainObject.project, null, projectId);
            if (listing.getPropertyImages() != null) {
                listing.getPropertyImages().addAll(projectImages);
            }
            else {
                listing.setPropertyImages(projectImages);
            }
            listing.setProjectName(project.getProjectName());
            listing.setBuilderName(project.getBuilderName());
            listing.setCompletionDate(project.getPromisedCompletionDate());
            listing.setProjectStatus(project.getProjectStatus());
            City city = cityRepository.findOne(project.getCityId());
            if (city != null) {
                listing.setCityName(city.getLabel());
            }
            List<Locality> localities = localityDao.findByLocationOrderByPriority(
                    project.getLocalityId(),
                    "locality",
                    null,
                    null);
            if (localities != null && localities.size() > 0) {
                Locality locality = localities.get(0);
                listing.setLocality(locality.getLabel());
                listing.setLocalityId(locality.getLocalityId());
            }
        }
    }

    /**
     * Get a PortfolioProperty for particular user id and PortfolioProperty id
     * 
     * @param userId
     * @param propertyId
     * @return
     */
    @Transactional(readOnly = true)
    public PortfolioListing getPortfolioListingById(Integer userId, Integer listingId) {
        logger.debug("Getting portfolio listing {} for user id {}", listingId, userId);
        PortfolioListing listing = portfolioListingDao.findByUserIdAndListingIdAndDeletedFlag(userId, listingId, false);
        if (listing == null) {
            logger.error("Portfolio Listing id {} not found for userid {}", listingId, userId);
            throw new ResourceNotAvailableException(ResourceType.LISTING, ResourceTypeAction.GET);
        }
        updateOtherSpecificData(listing);
        listing.setCurrentPrice(getListingCurrentPrice(listing));
        updatePaymentSchedule(listing);
        OverallReturn overallReturn = getOverAllReturn(
                new BigDecimal(listing.getTotalPrice()),
                new BigDecimal(listing.getCurrentPrice()));
        listing.setOverallReturn(overallReturn);
        return listing;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.proptiger.data.service.portfolio.AbstractService#preProcessCreate
     * (com.proptiger.data.model.resource.Resource)
     */
    @Override
    protected <T extends Resource & NamedResource> void preProcessCreate(T resource) {
        super.preProcessCreate(resource);
        PortfolioListing toCreate = (PortfolioListing) resource;
        PortfolioListing propertyPresent = portfolioListingDao.findByUserIdAndNameAndDeletedFlag(
                toCreate.getUserId(),
                toCreate.getName(),
                false);
        if (propertyPresent != null) {
            logger.error("Duplicate resource id {} and name {}", propertyPresent.getId(), propertyPresent.getName());
            throw new DuplicateNameResourceException("Resource with same name exist");
        }
        if (toCreate.getListingSize() == null || toCreate.getListingSize() <= 0) {
            throw new InvalidResourceException(getResourceType(), ResourceTypeField.SIZE);
        }
    }

    /**
     * Creates a PortfolioListing
     * 
     * @param userId
     * @param listing
     * @return
     */
    public PortfolioListing createPortfolioListing(Integer userId, PortfolioListing listing) {
        logger.debug("Create portfolio listing for user id {}", userId);
        listing.setUserId(userId);
        /*
         * Explicitly setting it to null due to use of @JsonUnwrapped, this
         * annotation automatically set value as non null, and that create
         * problem while creating resource.
         * 
         * TODO need to find better solution
         */
        listing.setProperty(null);
        PortfolioListing created = create(listing);
        created = portfolioListingDao.findByUserIdAndListingIdAndDeletedFlag(userId, created.getId(), false);
        updateOtherSpecificData(created);

        subscriptionService.enableOrAddUserSubscription(userId, listing.getListingId(), PortfolioListing.class
                .getAnnotation(Table.class).name(), Constants.SubscriptionType.PROJECT_UPDATES,
                Constants.SubscriptionType.DISCUSSIONS_REVIEWS_NEWS);
        return created;
    }

    /**
     * Updated an existing PortfolioListing
     * 
     * @param userId
     * @param propertyId
     * @param listing
     * @return
     */
    @Transactional(rollbackFor = ResourceNotAvailableException.class)
    public PortfolioListing updatePortfolioListing(Integer userId, Integer propertyId, PortfolioListing listing) {
        logger.debug("Update portfolio listing {} for user id {}", propertyId, userId);
        listing.setUserId(userId);
        listing.setId(propertyId);
        /*
         * as FetchType.Eager of Property is creating new object 
         * expecting nullaware bean to update property as well
         */
        listing.setProperty(null);                     
        PortfolioListing updated = update(listing);
        updateOtherSpecificData(updated);
        /*
         * Update current price
         */
        updated.setCurrentPrice(getListingCurrentPrice(updated));
        return updated;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = { ConstraintViolationException.class, DuplicateNameResourceException.class })
    protected <T extends Resource> T create(T resource) {
        PortfolioListing toCreate = (PortfolioListing) resource;
        logger.debug("Creating PortfolioProperty for userid {}", toCreate.getUserId());
        preProcessCreate(toCreate);
        PortfolioListing created = null;
        /*
         * Creating back reference to parent in child entity, so that while
         * saving parent, child will be saved.
         */
        if (toCreate.getOtherPrices() != null) {
            for (PortfolioListingPrice listingPrice : toCreate.getOtherPrices()) {
                listingPrice.setPortfolioListing(toCreate);
                // setting id null, as while creating id should not be present,
                // need to find better place to do this pre process work
                listingPrice.setListingPriceId(null);
            }
        }
        if (toCreate.getListingPaymentPlan() != null) {
            for (PortfolioListingPaymentPlan listingPaymentPlan : toCreate.getListingPaymentPlan()) {
                listingPaymentPlan.setPortfolioListing(toCreate);
                listingPaymentPlan.setListingPaymentPlanId(null);
            }
        }

        try {
            created = portfolioListingDao.save(toCreate);
        }
        catch (Exception exception) {
            throw new ConstraintViolationException(exception.getMessage(), exception);
        }
        logger.debug("Created PortfolioProperty id {} for userid {}", created.getId(), created.getUserId());
        return (T) created;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T extends Resource> T update(T resource) {
        PortfolioListing toUpdate = (PortfolioListing) resource;
        PortfolioListing resourcePresent = preProcessUpdate(toUpdate);
        PortfolioListing resourceWithSameName = portfolioListingDao.findByUserIdAndNameAndDeletedFlag(
                toUpdate.getUserId(),
                toUpdate.getName(),
                false);
        if (resourceWithSameName != null && !resourcePresent.getId().equals(resourceWithSameName.getId())) {
            logger.error(
                    "Duplicate resource id {} and name {}",
                    resourceWithSameName.getId(),
                    resourceWithSameName.getName());
            throw new DuplicateNameResourceException("Resource with same name exist");
        }
        
        try {
            BeanUtilsBean beanUtilsBean = new ExclusionAwareBeanUtilsBean();
            beanUtilsBean.copyProperties(resourcePresent, toUpdate);
            // updating already present listing i.e resourcePresent  with new data changes contained in toUpdate
            
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new ProAPIException("Portfolio listing update failed", e);
        }
        /*
         * Now need to update other price details if any
         */
        createOrUpdateOtherPrices(resourcePresent, toUpdate);
        return (T) resourcePresent;
    }

    /**
     * Creating or updating other price details in listing object
     * 
     * @param present
     * @param toUpdate
     */
    @Transactional
    private void createOrUpdateOtherPrices(PortfolioListing present, PortfolioListing toUpdate) {
        if (toUpdate.getOtherPrices() == null || toUpdate
                .getOtherPrices().isEmpty()) {
            return;
        }
        if ((present.getOtherPrices() == null || present.getOtherPrices().isEmpty()) && toUpdate.getOtherPrices() != null) {
            /*
             * create new other price, before that creating mapping
             */
            for (PortfolioListingPrice listingPrice : toUpdate.getOtherPrices()) {
                listingPrice.setPortfolioListing(present);
                listingPrice.setListingPriceId(null);
            }
            List<PortfolioListingPrice> created = listingPriceDao.save(toUpdate.getOtherPrices());
            present.setOtherPrices(new HashSet<>(created));
        }
        else if (present.getOtherPrices().size() == toUpdate.getOtherPrices().size()) {
            /*
             * Althought there would be only one other price for each listing,
             * but not applying that check here, so in case if there sizes
             * mismatch it will not update that
             */
            Iterator<PortfolioListingPrice> presentItr = present.getOtherPrices().iterator();
            Iterator<PortfolioListingPrice> toUpdatetItr = toUpdate.getOtherPrices().iterator();
            while (presentItr.hasNext() && toUpdatetItr.hasNext()) {
                PortfolioListingPrice pricePresent = presentItr.next();
                PortfolioListingPrice priceToUpdate = toUpdatetItr.next();
                pricePresent.update(priceToUpdate);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T extends Resource> T preProcessUpdate(T resource) {
        super.preProcessUpdate(resource);
        PortfolioListing toUpdate = (PortfolioListing) resource;
        PortfolioListing resourcePresent = portfolioListingDao.findByListingIdAndDeletedFlag(toUpdate.getId(), false);
        if (resourcePresent == null) {
            logger.error("PortfolioProperty id {} not found", toUpdate.getId());
            throw new ResourceNotAvailableException(ResourceType.LISTING, ResourceTypeAction.UPDATE);
        }
        if (toUpdate.getListingSize()!=null)
        {
            if( toUpdate.getListingSize() <= 0) {
            throw new InvalidResourceException(getResourceType(), ResourceTypeField.SIZE);
            }
        }
        return (T) resourcePresent;
    }

    /**
     * Deletes PortfolioListing for provided user id and listing id
     * 
     * @param userId
     * @param propertyId
     * @return
     */
    @Transactional(rollbackFor = ResourceNotAvailableException.class)
    public PortfolioListing deletePortfolioListing(Integer userId, Integer listingId, String reason) {
        logger.debug("Delete Portfolio Listing id {} for userid {}", listingId, userId);
        PortfolioListing propertyPresent = portfolioListingDao.findByUserIdAndListingIdAndDeletedFlag(
                userId,
                listingId,
                false);
        if (propertyPresent == null) {
            throw new ResourceNotAvailableException(ResourceType.LISTING, ResourceTypeAction.DELETE);
        }
        propertyPresent.setDeleted_flag(true);
        propertyPresent.setReason(reason);

        subscriptionService.disableSubscription(userId, listingId, PortfolioListing.class.getAnnotation(Table.class)
                .name(), Constants.SubscriptionType.PROJECT_UPDATES,
                Constants.SubscriptionType.DISCUSSIONS_REVIEWS_NEWS);

        return propertyPresent;
    }

    private void updatePaymentSchedule(List<PortfolioListing> portfolioListings) {
        for (PortfolioListing listing : portfolioListings) {
            updatePaymentSchedule(listing);
        }
    }

    /**
     * This method updates payment plan for portfolio listing object, if user
     * have already added or updated payment plan
     * 
     * @param portfolioListings
     */
    private void updatePaymentSchedule(PortfolioListing portfolioListing) {
        if (portfolioListing != null) {
            /*
             * If PortfolioListing does not have any payment plan associated,
             * means user is accessing this listing first time, so payment plan
             * will be sent as template for associated project. once a payment
             * plan is created or updated then, do not need to fetch payment
             * plan template
             */
            if (portfolioListing.getListingPaymentPlan() == null || portfolioListing.getListingPaymentPlan().size() == 0) {
                if (portfolioListing.getProperty() != null) {
                    List<ProjectPaymentSchedule> paymentScheduleList = paymentScheduleDao
                            .findByProjectIdGroupByInstallmentNo(portfolioListing.getProperty().getProjectId());
                    Set<PortfolioListingPaymentPlan> listingPaymentPlan = convertToPortfolioListingPaymentPlan(paymentScheduleList);
                    portfolioListing.setListingPaymentPlan(listingPaymentPlan);
                }
            }

        }
    }

    private Set<PortfolioListingPaymentPlan> convertToPortfolioListingPaymentPlan(
            List<ProjectPaymentSchedule> paymentScheduleList) {
        Set<PortfolioListingPaymentPlan> list = new LinkedHashSet<PortfolioListingPaymentPlan>();
        for (ProjectPaymentSchedule paymentSchedule : paymentScheduleList) {
            PortfolioListingPaymentPlan listingPaymentPlan = new PortfolioListingPaymentPlan();
            listingPaymentPlan.setAmount(0.0D);
            listingPaymentPlan.setComponentName(paymentSchedule.getComponentName());
            listingPaymentPlan.setComponentValue(paymentSchedule.getComponentValue());
            listingPaymentPlan.setDueDate(null);
            listingPaymentPlan.setInstallmentName(paymentSchedule.getInstallmentName());
            listingPaymentPlan.setInstallmentNumber(paymentSchedule.getInstallmentNumber());
            listingPaymentPlan.setPaymentDate(null);
            listingPaymentPlan.setPaymentPlan(paymentSchedule.getPaymentPlan());
            listingPaymentPlan.setPaymentSource(null);
            listingPaymentPlan.setStatus(null);
            list.add(listingPaymentPlan);
        }
        return list;
    }

    /**
     * Updating user preference of sell interest for property based on listing
     * id, After changing preference sending lead request
     * 
     * @param userId
     * @param listingId
     * @param interestedToSell
     * @return
     */
    @Transactional(rollbackFor = ResourceNotAvailableException.class)
    public PortfolioListing interestedToSellListing(Integer userId, Integer listingId, Boolean interestedToSell) {
        logger.debug(
                "Updating sell intereset for user id {} and listing id {} with sell interest {}",
                userId,
                listingId,
                interestedToSell);
        PortfolioListing listing = portfolioListingDao.findOne(listingId);
        if (listing == null || !listing.getUserId().equals(userId)) {
            logger.error("Portfolio Listing id {} not found for userid {}", listingId, userId);
            throw new ResourceNotAvailableException(ResourceType.LISTING, ResourceTypeAction.GET);
        }
        updateInterestedToSell(userId, listingId, interestedToSell, listing);
        updateOtherSpecificData(listing);
        ForumUser user = forumUserDao.findOne(userId);
        /*
         * Removing lead generation code in case of interested to sell option
         * from portfolio, because we do not have support for this in our
         * backend system
         */
        // logger.debug("Posting lead request for user id {} and listing id {} with sell interest {}",userId,listingId,interestedToSell);
        // Enquiry enquiry = createEnquiryObj(listing, user);
        // leadGenerationService.postLead(enquiry, LeadSaleType.RESALE,
        // LeadPageName.PORTFOLIO);
        /*
         * Sending mail to internal group
         */
        logger.debug("Sending interested to sell mail to internal group for listing {}", listingId);
        sendMail(userId, listing, MailType.INTERESTED_TO_SELL_PROPERTY_INTERNAL);
        /*
         * Sending interested to sell confirmation mail to user
         */
        logger.debug("Sending interested to sell mail to user for listing {}", listingId);
        sendMail(userId, listing, MailType.INTERESTED_TO_SELL_PROPERTY_USER);
        return listing;
    }

    /**
     * Updating user preference of loan interest for property based on listing
     * id, After changing preference sending emails
     * 
     * @param userId
     * @param listingId
     * @param interestedToLoan
     * @param loanType
     * @return
     */
    @Transactional(rollbackFor = ResourceNotAvailableException.class)
    public PortfolioListing interestedToHomeLoan(
            Integer userId,
            Integer listingId,
            Boolean interestedToLoan,
            String loanType) {
        logger.debug(
                "Updating loan intereset for user id {} and listing id {} with loan interest {}",
                userId,
                listingId,
                interestedToLoan);
        PortfolioListing listing = portfolioListingDao.findByUserIdAndListingIdAndDeletedFlag(userId, listingId, false);
        if (listing == null) {
            logger.error("Portfolio Listing id {} not found for userid {}", listingId, userId);
            throw new ResourceNotAvailableException(ResourceType.LISTING, ResourceTypeAction.GET);
        }
        updateLoanInterest(userId, listingId, interestedToLoan, listing, loanType);
        updateOtherSpecificData(listing);
        sendMail(userId, listing, MailType.LISTING_HOME_LOAN_CONFIRM_TO_USER);
        sendMail(userId, listing, MailType.LISTING_HOME_LOAN_CONFIRM_TO_INTERNAL);
        return listing;
    }

    private Enquiry createEnquiryObj(PortfolioListing listing, ForumUser user) {
        Enquiry enquiry = new Enquiry();
        Property property = listing.getProperty();
        ProjectDB project = projectDBDao.findOne(property.getProjectId());

        enquiry.setAdGrp("");
        enquiry.setCampaign("");
        enquiry.setCityId(project.getCityId());
        enquiry.setCityName("");
        enquiry.setCountryOfResidence(user.getCountryId() + "");
        enquiry.setCreatedDate(new Date());
        enquiry.setEmail(user.getEmail());
        enquiry.setFormName("");
        enquiry.setGaCampaign("");
        enquiry.setGaKeywords("");
        enquiry.setGaMedium("");
        enquiry.setGaNetwork("");
        enquiry.setGaSource("");
        enquiry.setGaTimespent("");
        enquiry.setGaUserId("");
        enquiry.setHttpReferer("");
        enquiry.setIp("");
        enquiry.setKeywords("");
        enquiry.setLocalityId(project.getLocalityId());
        enquiry.setName(user.getUsername());
        enquiry.setPageName("");
        enquiry.setPageUrl("");
        enquiry.setPhone(user.getContact() + "");
        enquiry.setPpc("");
        enquiry.setProjectId(Long.valueOf(property.getProjectId()));
        enquiry.setProjectName(project.getProjectName());
        enquiry.setQuery("");
        enquiry.setSource("");
        enquiry.setUser(user.getUsername());
        enquiry.setUserMedium("");
        return enquiry;
    }

    /**
     * Updating sell interest of user for listing
     * 
     * @param userId
     * @param listingId
     * @param interestedToSell
     * @return
     */
    @Transactional
    private PortfolioListing updateInterestedToSell(
            Integer userId,
            Integer listingId,
            Boolean interestedToSell,
            PortfolioListing listing) {
        listing.setInterestedToSell(interestedToSell);
        listing.setInterestedToSellOn(new Date());
        return listing;
    }

    /**
     * Updating loan interest of user for listing
     * 
     * @param userId
     * @param listingId
     * @param interestedToLoan
     * @return
     */
    @Transactional
    private PortfolioListing updateLoanInterest(
            Integer userId,
            Integer listingId,
            Boolean interestedToLoan,
            PortfolioListing listing,
            String loanType) {
        listing.setInterestedToLoan(interestedToLoan);
        listing.setInterestedToLoanOn(new Date());
        listing.setLoanType(loanType);
        return listing;
    }

    /**
     * @param userId
     * @param listingId
     * @param mailType
     * @return
     */
    public boolean handleMailRequest(Integer userId, Integer listingId, String mailType) {
        MailType mailTypeEnum = null;
        mailTypeEnum = MailType.valueOfString(mailType);
        if (mailTypeEnum == null) {
            throw new IllegalArgumentException("Invalid mail type");
        }
        PortfolioListing listing = getPortfolioListingById(userId, listingId);
        return sendMail(userId, listing, mailTypeEnum);
    }

    /**
     * Based on mail type, this method will create body and subject and send
     * mail using amazon service
     * 
     * @param userId
     * @param listingId
     * @param mailType
     * @return
     */
    private boolean sendMail(Integer userId, PortfolioListing listing, MailType mailTypeEnum) {
        ForumUser user = listing.getForumUser();
        String toStr = user.getEmail();
        MailBody mailBody = null;
        switch (mailTypeEnum) {
            case LISTING_ADD_MAIL_TO_USER:
                ListingAddMail listingAddMail = createListingAddMailObject(listing);
                mailBody = mailBodyGenerator.generateMailBody(
                        MailTemplateDetail.ADD_NEW_PORTFOLIO_LISTING,
                        listingAddMail);
                return mailSender.sendMailUsingAws(toStr, null, null, mailBody.getBody(), mailBody.getSubject());
            case LISTING_HOME_LOAN_CONFIRM_TO_USER:
                ListingLoanRequestMail listingLoanRequestMail = createListingLoanRequestObj(listing);
                mailBody = mailBodyGenerator.generateMailBody(
                        MailTemplateDetail.LISTING_LOAN_REQUEST_USER,
                        listingLoanRequestMail);
                return mailSender.sendMailUsingAws(toStr, null, null, mailBody.getBody(), mailBody.getSubject());
            case LISTING_HOME_LOAN_CONFIRM_TO_INTERNAL:
                ListingLoanRequestMail listingLoanRequestMailInternal = createListingLoanRequestObj(listing);
                mailBody = mailBodyGenerator.generateMailBody(
                        MailTemplateDetail.LISTING_LOAN_REQUEST_INTERNAL,
                        listingLoanRequestMailInternal);
                toStr = propertyReader.getRequiredProperty(PropertyKeys.MAIL_HOME_LOAN_INTERNAL_RECIEPIENT);
                return mailSender.sendMailUsingAws(toStr, null, null, mailBody.getBody(), mailBody.getSubject());
            case INTERESTED_TO_SELL_PROPERTY_INTERNAL:
                ListingResaleMail listingResaleMailInternal = createListingResaleMailObj(listing);
                mailBody = mailBodyGenerator.generateMailBody(
                        MailTemplateDetail.INTERESTED_TO_SELL_PROPERTY_INTERNAL,
                        listingResaleMailInternal);
                toStr = propertyReader.getRequiredProperty(PropertyKeys.MAIL_INTERESTED_TO_SELL_RECIEPIENT);
                return mailSender.sendMailUsingAws(toStr, null, null, mailBody.getBody(), mailBody.getSubject());
            case INTERESTED_TO_SELL_PROPERTY_USER:
                ListingResaleMail listingResaleMailUser = createListingResaleMailObj(listing);
                mailBody = mailBodyGenerator.generateMailBody(
                        MailTemplateDetail.INTERESTED_TO_SELL_PROPERTY_USER,
                        listingResaleMailUser);
                return mailSender.sendMailUsingAws(toStr, null, null, mailBody.getBody(), mailBody.getSubject());
            default:
                throw new IllegalArgumentException("Invalid mail type");
        }

    }

    /**
     * Creating listing resale mail object
     * 
     * @param listing
     * @return
     */
    private ListingResaleMail createListingResaleMailObj(PortfolioListing listing) {
        List<Property> properties = propertyService.getPropertiesForProject(listing.getProperty().getProjectId());
        StringBuilder url = new StringBuilder(propertyReader.getRequiredProperty(PROPTIGER_URL));
        if (properties != null && !properties.isEmpty()) {
            Property required = null;
            for (Property property : properties) {
                if (property.getPropertyId() == listing.getTypeId().intValue()) {
                    required = property;
                    break;
                }
            }
            if (required != null) {
                url.append(required.getURL());
            }

        }
        ForumUser forumUser = listing.getForumUser();
        ListingResaleMail listingResaleMail = new ListingResaleMail();
        listingResaleMail.setBuilder(listing.getBuilderName());
        listingResaleMail.setLocality(listing.getLocality());
        listingResaleMail.setProjectCity(listing.getCityName());
        listingResaleMail.setProjectName(listing.getProjectName());
        listingResaleMail.setPropertyLink(url.toString());
        listingResaleMail.setPropertyName(listing.getName());
        listingResaleMail.setUserName(forumUser.getUsername());
        listingResaleMail.setEmail(forumUser.getEmail());
        listingResaleMail.setMobile(forumUser.getContact() + "");
        listingResaleMail.setListingSize(listing.getListingSize());
        listingResaleMail.setMeasure(listing.getProperty().getMeasure());
        listingResaleMail.setUnitName(listing.getProperty().getUnitName());
        return listingResaleMail;
    }

    /**
     * Creating listing loan request object details
     * 
     * @param listing
     * @return
     */
    private ListingLoanRequestMail createListingLoanRequestObj(PortfolioListing listing) {
        ForumUser forumUser = listing.getForumUser();
        ListingLoanRequestMail listingLoanRequestMail = new ListingLoanRequestMail();
        listingLoanRequestMail.setProjectCity(listing.getCityName());
        listingLoanRequestMail.setProjectName(listing.getProjectName());
        listingLoanRequestMail.setUserName(listing.getForumUser().getUsername());
        listingLoanRequestMail.setEmail(forumUser.getEmail());
        listingLoanRequestMail.setMobile(forumUser.getContact() + "");
        return listingLoanRequestMail;
    }

    /**
     * Creating listing add mail object
     * 
     * @param listing
     * @return
     */
    private ListingAddMail createListingAddMailObject(PortfolioListing listing) {
        ListingAddMail listingAddMail = new ListingAddMail();
        listingAddMail.setPropertyName(listing.getName());
        listingAddMail.setPurchaseDate(listing.getPurchaseDate());
        listingAddMail.setTotalPrice(listing.getTotalPrice());
        listingAddMail.setUserName(listing.getForumUser().getUsername());
        return listingAddMail;
    }

    /**
     * This method get the price per unit area from Property object, this object
     * is mapped to cms database
     * 
     * @param property
     * @return
     */
    // public double getPropertyPricePerUnitArea(Property property){
    // Double pricePerUnitArea = 0.0;
    // Date priceDate = null;
    // if(property != null){
    // if(property.getListings() != null){
    // for(Listing listing: property.getListings()){
    // ListingPrice listingPrice =
    // getLatestPricePerUnitArea(listing.getListingPrice());
    // if (listingPrice != null) {
    // if (priceDate == null ||
    // priceDate.before(listingPrice.getEffectiveDate())) {
    // priceDate = listingPrice.getEffectiveDate();
    // pricePerUnitArea = listingPrice.getPricePerUnitArea();
    // }
    // else if (priceDate.equals(listingPrice.getEffectiveDate()) &&
    // listingPrice.getPricePerUnitArea() > pricePerUnitArea) {
    // pricePerUnitArea = listingPrice.getPricePerUnitArea();
    // }
    // }
    // }
    // }
    // }
    //
    // return pricePerUnitArea;
    // }

    public PortfolioListing sellYourProperty(PortfolioListing portfolioListing) {

        if (portfolioListing.getUserId() != null) {
            if (forumUserDao.findOne(portfolioListing.getUserId()) == null)
                throw new ResourceNotAvailableException(ResourceType.USER, ResourceTypeAction.GET);
        }

        if (portfolioListing.getTypeId() != null) {
            Property property = propertyService.getProperty(portfolioListing.getTypeId());
            if (property == null)
                throw new ResourceNotAvailableException(ResourceType.PROPERTY, ResourceTypeAction.GET);

            portfolioListing.setProjectId(property.getProjectId());
            portfolioListing.setLocalityId(property.getProject().getLocalityId());
            portfolioListing.setCityId(property.getProject().getLocality().getCityId());
        }
        else if (portfolioListing.getProjectId() != null) {
            Project project = projectService.getProjectData(portfolioListing.getProjectId());

            portfolioListing.setLocalityId(project.getLocalityId());
            portfolioListing.setCityId(project.getLocality().getCityId());
        }
        else if (portfolioListing.getLocalityId() != null) {
            Locality locality = localityService.getLocality(portfolioListing.getLocalityId());
            if (locality == null)
                throw new ResourceNotAvailableException(ResourceType.LOCALITY, ResourceTypeAction.GET);

            portfolioListing.setCityId(locality.getCityId());
        }
        else if (portfolioListing.getCityId() != null) {
            cityService.getCity(portfolioListing.getCityId());
        }

        if (portfolioListing.getIsBroker() == null || portfolioListing.getLeadUser() == null
                || portfolioListing.getLeadEmail() == null
                || portfolioListing.getLeadCountryId() == null
                || portfolioListing.getLeadContact() == null) {
            throw new IllegalArgumentException(
                    " user information is missing. email, username, contact number and country should be present.");
        }

        if (portfolioListing.getIsBroker() == false && ((portfolioListing.getCityId() == null && portfolioListing
                .getCityName() == null) || (portfolioListing.getLocality() == null && portfolioListing.getLocalityId() == null)
                || portfolioListing.getProjectName() == null || portfolioListing.getName() == null)) {
            throw new IllegalArgumentException(
                    "Project compulsory parameters : project name, property name, locality and city should be present.");
        }
        portfolioListing.setSourceType(PortfolioListing.Source.lead);
        /*
         * Setting the fields to null as because of unwrapped , the object get
         * initialized.
         */
        portfolioListing.setProperty(null);
        portfolioListing.setExtraAttributes(null);
        portfolioListing.setInterestedToLoan(null);
        portfolioListing.setInterestedToLoanOn(null);
        portfolioListing.setInterestedToSell(null);
        portfolioListing.setInterestedToSellOn(null);
        portfolioListing.setListingMeasure(null);

        PortfolioListing savePortfolioListing = portfolioListingDao.save(portfolioListing);
        if (savePortfolioListing == null)
            throw new PersistenceException("Sell your property request cannot be saved.");

        sendMailOnSellYourProperty(savePortfolioListing);
        return savePortfolioListing;
    }

    /**
     * This method will send the mail to recipients with details about the error
     * reported by user.
     * 
     * @param projectError
     * @param property
     * @return
     */
    private boolean sendMailOnSellYourProperty(PortfolioListing portfolioListing) {
        String mailToAddress = propertyReader.getRequiredProperty("mail.property.sell.to.recipient");
        String mailCCAddress = propertyReader.getRequiredProperty("mail.property.sell.cc.recipient");

        String[] mailCC = null;

        if (mailToAddress.length() < 1) {
            logger.error("Project/Property Error Reporting is not able to send mail as 'to' mail recipients is empty. The application properties property (mail.report.error.to.recipient) is empty.");
            return false;
        }

        String[] mailTo = mailToAddress.split(",");
        if (mailCCAddress.length() > 0) {
            mailCC = mailCCAddress.split(",");
        }

        MailBody mailBody = mailBodyGenerator.generateMailBody(MailTemplateDetail.SELL_YOUR_PROPERTY, portfolioListing);

        return mailSender.sendMailUsingAws(mailTo, mailCC, null, mailBody.getBody(), mailBody.getSubject());

    }

    @Override
    protected ResourceType getResourceType() {
        return ResourceType.LISTING;
    }

}
