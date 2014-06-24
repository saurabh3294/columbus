package com.proptiger.data.service.user.portfolio;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceException;
import javax.persistence.Table;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.proptiger.data.enums.DomainObject;
import com.proptiger.data.enums.mail.MailTemplateDetail;
import com.proptiger.data.enums.mail.MailType;
import com.proptiger.data.enums.portfolio.ListingStatus;
import com.proptiger.data.enums.resource.ResourceType;
import com.proptiger.data.enums.resource.ResourceTypeAction;
import com.proptiger.data.enums.resource.ResourceTypeField;
import com.proptiger.data.init.ExclusionAwareBeanUtilsBean;
import com.proptiger.data.internal.dto.mail.ListingAddMail;
import com.proptiger.data.internal.dto.mail.ListingLoanRequestMail;
import com.proptiger.data.internal.dto.mail.ListingResaleMail;
import com.proptiger.data.internal.dto.mail.MailBody;
import com.proptiger.data.internal.dto.mail.MailDetails;
import com.proptiger.data.model.City;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.ProjectPaymentSchedule;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.model.user.portfolio.OverallReturn;
import com.proptiger.data.model.user.portfolio.Portfolio;
import com.proptiger.data.model.user.portfolio.PortfolioListing;
import com.proptiger.data.model.user.portfolio.PortfolioListingPaymentPlan;
import com.proptiger.data.model.user.portfolio.PortfolioListingPrice;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.repo.ForumUserDao;
import com.proptiger.data.repo.ProjectPaymentScheduleDao;
import com.proptiger.data.repo.user.portfolio.PortfolioListingDao;
import com.proptiger.data.repo.user.portfolio.PortfolioListingPriceDao;
import com.proptiger.data.service.CityService;
import com.proptiger.data.service.ImageEnricher;
import com.proptiger.data.service.ImageService;
import com.proptiger.data.service.LocalityService;
import com.proptiger.data.service.ProjectService;
import com.proptiger.data.service.PropertyService;
import com.proptiger.data.service.mail.MailSender;
import com.proptiger.data.service.mail.TemplateToHtmlGenerator;
import com.proptiger.data.service.user.LeadGenerationService;
import com.proptiger.data.service.user.SubscriptionService;
import com.proptiger.data.util.Constants;
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.data.util.portfolio.PortfolioUtil;
import com.proptiger.exception.ConstraintViolationException;
import com.proptiger.exception.DuplicateNameResourceException;
import com.proptiger.exception.InvalidResourceException;
import com.proptiger.exception.ProAPIException;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 * This class provides CRUD operations over a property listing that is a
 * addressable entity
 * 
 * @author Rajeev Pandey
 * 
 */
@Service
public class PortfolioService {

    private static Logger             logger = LoggerFactory.getLogger(PortfolioService.class);
    @Autowired
    private PortfolioListingDao       portfolioListingDao;

    @Autowired
    private LeadGenerationService     leadGenerationService;

    @Autowired
    private PortfolioListingPriceDao  listingPriceDao;

    @Autowired
    private PropertyService           propertyService;

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
    private ImageService              imageService;

    @Autowired
    private ProjectService            projectService;

    @Autowired
    private LocalityService           localityService;

    @Autowired
    private CityService               cityService;

    @Autowired
    private SubscriptionService       subscriptionService;

    @Value("${proptiger.url}")
    private String                    websiteHost;

    @Autowired
    private ImageEnricher             imageEnricher;

    /**
     * Get portfolio object for a particular user id
     * 
     * @param userId
     * @param listingStatus
     * @return
     */
    @Transactional(readOnly = true)
    public Portfolio getPortfolioByUserId(Integer userId, List<ListingStatus> listingStatus) {
        logger.debug("Getting portfolio details for user id {}", userId);
        Portfolio portfolio = new Portfolio();
        List<PortfolioListing> listings = portfolioListingDao
                .findByUserIdAndDeletedFlagAndSourceTypeInAndListingStatusInOrderByListingIdDesc(
                        userId,
                        false,
                        Constants.SOURCETYPE_LIST,
                        listingStatus);
        PortfolioUtil.updatePriceInfoInPortfolio(portfolio, listings);
        if (listings != null) {
            for (PortfolioListing l : listings) {
                portfolio.addListings(l.getId());
            }
        }

        return portfolio;
    }

    /**
     * Get all listing object for userId
     * 
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public List<PortfolioListing> getAllPortfolioListings(Integer userId, List<ListingStatus> listingStatus) {
        logger.debug("Getting all portfolio listings for user id {}", userId);
        List<PortfolioListing> listings = portfolioListingDao
                .findByUserIdAndDeletedFlagAndSourceTypeInAndListingStatusInOrderByListingIdDesc(
                        userId,
                        false,
                        Constants.SOURCETYPE_LIST,
                        listingStatus);

        updateOtherSpecificData(listings);
        updatePaymentSchedule(listings);
        return listings;
    }

    /**
     * Updating derived data in listing objects
     * 
     * @param listing
     */
    private void updateOtherSpecificData(List<PortfolioListing> listings) {
        if (listings == null || listings.isEmpty()) {
            return;
        }
        List<Long> propertyIds = new ArrayList<Long>();
        List<Long> completeProjectIds = new ArrayList<>();
        Set<Integer> incompleteProjectIds = new HashSet<Integer>();

        for (PortfolioListing listing : listings) {
            if (listing.getListingStatus() == ListingStatus.ACTIVE) {  // add both ProjectIds and PropertyIds for ACTIVE listings
                propertyIds.add(new Long(listing.getTypeId()));
                if (listing.getProjectId() == null) {
                    completeProjectIds.add(new Long(listing.getProperty().getProjectId()));
                }
                else if (listing.getTypeId() != null) {
                    completeProjectIds.add(new Long(listing.getProjectId()));
                }
            }
            else if (listing.getListingStatus() == ListingStatus.INCOMPLETE) {  // add only ProjectIds for INCOMPLETE listings
                incompleteProjectIds.add(listing.getProjectId());
            }
        }
        Map<Integer, Project> projectIdToProjectMap = new HashMap<Integer, Project>();
        Map<Integer, List<Image>> propertyIdToImageMap = new HashMap<Integer, List<Image>>();
        if (!propertyIds.isEmpty()) {
            Selector propertySelector = new Gson().fromJson(
                    "{\"filters\":{\"and\":[{\"equal\":{\"propertyId\":" + propertyIds
                            + "}}]},\"paging\":{\"start\":0,\"rows\":9999}}",
                    Selector.class);
            List<Property> properties = propertyService.getProperties(propertySelector);

            projectIdToProjectMap = PortfolioUtil.createProjectIdMap(properties);

            List<Image> propertyImages = imageService.getImages(DomainObject.property, null, propertyIds);
            propertyIdToImageMap = PortfolioUtil.getPropertyIdToImageMap(propertyImages); 
        }

        List<Project> projects = new ArrayList<Project>();
        if (!incompleteProjectIds.isEmpty()) {
            projects = projectService.getProjectsByIds(incompleteProjectIds);
        }
        for (Project project : projects) {
            projectIdToProjectMap.put(project.getProjectId(), project);
        }
        List<Image> projectImages = imageService.getImages(DomainObject.project, null, completeProjectIds);
        Map<Integer, List<Image>> projectIdToImagesMap = PortfolioUtil.getProjectIdToImageMap(projectImages); 
        Integer projectId;
        for (PortfolioListing listing : listings) {
            if (listing.getProjectId() == null) {
                projectId = listing.getProperty().getProjectId();
            }
            else {
                projectId = listing.getProjectId();
            }
            /*
             * Update current price
             */
            listing.setCurrentPrice(PortfolioUtil.getListingCurrentPrice(listing));
            /*
             * Adding both property and project images
             */
            listing.setPropertyImages(propertyIdToImageMap.get(listing.getTypeId()));
            if (listing.getPropertyImages() != null) {
                if (projectIdToImagesMap.get(projectId) != null) {
                    // if project images are then add that too
                    listing.getPropertyImages().addAll(projectIdToImagesMap.get(projectId));
                }

            }
            else {
                // if property image were not present then add project images
                listing.setPropertyImages(projectIdToImagesMap.get(projectId));
            }
            Project project = projectIdToProjectMap.get(projectId);
            if (listing.getPropertyImages().isEmpty()) {
                Image defaultProjectImage = imageEnricher.getDefaultProjectImage(project.getImageURL());
                listing.setPropertyImages(Arrays.asList(defaultProjectImage));
            }
            listing.setProjectName(project.getName());
            listing.setBuilderName(project.getBuilder().getName());
            listing.setCompletionDate(project.getPossessionDate());
            listing.setProjectStatus(project.getProjectStatus());
            City city = project.getLocality().getSuburb().getCity();
            listing.setCityName(city.getLabel());
            Locality locality = project.getLocality();
            listing.setLocality(locality.getLabel());
            listing.setLocalityId(locality.getLocalityId());
        }
    }

    /**
     * Get a PortfolioProperty for particular user id and PortfolioProperty id
     * 
     * @param userId
     * @param listingId
     * @return
     */
    @Transactional(readOnly = true)
    public PortfolioListing getPortfolioListingById(Integer userId, Integer listingId) {
        logger.debug("Getting portfolio listing {} for user id {}", listingId, userId);

        PortfolioListing listing = portfolioListingDao.findByListingIdAndDeletedFlag(listingId, false);

        if (listing == null) {
            logger.error("Portfolio Listing id {} not found for userid {}", listingId, userId);
            throw new ResourceNotAvailableException(ResourceType.LISTING, ResourceTypeAction.GET);
        }
        updateOtherSpecificData(Arrays.asList(listing));
        updatePaymentSchedule(listing);
        OverallReturn overallReturn = PortfolioUtil
                .getOverAllReturn(listing.getTotalPrice(), listing.getCurrentPrice());
        listing.setOverallReturn(overallReturn);
        return listing;
    }

    private void preCreateValidations(PortfolioListing toCreate) {
        toCreate.setId(null);
        PortfolioListing propertyPresent = portfolioListingDao
                .findByUserIdAndNameAndProjectIdAndDeletedFlagAndSourceTypeIn(
                        toCreate.getUserId(),
                        toCreate.getName(),
                        toCreate.getProjectId(),
                        false,
                        Constants.SOURCETYPE_LIST);
        if (propertyPresent != null) {
            logger.error("Duplicate resource id {} and name {}", propertyPresent.getId(), propertyPresent.getName());
            throw new DuplicateNameResourceException("Resource with same name exist");
        }
        if(toCreate.getTypeId() == null || toCreate.getListingSize() == null || toCreate.getName() == null || toCreate.getBasePrice() == null || toCreate.getTotalPrice() == null){
            toCreate.setListingStatus(ListingStatus.INCOMPLETE);
        }
        else if (toCreate.getListingSize() == null || toCreate.getListingSize() <= 0) {
                throw new InvalidResourceException(ResourceType.LISTING, ResourceTypeField.SIZE);
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
        created = portfolioListingDao.findByListingIdAndDeletedFlag(created.getId(), false);
        if (created.getListingStatus() == ListingStatus.ACTIVE) {
            subscriptionService.enableOrAddUserSubscription(
                    userId,
                    listing.getListingId(),
                    PortfolioListing.class.getAnnotation(Table.class).name(),
                    Constants.SubscriptionType.PROJECT_UPDATES,
                    Constants.SubscriptionType.DISCUSSIONS_REVIEWS_NEWS);
        }
       return created;
    }

    /**
     * Updated an existing PortfolioListing
     * 
     * @param userId
     * @param listingId
     * @param listing
     * @return
     */
    @Transactional(rollbackFor = ResourceNotAvailableException.class)
    public PortfolioListing updatePortfolioListing(Integer userId, Integer listingId, PortfolioListing listing) {
        logger.debug("Update portfolio listing {} for user id {}", listingId, userId);
        listing.setUserId(userId);
        listing.setId(listingId);
        /*
         * as FetchType.Eager of Property is creating new object expecting null
         * aware bean to update property as well
         */
        listing.setProperty(null);
        PortfolioListing updated = update(listing);
        // updateOtherSpecificData(Arrays.asList(listing));
        return updated;
    }

    @Transactional(rollbackFor = { ConstraintViolationException.class, DuplicateNameResourceException.class })
    private PortfolioListing create(PortfolioListing toCreate) {
        logger.debug("Creating PortfolioProperty for userid {}", toCreate.getUserId());
        preCreateValidations(toCreate);
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
        PortfolioListing created = null;
        try {
            created = portfolioListingDao.save(toCreate);
        }
        catch (Exception exception) {
            throw new ConstraintViolationException(exception.getMessage(), exception);
        }
        logger.debug("Created PortfolioProperty id {} for userid {}", created.getId(), created.getUserId());
        return created;
    }

    private PortfolioListing update(PortfolioListing toUpdate) {
        PortfolioListing resourcePresent = preProcessUpdate(toUpdate);
        PortfolioListing resourceWithSameName = portfolioListingDao
                .findByUserIdAndNameAndProjectIdAndDeletedFlagAndSourceTypeIn(
                        toUpdate.getUserId(),
                        toUpdate.getName(),
                        toUpdate.getProjectId(),
                        false,
                        Constants.SOURCETYPE_LIST);
        if (resourceWithSameName != null && !resourcePresent.getId().equals(resourceWithSameName.getId())) {
            logger.error(
                    "Duplicate resource id {} and name {}",
                    resourceWithSameName.getId(),
                    resourceWithSameName.getName());
            throw new DuplicateNameResourceException("Resource with same name exist");
        }

        /*
         * Now need to update other price details if any
         */
        createOrUpdateOtherPrices(resourcePresent, toUpdate);

        /*
         * Setting OtherPrices of toUpdate null as it has already been updated
         * otherwise would be set null during copy in BeansUtils NullAware
         */
        toUpdate.setOtherPrices(null);
        /*
         * As Payment plan is not to be updated, otherwise would be set null
         * during copy in BeansUtils NullAware
         */
        toUpdate.setListingPaymentPlan(null);

        try {
            BeanUtilsBean beanUtilsBean = new ExclusionAwareBeanUtilsBean();
            /*
             * updating already present listing i.e resourcePresent with new
             * data changes contained in toUpdate
             */
            beanUtilsBean.copyProperties(resourcePresent, toUpdate);

        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new ProAPIException("Portfolio listing update failed", e);
        }

        return resourcePresent;
    }

    /**
     * Creating or updating other price details in listing object
     * 
     * @param present
     * @param toUpdate
     */
    @Transactional
    private void createOrUpdateOtherPrices(PortfolioListing present, PortfolioListing toUpdate) {
        if (toUpdate.getOtherPrices() == null || toUpdate.getOtherPrices().isEmpty()) {
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

    private PortfolioListing preProcessUpdate(PortfolioListing toUpdate) {
        PortfolioListing resourcePresent = portfolioListingDao.findByListingIdAndDeletedFlag(toUpdate.getId(), false);
        if (resourcePresent == null) {
            logger.error("PortfolioProperty id {} not found", toUpdate.getId());
            throw new ResourceNotAvailableException(ResourceType.LISTING, ResourceTypeAction.UPDATE);
        }
        if (toUpdate.getListingSize() != null) {
            if (toUpdate.getListingSize() <= 0) {
                throw new InvalidResourceException(ResourceType.LISTING, ResourceTypeField.SIZE);
            }
        }
        return resourcePresent;
    }

    /**
     * Deletes PortfolioListing for provided user id and listing id
     * 
     * @param userId
     * @param listingId
     * @return
     */
    @Transactional(rollbackFor = ResourceNotAvailableException.class)
    public PortfolioListing deletePortfolioListing(Integer userId, Integer listingId, String reason) {
        logger.debug("Delete Portfolio Listing id {} for userid {}", listingId, userId);
        PortfolioListing propertyPresent = portfolioListingDao.findByListingIdAndDeletedFlag(listingId, false);
        if (propertyPresent == null) {
            throw new ResourceNotAvailableException(ResourceType.LISTING, ResourceTypeAction.DELETE);
        }
        propertyPresent.setDeleted_flag(true);
        propertyPresent.setReason(reason);
        if (propertyPresent.getListingStatus() == ListingStatus.ACTIVE) {
            subscriptionService.disableSubscription(
                    userId,
                    listingId,
                    PortfolioListing.class.getAnnotation(Table.class).name(),
                    Constants.SubscriptionType.PROJECT_UPDATES,
                    Constants.SubscriptionType.DISCUSSIONS_REVIEWS_NEWS);
        }
        return propertyPresent;
    }

    private void updatePaymentSchedule(List<PortfolioListing> portfolioListings) {
        if (portfolioListings == null || portfolioListings.isEmpty()) {
            return;
        }
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
                    Set<PortfolioListingPaymentPlan> listingPaymentPlan = ProjectPaymentSchedule
                            .convertToPortfolioListingPaymentPlan(paymentScheduleList);
                    portfolioListing.setListingPaymentPlan(listingPaymentPlan);
                }
            }

        }
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
        listing.updateInterestedToSell(interestedToSell);
        updateOtherSpecificData(Arrays.asList(listing));
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
        PortfolioListing listing = portfolioListingDao.findByListingIdAndDeletedFlag(listingId, false);
        if (listing == null) {
            logger.error("Portfolio Listing id {} not found for userid {}", listingId, userId);
            throw new ResourceNotAvailableException(ResourceType.LISTING, ResourceTypeAction.GET);
        }
        listing.updateLoanInterest(interestedToLoan, loanType);
        updateOtherSpecificData(Arrays.asList(listing));
        sendMail(userId, listing, MailType.LISTING_HOME_LOAN_CONFIRM_TO_USER);
        sendMail(userId, listing, MailType.LISTING_HOME_LOAN_CONFIRM_TO_INTERNAL);
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
        MailDetails mailDetails = null;
        switch (mailTypeEnum) {
            case LISTING_ADD_MAIL_TO_USER:
                ListingAddMail listingAddMail = listing.createListingAddMailObject();
                mailBody = mailBodyGenerator.generateMailBody(
                        MailTemplateDetail.ADD_NEW_PORTFOLIO_LISTING,
                        listingAddMail);
                mailDetails = new MailDetails(mailBody).setMailTo(toStr);
                return mailSender.sendMailUsingAws(mailDetails);
            case LISTING_HOME_LOAN_CONFIRM_TO_USER:
                ListingLoanRequestMail listingLoanRequestMail = listing.createListingLoanRequestObj();
                mailBody = mailBodyGenerator.generateMailBody(
                        MailTemplateDetail.LISTING_LOAN_REQUEST_USER,
                        listingLoanRequestMail);
                mailDetails = new MailDetails(mailBody).setMailTo(toStr);
                return mailSender.sendMailUsingAws(mailDetails);
            case LISTING_HOME_LOAN_CONFIRM_TO_INTERNAL:
                ListingLoanRequestMail listingLoanRequestMailInternal = listing.createListingLoanRequestObj();
                mailBody = mailBodyGenerator.generateMailBody(
                        MailTemplateDetail.LISTING_LOAN_REQUEST_INTERNAL,
                        listingLoanRequestMailInternal);
                toStr = propertyReader.getRequiredProperty(PropertyKeys.MAIL_HOME_LOAN_INTERNAL_RECIEPIENT);
                mailDetails = new MailDetails(mailBody).setMailTo(toStr);
                return mailSender.sendMailUsingAws(mailDetails);
            case INTERESTED_TO_SELL_PROPERTY_INTERNAL:
                ListingResaleMail listingResaleMailInternal = listing.createListingResaleMailObj(websiteHost);
                mailBody = mailBodyGenerator.generateMailBody(
                        MailTemplateDetail.INTERESTED_TO_SELL_PROPERTY_INTERNAL,
                        listingResaleMailInternal);
                toStr = propertyReader.getRequiredProperty(PropertyKeys.MAIL_INTERESTED_TO_SELL_RECIEPIENT);
                mailDetails = new MailDetails(mailBody).setMailTo(toStr);
                return mailSender.sendMailUsingAws(mailDetails);
            case INTERESTED_TO_SELL_PROPERTY_USER:
                ListingResaleMail listingResaleMailUser = listing.createListingResaleMailObj(websiteHost);
                mailBody = mailBodyGenerator.generateMailBody(
                        MailTemplateDetail.INTERESTED_TO_SELL_PROPERTY_USER,
                        listingResaleMailUser);
                mailDetails = new MailDetails(mailBody).setMailTo(toStr);
                return mailSender.sendMailUsingAws(mailDetails);
            default:
                throw new IllegalArgumentException("Invalid mail type");
        }

    }

    public PortfolioListing sellYourProperty(PortfolioListing portfolioListing) {
        ForumUser forumUser = null;
        if (portfolioListing.getUserId() != null) {
            forumUser = forumUserDao.findOne(portfolioListing.getUserId());
            if (forumUser == null) {
                throw new ResourceNotAvailableException(ResourceType.USER, ResourceTypeAction.GET);
            }
        }

        if (portfolioListing.getTypeId() != null) {
            Property property = propertyService.getProperty(portfolioListing.getTypeId());
            if (property == null) {
                throw new ResourceNotAvailableException(ResourceType.PROPERTY, ResourceTypeAction.GET);
            }
            portfolioListing.setProjectId(property.getProjectId());
            portfolioListing.setLocalityId(property.getProject().getLocalityId());
            portfolioListing.setCityId(property.getProject().getLocality().getSuburb().getCityId());
        }
        else if (portfolioListing.getProjectId() != null) {
            Project project = projectService.getProjectData(portfolioListing.getProjectId());

            portfolioListing.setLocalityId(project.getLocalityId());
            portfolioListing.setCityId(project.getLocality().getSuburb().getCityId());
        }
        else if (portfolioListing.getLocalityId() != null) {
            Locality locality = localityService.getLocality(portfolioListing.getLocalityId());
            if (locality == null) {
                throw new ResourceNotAvailableException(ResourceType.LOCALITY, ResourceTypeAction.GET);
            }
            portfolioListing.setCityId(locality.getSuburb().getCityId());
        }
        else if (portfolioListing.getCityId() != null) {
            /*
             * checking whether city id is valid or not, if not a valid city
             * then cityService will throw an exception
             */
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
        if (savePortfolioListing == null) {
            throw new PersistenceException("Sell your property request cannot be saved.");
        }
        savePortfolioListing.setForumUser(forumUser);
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

        if (mailToAddress.length() < 1) {
            logger.error("Project/Property Error Reporting is not able to send mail as 'to' mail recipients is empty. The application properties property (mail.report.error.to.recipient) is empty.");
            return false;
        }

        MailBody mailBody = mailBodyGenerator.generateMailBody(MailTemplateDetail.SELL_YOUR_PROPERTY, portfolioListing);
        MailDetails mailDetails = new MailDetails(mailBody).setMailTo(mailToAddress).setMailCC(mailCCAddress);
        return mailSender.sendMailUsingAws(mailDetails);

    }

}
