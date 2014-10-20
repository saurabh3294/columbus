package com.proptiger.data.service.user.portfolio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.model.EmptyBatchRequestException;
import com.proptiger.data.enums.ConstructionStatus;
import com.proptiger.data.enums.portfolio.ListingStatus;
import com.proptiger.data.enums.resource.ResourceType;
import com.proptiger.data.enums.resource.ResourceTypeAction;
import com.proptiger.data.internal.dto.PortfolioPriceTrend;
import com.proptiger.data.internal.dto.PriceDetail;
import com.proptiger.data.internal.dto.ProjectPriceTrend;
import com.proptiger.data.internal.dto.ProjectPriceTrendInput;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.user.portfolio.PortfolioListing;
import com.proptiger.data.model.user.portfolio.PortfolioListingPrice;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.LimitOffsetPageRequest;
import com.proptiger.data.repo.ProjectDBDao;
import com.proptiger.data.repo.user.portfolio.PortfolioListingDao;
import com.proptiger.data.service.B2BAttributeService;
import com.proptiger.data.service.ProjectPriceTrendService;
import com.proptiger.data.service.ProjectService;
import com.proptiger.data.util.Constants;
import com.proptiger.data.util.DateUtil;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 * This class provides price trend for portfolio and for a particular listing of
 * user
 * 
 * @author Rajeev Pandey
 * 
 */
@Service
public class PortfolioPriceTrendService {

    private static Logger            logger         = LoggerFactory.getLogger(PortfolioPriceTrendService.class);

    private static Integer           MONTHS_IN_YEAR = 12;
    @Autowired
    private PortfolioListingDao      portfolioListingDao;

    @Autowired
    private PortfolioService         portfolioService;
    @Autowired
    private ProjectPriceTrendService projectPriceTrendService;

    @Autowired
    private ProjectDBDao             projectDBDao;

    @Autowired
    private ProjectService           projectService;

    @Autowired
    private B2BAttributeService      b2bAttributeService;

    @Value("${b2b.price-inventory.max.month.dblabel}")
    private String                   currentMonthDbLabel;

    public String                    trendCurrentMonth;

    @PostConstruct
    private void initialize() {
        trendCurrentMonth = b2bAttributeService.getAttributeByName(currentMonthDbLabel);
    }
    
    /**
     * Get price trend for a listing associated with user
     * 
     * @param userId
     * @param listingId
     * @param noOfMonths
     * @return
     */
    public ProjectPriceTrend getListingPriceTrend(Integer userId, Integer listingId, Integer noOfMonths) {
        logger.debug("Price trend for user id {} and listing id {} for months {}", userId, listingId, noOfMonths);
        PortfolioListing listing = portfolioListingDao.findByUserIdAndListingIdAndListingStatusIn(userId, 
                listingId,
                Constants.LISTINGSTATUS_LIST);
        if (listing == null) {
            throw new ResourceNotAvailableException(ResourceType.LISTING, ResourceTypeAction.GET);
        }

        List<PortfolioListing> listings = new ArrayList<PortfolioListing>();
        listings.add(listing);
        List<ProjectPriceTrend> projectPriceTrend = getProjectPriceTrends(noOfMonths, listings);
        if (projectPriceTrend != null && projectPriceTrend.size() > 0) {
            return projectPriceTrend.get(0);
        }

        return new ProjectPriceTrend();
    }

    /**
     * Setting project name in each portfolio listing
     * 
     * @param listings
     */
    private void updateProjectName(List<PortfolioListing> listings) {
        for (PortfolioListing listing : listings) {
            if (listing.getProperty() != null) {
                listing.setProjectName(projectDBDao.getProjectNameById(listing.getProperty().getProjectId()));
            }
        }
    }

    /**
     * Calculate Portfolio price trend for the properties associated with user,
     * In case of no listing present for user, empty response will be returned
     * 
     * @param userId
     * @param noOfMonths
     * @param selector
     * @return
     */
    public PortfolioPriceTrend getPortfolioPriceTrend(Integer userId, Integer noOfMonths, FIQLSelector selector) {
        PortfolioPriceTrend portfolioPriceTrend = new PortfolioPriceTrend();
        logger.debug("Price trend for user id {} for months {}", userId, noOfMonths);
        List<PortfolioListing> listings = portfolioListingDao
                .findByUserIdAndSourceTypeInAndListingStatusInOrderByListingIdDesc(
                        userId,
                        Constants.SOURCETYPE_LIST,
                        Arrays.asList(ListingStatus.ACTIVE),
                        LimitOffsetPageRequest.createPageableDefaultRowsAll(selector));
        if (listings == null || listings.size() == 0) {
            List<ProjectPriceTrend> list = new ArrayList<>();
            portfolioPriceTrend.setProjectPriceTrend(list);
            return portfolioPriceTrend;
        }
        List<ProjectPriceTrend> projectPriceTrendTemp = getProjectPriceTrends(noOfMonths, listings);
        portfolioPriceTrend.setProjectPriceTrend(projectPriceTrendTemp);
        return portfolioPriceTrend;
    }

    /**
     * @param noOfMonths
     * @param listings
     * @return
     */
    private List<ProjectPriceTrend> getProjectPriceTrends(Integer noOfMonths, List<PortfolioListing> listings) {
        portfolioService.setPropertyInListings(listings);
        List<ProjectPriceTrend> projectPriceTrendTemp = new ArrayList<ProjectPriceTrend>();
        if (!listings.isEmpty()) {
            updateProjectName(listings);
            List<ProjectPriceTrendInput> inputs = createProjectPriceTrendInputs(listings);
            projectPriceTrendTemp = projectPriceTrendService.getProjectPriceHistory(inputs, noOfMonths);
            /*
             * Now add price trend from current month and make price trend
             * number equal to noOfMonths
             */
            addPriceDetailsFromCurrentMonth(projectPriceTrendTemp, noOfMonths, listings);
            /*
             * Update Price with total price of Listing
             */
            updatePriceAsTotalListingPriceInTrend(projectPriceTrendTemp, listings);
        }
        return projectPriceTrendTemp;
    }

    /**
     * <<<<<<< HEAD ======= Changing price trend date to month precision as it
     * would be easy to plot on UI.
     * 
     * @param projectPriceTrendTemp
     */
    private void makePriceTrendDateMonthPrecision(List<ProjectPriceTrend> projectPriceTrendTemp) {
        Calendar cal = Calendar.getInstance();
        for (ProjectPriceTrend priceTrend : projectPriceTrendTemp) {
            for (PriceDetail priceDetail : priceTrend.getPrices()) {
                Date d = priceDetail.getEffectiveDate();
                cal.setTime(d);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 1);
                cal.set(Calendar.MINUTE, 1);
                cal.set(Calendar.SECOND, 1);
                cal.set(Calendar.MILLISECOND, 1);
                priceDetail.setEffectiveDate(cal.getTime());
            }
        }

    }

    /**
     * >>>>>>> 6e01daf2709cf4a6f7214140f0a135ab6ec2847d There may be cases when
     * we do not get price trend for no of months specified from Trend API. This
     * method will make sure there are price trend for specified no of months,
     * so this method may add PriceTrend at last and at first if not present,
     * and after adding make sure that there are {noOfMonths} price trend object
     * only. To add at last it will take last price trend object and clone to
     * add till current month, And to add at first it will take first price
     * trend object and clone to add at first till specified number of months
     * condition met
     * 
     * @param projectPriceTrends
     * @param noOfMonths
     * @param listings
     */
    private void addPriceDetailsFromCurrentMonth(
            List<ProjectPriceTrend> projectPriceTrends,
            Integer noOfMonths,
            List<PortfolioListing> listings) {

        Map<Integer, Date> idLauchDateMap = getLaunchOrPreLaunchDate(projectPriceTrends);
        for (ProjectPriceTrend priceTrend : projectPriceTrends) {
            Calendar cal = Calendar.getInstance();
            Date currentDate = cal.getTime();
            logger.debug(
                    "Adding price detail from current month for project id {} and name {}",
                    priceTrend.getProjectId(),
                    priceTrend.getProjectName());
            List<PriceDetail> prices = priceTrend.getPrices();
            if (prices == null) {
                // Could not get price trend for this project id from Trend API,
                // Add current per square unit price as price trend for this
                // project
                prices = new ArrayList<>();
                PriceDetail priceDetail = new PriceDetail();
                PortfolioListing listingForCurrentProject = getListingForProject(priceTrend, listings);
                priceDetail.setEffectiveDate(DateUtil.parseYYYYmmddStringToDate(trendCurrentMonth));
                Double pricePerUnitArea = listingForCurrentProject.getProperty().getPricePerUnitArea();
                if (pricePerUnitArea == null) {
                    pricePerUnitArea = Math.rint(listingForCurrentProject.getTotalPrice() / listingForCurrentProject
                            .getListingSize());
                }
                priceDetail.setPrice(pricePerUnitArea);
                prices.add(priceDetail);
                priceTrend.setPrices(prices);
            }
            PriceDetail lastPriceTrend = prices.get(prices.size() - 1);
            Date lastDatePresent = lastPriceTrend.getEffectiveDate();
            cal.setTime(lastDatePresent);

            // Add price detail for remaining last months till current month
            while (lastDatePresent.compareTo(currentDate) <= 0) {
                PriceDetail detail = new PriceDetail();
                detail.setPrice(lastPriceTrend.getPrice());
                cal.add(Calendar.MONTH, 1);
                detail.setEffectiveDate(cal.getTime());
                lastDatePresent = cal.getTime();
                prices.add(prices.size(), detail);
            }

            // Remove Prices after Current Month
            removePriceTrendDateAfterSpecifiedMonth(prices);

            // Add price detail at starting months
            if (!prices.isEmpty()) {
                PriceDetail firstPriceTrend = prices.get(0);
                Date firstDatePresent = firstPriceTrend.getEffectiveDate();
                cal.setTime(firstDatePresent);
                Date launchDate = idLauchDateMap.get(priceTrend.getProjectId());
                while (prices.size() <= noOfMonths && (launchDate == null || launchDate.before(cal.getTime()))) {
                    PriceDetail detail = new PriceDetail();
                    detail.setPrice(firstPriceTrend.getPrice());
                    cal.add(Calendar.MONTH, -1);
                    detail.setEffectiveDate(cal.getTime());
                    prices.add(0, detail);
                }

                /*
                 * removing price detail before launch date.
                 */
                while (launchDate != null && !prices.isEmpty() && prices.get(0).getEffectiveDate().before(launchDate)) {
                    prices.remove(0);
                }
                includeMissingPriceTrendData(cal, prices);
                removeExtraPriceTrendThanRequired(noOfMonths, prices);
            }

        }
    }

    /**
     * @param projectPriceTrends
     * @return Creating Map of Launch-Date or Pre-Launch-Date against Project-Id
     */
    private Map<Integer, Date> getLaunchOrPreLaunchDate(List<ProjectPriceTrend> projectPriceTrends) {
        Map<Integer, Date> idLaunchDateMap = new HashMap<Integer, Date>();
        Set<Integer> idSet = new HashSet<Integer>();
        for (ProjectPriceTrend priceTrend : projectPriceTrends) {
            idSet.add(priceTrend.getProjectId());
        }
        List<Project> projectList = projectService.getProjectsByIds(idSet);
        if (projectList != null) {
            for (Project project : projectList) {
                Integer projectId = project.getProjectId();
                if (project.getProjectStatus().equalsIgnoreCase(ConstructionStatus.PreLaunch.getStatus())) {
                    idLaunchDateMap.put(projectId, project.getPreLaunchDate());
                }
                else {
                    idLaunchDateMap.put(projectId, project.getLaunchDate());
                }
            }
        }
        return idLaunchDateMap;
    }

    private void includeMissingPriceTrendData(Calendar cal, List<PriceDetail> prices) {
        /*
         * Check if any month data is missing in between
         */
        if (prices.size() > 1) {
            int pricesSize = prices.size();
            PriceDetail last = prices.get(prices.size() - 1);
            cal.setTime(last.getEffectiveDate());
            int lastMonth = cal.get(Calendar.MONTH);
            logger.debug("In adding missing month block");
            for (int counter = pricesSize - 2; counter >= 0; counter--) {
                PriceDetail temp = prices.get(counter);
                cal.setTime(temp.getEffectiveDate());
                int tempLastMonth = cal.get(Calendar.MONTH);
                int currMonth = tempLastMonth;
                if ((currMonth + 1) % MONTHS_IN_YEAR == lastMonth) {
                    // Found continuous month, so skip
                }
                else {
                    // add missing month price details taking last price
                    // detail data
                    int i = 1;
                    while ((currMonth + 1) % MONTHS_IN_YEAR != lastMonth) {
                        PriceDetail newPriceDetail = new PriceDetail();
                        cal.add(Calendar.MONTH, 1);
                        currMonth = cal.get(Calendar.MONTH);
                        newPriceDetail.setEffectiveDate(cal.getTime());
                        newPriceDetail.setPrice(last.getPrice());
                        prices.add(counter + i++, newPriceDetail);
                    }
                }
                last = temp;
                lastMonth = tempLastMonth;
            }
            logger.debug("After adding missing month block");
        }
    }

    private void removeExtraPriceTrendThanRequired(Integer noOfMonths, List<PriceDetail> prices) {
        /*
         * If there are more price details than required then remove from first
         */
        if (prices.size() > noOfMonths) {
            // remove from first
            int removeCounter = 0;
            int toRemove = prices.size() - noOfMonths;
            while (removeCounter < toRemove) {
                prices.remove(0);
                removeCounter++;
            }
        }
    }

    /*
     * Removing price trend after specified month provided in
     * application.properties.
     */
    private void removePriceTrendDateAfterSpecifiedMonth(List<PriceDetail> prices) {
        if (prices != null) {
            Date trendCurrentDate = DateUtil.parseYYYYmmddStringToDate(trendCurrentMonth);
            Calendar cal = Calendar.getInstance();
            cal.setTime(trendCurrentDate);
            cal.add(Calendar.MONTH, 1); // Adding one month to include current
                                        // month trend
            trendCurrentDate = cal.getTime();
            while (!prices.isEmpty() && !prices.get(prices.size() - 1).getEffectiveDate().before(trendCurrentDate)) {
                prices.remove(prices.size() - 1);
            }
        }
    }

    /**
     * CMS API gives per square unit price, This method updates the prices
     * receive from CMS API as total price for property size
     * 
     * @param projectPriceTrends
     * @param listings
     */
    private void updatePriceAsTotalListingPriceInTrend(
            List<ProjectPriceTrend> projectPriceTrends,
            List<PortfolioListing> listings) {
        Iterator<ProjectPriceTrend> priceTrendItr = projectPriceTrends.iterator();
        while (priceTrendItr.hasNext()) {
            ProjectPriceTrend projectPriceTrend = priceTrendItr.next();
            PortfolioListing listing = getListingForProject(projectPriceTrend, listings);
            if (listing != null) {
                Double size = listing.getListingSize();
                if (size == null) {
                    size = 0.0D;
                }
                /*
                 * Adding other pricess too in price trend becoz while getting
                 * portfolio/listing we add other prices in current price
                 * 
                 * TODO change this if we change in get portfolio or listing
                 */
                double totalOtherPrice = getTotalOtherPrice(listing.getOtherPrices());
                // double totalOtherPrice = 0.0D;
                if (projectPriceTrend.getPrices() != null) {
                    for (PriceDetail priceDetail : projectPriceTrend.getPrices()) {
                        if (priceDetail.getPrice() == 0.0D) {
                            priceDetail.setPricePerUnitArea(Math.rint(listing.getTotalPrice() / listing
                                    .getListingSize()));
                            priceDetail.setPrice(listing.getTotalPrice());

                        }
                        else {
                            double totPrice = priceDetail.getPrice();
                            priceDetail.setPricePerUnitArea(priceDetail.getPrice());
                            totPrice = totPrice * size + totalOtherPrice;
                            priceDetail.setPrice((int) totPrice);

                        }

                    }
                }

            }
            else {
                priceTrendItr.remove();
            }
        }
    }

    /**
     * Calculate total other price
     * 
     * @param otherPrices
     * @return
     */
    private double getTotalOtherPrice(Set<PortfolioListingPrice> otherPrices) {
        double price = 0.0D;
        if (otherPrices != null) {
            for (PortfolioListingPrice listingPrice : otherPrices) {
                price = price + listingPrice.getAmount();
            }
        }
        return price;
    }

    /**
     * @param projectPriceTrend
     * @param listings
     * @return
     */
    private PortfolioListing getListingForProject(ProjectPriceTrend projectPriceTrend, List<PortfolioListing> listings) {
        for (PortfolioListing listing : listings) {
            if (listing.getTypeId().equals(projectPriceTrend.getTypeId()) && listing.getProperty().getProjectId() == projectPriceTrend
                    .getProjectId().intValue()) {
                return listing;
            }
        }
        return null;
    }

    /**
     * Creating ProjectPriceTrendInput
     * 
     * @param listings
     * @return
     */
    public List<ProjectPriceTrendInput> createProjectPriceTrendInputs(List<PortfolioListing> listings) {
        List<ProjectPriceTrendInput> inputs = new ArrayList<ProjectPriceTrendInput>();
        for (PortfolioListing listing : listings) {
            ProjectPriceTrendInput input = new ProjectPriceTrendInput();
            input.setListingName(listing.getName());
            input.setProjectId(listing.getProperty().getProjectId());
            input.setTypeId(listing.getTypeId());
            input.setProjectName(listing.getProjectName());
            input.setBedrooms(listing.getProperty().getBedrooms());
            inputs.add(input);
        }
        return inputs;
    }

}