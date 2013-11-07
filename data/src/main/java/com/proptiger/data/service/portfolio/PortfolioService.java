package com.proptiger.data.service.portfolio;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.model.Enquiry;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.ProjectDB;
import com.proptiger.data.model.ProjectPaymentSchedule;
import com.proptiger.data.model.ProjectType;
import com.proptiger.data.model.portfolio.OverallReturn;
import com.proptiger.data.model.portfolio.Portfolio;
import com.proptiger.data.model.portfolio.PortfolioListing;
import com.proptiger.data.model.portfolio.PortfolioListingPaymentPlan;
import com.proptiger.data.model.portfolio.PortfolioListingPrice;
import com.proptiger.data.model.portfolio.ReturnType;
import com.proptiger.data.model.resource.NamedResource;
import com.proptiger.data.model.resource.Resource;
import com.proptiger.data.repo.ForumUserDao;
import com.proptiger.data.repo.LocalityDao;
import com.proptiger.data.repo.ProjectDBDao;
import com.proptiger.data.repo.ProjectPaymentScheduleDao;
import com.proptiger.data.repo.portfolio.PortfolioListingDao;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.exception.ConstraintViolationException;
import com.proptiger.exception.DuplicateNameResourceException;
import com.proptiger.exception.ResourceAlreadyExistException;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 * This class provides CRUD operations over a property that is a addressable entity
 * 
 * @author Rajeev Pandey
 *
 */
@Service
public class PortfolioService extends AbstractService{

	private static Logger logger = LoggerFactory.getLogger(PortfolioService.class);
	@Autowired
	private PortfolioListingDao portfolioListingDao;
	
	@Autowired
	private LeadGenerationService leadGenerationService;
	
	@Autowired
	private ProjectDBDao projectDBDao;
	
	@Autowired
	private LocalityDao localityDao;
	
	@Autowired
	private ProjectPaymentScheduleDao paymentScheduleDao;
	
	@Autowired
	private PropertyReader propertyReader;
	
	@Autowired
	private ForumUserDao forumUserDao;
	/**
	 * Get portfolio object for a particular user id
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Portfolio getPortfolioByUserId(Integer userId){
		Portfolio portfolio = new Portfolio();
		List<PortfolioListing> listings = portfolioListingDao.findByUserId(userId);
		//portfolio.setPortfolioListings(listings);
		updatePriceInfoInPortfolio(portfolio, listings);
		updatePaymentSchedule(listings);
		if(listings != null){
			for(PortfolioListing l: listings){
				portfolio.addListings(l.getId());
			}
		}
		
		return portfolio;
	}
	/**
	 * Updates price information in Portfolio object
	 * @param portfolio
	 * @param listings
	 */
	private void updatePriceInfoInPortfolio(Portfolio portfolio, List<PortfolioListing> listings) {
		double originalValue = 0.0D;
		double currentValue = 0.0D;
		if(listings != null){
			for(PortfolioListing listing: listings){
				originalValue += listing.getTotalPrice();
				listing.setCurrentPrice(listing.getProjectType().getSize() * listing.getProjectType().getPricePerUnitArea());
				currentValue += listing.getCurrentPrice();
			}
		}
		if(currentValue == 0.0D){
			logger.debug("Current value not available for Portfolio");
			currentValue = originalValue;
		}
		portfolio.setCurrentValue(currentValue);
		portfolio.setOriginalValue(originalValue);
		OverallReturn overallReturn = getOverAllReturn(originalValue,
				currentValue);
		portfolio.setOverallReturn(overallReturn );
	}
	/**
	 * Calculates the overall return 
	 * @param originalValue
	 * @param currentValue
	 * @return
	 */
	private OverallReturn getOverAllReturn(double originalValue,
			double currentValue) {
		OverallReturn overallReturn = new OverallReturn();
		double changeAmt = currentValue - originalValue;
		overallReturn.setChangeAmount(Math.abs(changeAmt));
		if(originalValue == 0.0D){
			overallReturn.setChangePercent(0.0D);
		}
		else{
			overallReturn.setChangePercent((Math.abs(changeAmt)/originalValue)*100);
		}
		if(changeAmt < 0){
			overallReturn.setReturnType(ReturnType.DECLINE);
		}
		else if(changeAmt > 0){
			overallReturn.setReturnType(ReturnType.APPRECIATION);
		}
		else{
			overallReturn.setReturnType(ReturnType.NOCHANGE);
		}
		return overallReturn;
	}
	
	/**
	 * Creating a logical entity Portfolio, that consists a list of PortfolioListing objects
	 * 
	 * @param userId
	 * @param portfolio
	 * @return
	 */
	public Portfolio createPortfolio(Integer userId, Portfolio portfolio) {
		List<PortfolioListing> presentListing = portfolioListingDao.findByUserId(userId);
		List<PortfolioListing> toCreate = portfolio.getPortfolioListings();
		if (presentListing != null && presentListing.size() > 0) {
			logger.error("Portfolio exists for userid {}", userId);
			throw new ResourceAlreadyExistException("Portfolio exist for user id "+userId);
		}
		createPortfolioListings(userId, toCreate);
		Portfolio created = new Portfolio();
		List<PortfolioListing> listings = portfolioListingDao.findByUserId(userId);
		created.setPortfolioListings(listings);
		updatePriceInfoInPortfolio(created, listings);
		return created;
	}
	
	private void createPortfolioListings(Integer userId, List<PortfolioListing> toCreateList){
		if(toCreateList != null){
			for(PortfolioListing toCreate: toCreateList){
				createPortfolioListing(userId, toCreate);
			}
		}
	}
	/**
	 * This method update portfolio for user id. If no portfolio listing exist then it will
	 * create portfolio listing, and if listing is already present then it will update.
	 * 
	 * If any of the existing listing not passed to be updated in portfolio object, then that 
	 * listing will be deleted from database.
	 * 
	 * If any existing listing passed to be updated without passing id, then it will be treated as
	 * new listing to create.
	 * 
	 * existing listings
	 * @param userId
	 * @param portfolio
	 * @return
	 */
	public Portfolio updatePortfolio(Integer userId, Portfolio portfolio){
		List<PortfolioListing> presentListingList = portfolioListingDao.findByUserId(userId);
		Portfolio updated = new Portfolio();
		if (presentListingList == null || presentListingList.size() == 0) {
			logger.debug("No portfolio listing exists for userid {}", userId);
			/*
			 * create new portfolio
			 */
			
			createPortfolioListings(userId, portfolio.getPortfolioListings());
			
		}
		else{
			updated = createOrUpdatePortfolioListings(userId, portfolio, presentListingList);
		}
		List<PortfolioListing> updatedListings = portfolioListingDao.findByUserId(userId);
		updated.setPortfolioListings(updatedListings);
		/*
		 * Updating price information in portfolio
		 */
		updatePriceInfoInPortfolio(updated, updatedListings);
		return updated;
	}
	
	@Transactional(rollbackFor = {ConstraintViolationException.class, DuplicateNameResourceException.class})
	private Portfolio createOrUpdatePortfolioListings(Integer userId,
			Portfolio toUpdatePortfolio, List<PortfolioListing> presentListingList) {
		/*
		 * Either a new Listing will be created if not already present otherwise will be updated
		 */
		List<Integer> updatedOrCreatedListings = new ArrayList<Integer>();
		/*
		 * Few listings already mapped with user id, there might be some new listings
		 * to be created and few might need to update
		 */
		Portfolio updatedPortfolio = new Portfolio();
		List<PortfolioListing> toUpdateList = toUpdatePortfolio.getPortfolioListings();
		for(PortfolioListing toUpdate: toUpdateList){
			if(toUpdate.getId() == null){
				/*
				 * Need to create new Listing, and adding that to portfolio
				 */
				PortfolioListing newListing = createPortfolioListing(userId, toUpdate);
				updatedOrCreatedListings.add(newListing.getId());
				updatedPortfolio.addPortfolioListings(newListing);
			}
			else{
				/*
				 * Check if toUpdate is already present in database, if present then update that
				 * otherwise create
				 */
				boolean isUpdated = false;
				for(PortfolioListing present: presentListingList){
					if(toUpdate.getId().equals(present.getId())){
						//need to update
						present.update(toUpdate);
						updatedOrCreatedListings.add(toUpdate.getId());
						isUpdated = true;
						updatedPortfolio.addPortfolioListings(present);
						break;
					}
				}
				
				if(!isUpdated){
					/*
					 * Requested PortfolioListing object (toUpdate) is not present in database, so creating new
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
		for(PortfolioListing listingPresent: presentListingList){
			if(!updatedOrCreatedListings.contains(listingPresent.getId())){
				portfolioListingDao.delete(listingPresent.getId());
			}
			
		}
		return updatedPortfolio;
	}
	/**
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<PortfolioListing> getAllPortfolioListings(Integer userId){
		List<PortfolioListing> listings = portfolioListingDao.findByUserId(userId);
		if(listings != null){
			for(PortfolioListing listing: listings){
				listing.setCurrentPrice(listing.getProjectType().getSize() * listing.getProjectType().getPricePerUnitArea());
				updateProjectSpecificData(listing);
			}
			updatePaymentSchedule(listings);
		}
		return listings;
	}
	private void updateProjectSpecificData(PortfolioListing listing) {
		ProjectDB project = projectDBDao.findOne(listing.getProjectType().getProjectId());
		if(project != null){
			listing.setProjectName(project.getProjectName());
			listing.setBuilderName(project.getBuilderName());
			listing.setCompletionDate(project.getCompletionDate());
			listing.setProjectStatus(project.getProjectStatus());
			Locality locality = localityDao.findOne(project.getLocalityId());
			if(locality != null){
				listing.setLocality(locality.getLabel());
			}
		}
		
	}
	
	/**
	 * Get a PortfolioProperty for particular user id and PortfolioProperty id
	 * @param userId
	 * @param propertyId
	 * @return
	 */
	@Transactional(readOnly = true)
	public PortfolioListing getPortfolioListingById(Integer userId, Integer listingId){
		PortfolioListing listing = portfolioListingDao.findByUserIdAndListingId(userId, listingId);
		if(listing == null){
			logger.error("Portfolio Listing id {} not found for userid {}",listingId, userId);
			throw new ResourceNotAvailableException("Resource not available");
		}
		updateProjectSpecificData(listing);
		listing.setCurrentPrice(listing.getProjectType().getSize() * listing.getProjectType().getPricePerUnitArea());
		updatePaymentSchedule(listing);
		OverallReturn overallReturn = getOverAllReturn(listing.getTotalPrice(),
				listing.getCurrentPrice());
		listing.setOverallReturn(overallReturn);
		return listing;
	}

	@Override
	protected <T extends Resource & NamedResource> void preProcessCreate(T resource) {
		super.preProcessCreate(resource);
		PortfolioListing toCreate = (PortfolioListing) resource;
		PortfolioListing propertyPresent = portfolioListingDao.findByUserIdAndName(toCreate.getUserId(), toCreate.getName());
		if(propertyPresent != null){
			logger.error("Duplicate resource id {} and name {}",propertyPresent.getId(), propertyPresent.getName());
			throw new DuplicateNameResourceException("Resource with same name exist");
		}
	}
	
	/**
	 * Creates a PortfolioListing
	 * @param userId
	 * @param listing
	 * @return
	 */
	@Transactional(rollbackFor = {ConstraintViolationException.class, DuplicateNameResourceException.class})
	public PortfolioListing createPortfolioListing(Integer userId, PortfolioListing listing){
		listing.setUserId(userId);
		/*
		 * Explicitly setting it to null due to use of @JsonUnwrapped, this annotation automatically
		 * set value as non null, and that create problem while creating resource.
		 * 
		 * TODO need to find better solution
		 */
		listing.setProjectType(null);
		return create(listing);
	}
	
	/**
	 * Updated an existing PortfolioListing
	 * @param userId
	 * @param propertyId
	 * @param property
	 * @return
	 */
	@Transactional(rollbackFor = ResourceNotAvailableException.class)
	public PortfolioListing updatePortfolioListing(Integer userId, Integer propertyId, PortfolioListing property){
		property.setUserId(userId);
		property.setId(propertyId);
		return update(property);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected <T extends Resource> T create(T resource) {
		PortfolioListing toCreate = (PortfolioListing) resource;
		logger.debug("Creating PortfolioProperty for userid {}",toCreate.getUserId());
		preProcessCreate(toCreate);
		PortfolioListing created = null;
		/*
		 * Creating back reference to parent in child entity, so that while saving
		 * parent, child will be saved.
		 */
		if(toCreate.getListingPrice() != null){
			for (PortfolioListingPrice listingPrice : toCreate.getListingPrice()) {
				listingPrice.setPortfolioListing(toCreate);
				//setting id null, as while creating id should not be present,
				//need to find better place to do this pre process work
				listingPrice.setListingPriceId(null);
			}
		}
		if(toCreate.getListingPaymentPlan() != null){
			for(PortfolioListingPaymentPlan listingPaymentPlan:toCreate.getListingPaymentPlan()){
				listingPaymentPlan.setPortfolioListing(toCreate);
				listingPaymentPlan.setListingPaymentPlanId(null);
			}
		}
		
		try{
			created = portfolioListingDao.save(toCreate);
		}catch(Exception exception){
			throw new ConstraintViolationException(exception.getMessage(), exception);
		}
		logger.debug("Created PortfolioProperty id {} for userid {}",created.getId(),created.getUserId());
		return (T) created;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <T extends Resource> T update(T resource) {
		PortfolioListing toUpdate = (PortfolioListing) resource;
		PortfolioListing resourcePresent = preProcessUpdate(toUpdate);
		PortfolioListing resourceWithSameName = portfolioListingDao.findByUserIdAndName(toUpdate.getUserId(), toUpdate.getName());
		if(resourceWithSameName != null){
			logger.error("Duplicate resource id {} and name {}",resourceWithSameName.getId(), resourceWithSameName.getName());
			throw new DuplicateNameResourceException("Resource with same name exist");
		}
		resourcePresent.update(toUpdate);
		return (T) resourcePresent;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <T extends Resource> T preProcessUpdate(T resource) {
		super.preProcessUpdate(resource);
		PortfolioListing toUpdate = (PortfolioListing) resource;
		PortfolioListing resourcePresent = portfolioListingDao.findOne(toUpdate.getId());
		if(resourcePresent == null){
			logger.error("PortfolioProperty id {} not found",toUpdate.getId());
			throw new ResourceNotAvailableException("Resource "+toUpdate.getId()+" not available");
		}
		return (T) resourcePresent;
	}

	/**
	 * Deletes PortfolioListing for provided user id and listing id
	 * @param userId
	 * @param propertyId
	 * @return
	 */
	@Transactional(rollbackFor = ResourceNotAvailableException.class)
	public PortfolioListing deletePortfolioListing(Integer userId, Integer listingId){
		PortfolioListing propertyPresent = portfolioListingDao.findByUserIdAndListingId(userId, listingId);
		portfolioListingDao.delete(propertyPresent);
		return propertyPresent;
	}
	
	private void updatePaymentSchedule(List<PortfolioListing> portfolioListings){
		for(PortfolioListing listing: portfolioListings){
			updatePaymentSchedule(listing);
		}
	}
	/**
	 * This method updates payment plan for portfolio listing object, if user have already added or updated payment plan
	 * @param portfolioListings
	 */
	private void updatePaymentSchedule(PortfolioListing portfolioListing){
		if (portfolioListing != null) {
			/*
			 * If PortfolioListing does not have any payment plan associated,
			 * means user is accessing this listing first time, so payment
			 * plan will be sent as template for associated project. once a payment
			 * plan is created or updated then, do not need to fetch payment
			 * plan template
			 */
			if (portfolioListing.getListingPaymentPlan() == null
					|| portfolioListing.getListingPaymentPlan().size() == 0) {
				ProjectType projectType = portfolioListing.getProjectType();
				if (projectType != null && projectType.getProjectId() != null) {
					List<ProjectPaymentSchedule> paymentScheduleList = paymentScheduleDao
							.findByProjectIdGroupByInstallmentNo(projectType.getProjectId());
					Set<PortfolioListingPaymentPlan> listingPaymentPlan = convertToPortfolioListingPaymentPlan(paymentScheduleList);
					portfolioListing.setListingPaymentPlan(listingPaymentPlan);
				}
			}

		}
	}
	private Set<PortfolioListingPaymentPlan> convertToPortfolioListingPaymentPlan(
			List<ProjectPaymentSchedule> paymentScheduleList) {
		Set<PortfolioListingPaymentPlan> list = new LinkedHashSet<PortfolioListingPaymentPlan>();
		for(ProjectPaymentSchedule paymentSchedule: paymentScheduleList){
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
	 * Updating user preference of sell interest for property based on listing id,
	 * After changing preference sending lead request
	 * @param userId
	 * @param listingId
	 * @param interestedToSell
	 * @return
	 */
	public PortfolioListing interestedToSellListing(Integer userId, Integer listingId, Boolean interestedToSell){
		logger.debug("Updating sell intereset for user id {} and listing id {} with sell interest {}",userId,listingId,interestedToSell);
		PortfolioListing listing = updateInterestedToSell(userId, listingId,
				interestedToSell);
		ForumUser user = forumUserDao.findOne(userId);
		logger.debug("Posting lead request for user id {} and listing id {} with sell interest {}",userId,listingId,interestedToSell);
		Enquiry enquiry = createEnquiryObj(listing, user);
		leadGenerationService.postLead(enquiry, LeadSaleType.RESALE, LeadPageName.PORTFOLIO);
		return listing;
	}
	private Enquiry createEnquiryObj(PortfolioListing listing, ForumUser user) {
		Enquiry enquiry = new Enquiry();
		ProjectType projectType = listing.getProjectType();
		ProjectDB project = projectDBDao.findOne(projectType.getProjectId());
		
		enquiry.setAdGrp("");
		enquiry.setCampaign("");
		enquiry.setCityId(project.getCityId());
		enquiry.setCityName("");
		enquiry.setCountryOfResidence(user.getCountryId()+"");
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
		enquiry.setPhone(user.getContact()+"");
		enquiry.setPpc("");
		enquiry.setProjectId(Long.valueOf(projectType.getProjectId()));
		enquiry.setProjectName(project.getProjectName());
		enquiry.setQuery("");
		enquiry.setSource("");
		enquiry.setUser(user.getUsername());
		enquiry.setUserMedium("");
		return enquiry;
	}
	/**
	 * Updating sell interest of user for listing
	 * @param userId
	 * @param listingId
	 * @param interestedToSell
	 * @return
	 */
	@Transactional
	private PortfolioListing updateInterestedToSell(Integer userId,
			Integer listingId, Boolean interestedToSell) {
		PortfolioListing listing = portfolioListingDao.findByUserIdAndListingId(userId, listingId);
		if(listing == null){
			logger.error("Portfolio Listing id {} not found for userid {}",listingId, userId);
			throw new ResourceNotAvailableException("Resource not available");
		}
		listing.setInterestedToSell(interestedToSell);
		listing.setInterestedToSellOn(new Date());
		return listing;
	}
}
