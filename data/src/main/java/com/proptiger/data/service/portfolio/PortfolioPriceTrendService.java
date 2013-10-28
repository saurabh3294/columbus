package com.proptiger.data.service.portfolio;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.internal.dto.PortfolioPriceTrend;
import com.proptiger.data.internal.dto.PriceDetail;
import com.proptiger.data.internal.dto.ProjectPriceTrend;
import com.proptiger.data.internal.dto.ProjectPriceTrendInput;
import com.proptiger.data.model.portfolio.PortfolioListing;
import com.proptiger.data.model.portfolio.PortfolioListingPrice;
import com.proptiger.data.repo.ProjectDBDao;
import com.proptiger.data.repo.portfolio.PortfolioListingDao;
import com.proptiger.data.service.ProjectPriceTrendService;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 * This class provides price trend for portfolio and for a particular listing of user
 * @author Rajeev Pandey
 *
 */
@Service
public class PortfolioPriceTrendService {

	private static Logger logger = LoggerFactory.getLogger(PortfolioPriceTrendService.class);
	
	@Autowired
	private PortfolioListingDao portfolioListingDao;
	
	@Autowired
	private ProjectDBDao projectDBDao;
	
	@Autowired
	private ProjectPriceTrendService projectPriceTrendService;
	
	/**
	 * Get price trend for a listing associated with user
	 * @param userId
	 * @param listingId
	 * @param noOfMonths
	 * @return
	 */
	public ProjectPriceTrend getListingPriceTrend(Integer userId, Integer listingId, Integer noOfMonths){
		logger.debug("Price trend for user id {} and listing id {} for months {}", userId, listingId, noOfMonths);
		PortfolioListing listing = portfolioListingDao.findByUserIdAndListingId(userId, listingId);
		if(listing == null){
			throw new ResourceNotAvailableException("Listing id "+listingId +" not present for user id "+userId);
		}
		List<PortfolioListing> listings = new ArrayList<PortfolioListing>();
		listings.add(listing);

		List<ProjectPriceTrendInput> inputs = createProjectPriceTrendInputs(listings);
		List<ProjectPriceTrend> projectPriceTrend = projectPriceTrendService
				.getProjectPriceHistory(inputs, noOfMonths);
		/*
		 * Now add price trend from current month and make price trend number equal to noOfMonths
		 */
		addPriceDetailsFromCurrentMonth(projectPriceTrend, noOfMonths);
		/*
		 * Update per square price received from CMS API to total price
		 */
		updatePriceAsTotalListingPriceInTrend(projectPriceTrend, listings);
		if(projectPriceTrend != null && projectPriceTrend.size() > 0){
			return projectPriceTrend.get(0);
		}
		
		return new ProjectPriceTrend();
	}
	
	/**
	 * Calculate Portfolio price trend for the properties associated with user
	 * @param userId
	 * @param noOfMonths
	 * @return
	 */
	public PortfolioPriceTrend getPortfolioPriceTrend(Integer userId,
			Integer noOfMonths) {
		logger.debug("Price trend for user id {} for months {}", userId, noOfMonths);
		List<PortfolioListing> listings = portfolioListingDao
				.findByUserId(userId);
		if(listings == null || listings.size() == 0){
			throw new ResourceNotAvailableException("No PortfolioListings for user id "+userId);
		}
		/*Map<Integer, List<Integer>> projectIdTypeIdMap = createProjectIdTypeIdMap(listings);

		List<ProjectPriceTrend> projectPriceTrend = projectPriceTrendService
				.getProjectPriceHistory(projectIdTypeIdMap, noOfMonths);*/
		
		List<ProjectPriceTrendInput> inputs = createProjectPriceTrendInputs(listings);
		List<ProjectPriceTrend> projectPriceTrendTemp = projectPriceTrendService
				.getProjectPriceHistory(inputs, noOfMonths);
		/*
		 * Now add price trend from current month and make price trend number equal to noOfMonths
		 */
		addPriceDetailsFromCurrentMonth(projectPriceTrendTemp, noOfMonths);
		/*
		 * Update per square price received from CMS API to total price
		 */
		updatePriceAsTotalListingPriceInTrend(projectPriceTrendTemp, listings);
		PortfolioPriceTrend portfolioPriceTrend = new PortfolioPriceTrend();
		portfolioPriceTrend.setProjectPriceTrend(projectPriceTrendTemp);
		updatePriceTrendForPortfolio(portfolioPriceTrend, noOfMonths);
		return portfolioPriceTrend;
	}

	/**
	 * Create List of PriceDetail object for portfolio price trend by adding corresponding 
	 * price trend from all project price trends
	 * @param portfolioPriceTrend
	 * @param noOfMonths
	 */
	private void updatePriceTrendForPortfolio(
			PortfolioPriceTrend portfolioPriceTrend, Integer noOfMonths) {
		List<PriceDetail> portfolioPriceTrendDetals = new ArrayList<>();
		for(int counter = 0; counter < noOfMonths; counter++){
			PriceDetail priceDetail = new PriceDetail();
			Date date = null;
			for(ProjectPriceTrend projectPriceTrend :portfolioPriceTrend.getProjectPriceTrend()){
				/*
				 * Ignoring ProjectPriceTrend that does not have priceDetail list
				 */
				if(projectPriceTrend.getPrices() != null){
					priceDetail.setPrice((int)(priceDetail.getPrice() + projectPriceTrend.getPrices().get(counter).getPrice()));
					if(date == null){
						date = projectPriceTrend.getPrices().get(counter).getEffectiveDate();
					}
				}
				
			}
			priceDetail.setEffectiveDate(date);
			portfolioPriceTrendDetals.add(priceDetail);
		}
		portfolioPriceTrend.setPortfolioPriceTrend(portfolioPriceTrendDetals);
	}

	/**
	 * There may be cases when we do not get price trend for no of months specified from cms API.
	 * This method will make sure there are price trend for specified no of months, so this method
	 * may add PriceTrend at last and at first if not present, and after adding make sure that
	 * there are  {noOfMonths} price trend object only.
	 * To add at last it will take last price trend object and clone to add till current month,
	 * And to add at first it will take first price trend object and clone to add at first till specified 
	 * number of months condition met
	 * 
	 * @param projectPriceTrends
	 * @param noOfMonths
	 */
	private void addPriceDetailsFromCurrentMonth(
			List<ProjectPriceTrend> projectPriceTrends, Integer noOfMonths) {
		Calendar cal = Calendar.getInstance();
		int currentMonth = cal.get(Calendar.MONTH);
		
		for(ProjectPriceTrend priceTrend: projectPriceTrends){
			List<PriceDetail> prices = priceTrend.getPrices();
			if(prices != null){
				PriceDetail lastPriceTrend = prices.get(prices.size() - 1);
				Date lastDatePresent = lastPriceTrend.getEffectiveDate();
				cal.setTime(lastDatePresent);
				int lastMonthPresent = cal.get(Calendar.MONTH);
				int monthCounter = lastMonthPresent;
				
				while(monthCounter != currentMonth){
					PriceDetail detail = new PriceDetail();
					detail.setPrice(lastPriceTrend.getPrice());
					cal.add(Calendar.MONTH, 1);
					detail.setEffectiveDate(cal.getTime());
					monthCounter = cal.get(Calendar.MONTH);
					prices.add(prices.size(), detail);
				}
				if(prices.size() > noOfMonths){
					//remove from first
					int removeCounter = 0;
					int toRemove = prices.size() - noOfMonths;
					while(removeCounter < toRemove){
						prices.remove(removeCounter);
						removeCounter++;
					}
				}
				else{
					//add at first
					PriceDetail firstPriceTrend = prices.get(0);
					Date firstDatePresent = firstPriceTrend.getEffectiveDate();
					cal.setTime(firstDatePresent);
					while(prices.size() < noOfMonths){
						PriceDetail detail = new PriceDetail();
						detail.setPrice(firstPriceTrend.getPrice());
						cal.add(Calendar.MONTH, -1);
						detail.setEffectiveDate(cal.getTime());
						prices.add(0, detail);
					}
				}
			}
			
		}
		
		
	}

	/**
	 * CMS API gives per square unit price,
	 * This method updates the prices receive from CMS API as total price for property size
	 * 
	 * @param projectPriceTrends
	 * @param listings
	 */
	private void updatePriceAsTotalListingPriceInTrend(
			List<ProjectPriceTrend> projectPriceTrends,
			List<PortfolioListing> listings) {
		Iterator<ProjectPriceTrend> priceTrendItr = projectPriceTrends.iterator();
		while(priceTrendItr.hasNext()){
			ProjectPriceTrend projectPriceTrend = priceTrendItr.next();
			PortfolioListing listing = getListingForProject(projectPriceTrend, listings);
			if(listing != null){
				Double size = listing.getProjectType().getSize();
				double totalOtherPrice = getTotalOtherPrice(listing.getListingPrice());
				if(projectPriceTrend.getPrices() != null){
					for(PriceDetail priceDetail: projectPriceTrend.getPrices()){
						double totPrice = priceDetail.getPrice();
						totPrice = totPrice * size + totalOtherPrice;
						priceDetail.setPrice((int)totPrice);
					}
				}
				
			}
			else{
				priceTrendItr.remove();
			}
		}
	}

	/** 
	 * Calculate total other price 
	 * @param otherPrices
	 * @return
	 */
	private double getTotalOtherPrice(Set<PortfolioListingPrice> otherPrices) {
		double price = 0.0D;
		for(PortfolioListingPrice listingPrice: otherPrices){
			price = price + listingPrice.getAmount();
		}
		return price;
	}

	/**
	 * @param projectPriceTrend
	 * @param listings
	 * @return
	 */
	private PortfolioListing getListingForProject(
			ProjectPriceTrend projectPriceTrend, List<PortfolioListing> listings) {
		for(PortfolioListing listing: listings){
			if (listing.getTypeId().equals(projectPriceTrend.getTypeId())
					&& listing.getProjectType().getProjectId().equals(projectPriceTrend.getProjectId())) {
				return listing;
			}
		}
		return null;
	}
	/**
	 * Creating ProjectPriceTrendInput
	 * @param listings
	 * @return
	 */
	public List<ProjectPriceTrendInput> createProjectPriceTrendInputs(List<PortfolioListing> listings){
		List<ProjectPriceTrendInput>  inputs = new ArrayList<ProjectPriceTrendInput>();
		for(PortfolioListing listing: listings){
			ProjectPriceTrendInput input = new ProjectPriceTrendInput();
			input.setName(listing.getName());
			input.setProjectId(listing.getProjectType().getProjectId());
			input.setTypeId(listing.getTypeId());
			inputs.add(input);
		}
		return inputs;
	}
}
