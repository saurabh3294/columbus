package com.proptiger.data.util.portfolio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.proptiger.core.enums.ListingStatus;
import com.proptiger.core.enums.ReturnType;
import com.proptiger.core.model.cms.Project;
import com.proptiger.core.model.cms.Property;
import com.proptiger.core.model.proptiger.Image;
import com.proptiger.core.model.proptiger.OverallReturn;
import com.proptiger.core.model.proptiger.PortfolioListing;
import com.proptiger.core.model.proptiger.PortfolioListingPrice;
import com.proptiger.data.model.user.portfolio.Portfolio;

/**
 * Portfolio Utility methods. reference
 * 
 * @author Rajeev Pandey
 * 
 */
public class PortfolioUtil {

    /**
     * Updates price information in Portfolio object
     * 
     * @param portfolio
     * @param listings
     */
    public static void updatePriceInfoInPortfolio(Portfolio portfolio, List<PortfolioListing> listings) {
        double originalValue = 0.0;
        double currentValue = 0.0;
        if (listings != null) {
            for (PortfolioListing listing : listings) {
                if (listing.getListingStatus() == ListingStatus.INCOMPLETE) {  //Incomplete Listings not to be counted for Overall Return
                    continue;
                }
                originalValue += listing.getTotalPrice();
                listing.setCurrentPrice(getListingCurrentPrice(listing));
                currentValue += listing.getCurrentPrice();
            }
        }
        portfolio.setCurrentValue(currentValue);
        portfolio.setOriginalValue(originalValue);
        OverallReturn overallReturn = getOverAllReturn(originalValue, currentValue);
        portfolio.setOverallReturn(overallReturn);
    }

    /**
     * This method returns current value of listing and if that is 0 then it
     * will return total price as current price.
     * 
     * @param listing
     * @return
     */
    public static double getListingCurrentPrice(PortfolioListing listing) {
        double currentValue = 0.0;
        Double size = listing.getListingSize();
        if (size == null) {
            size = 0.0D;
        }
        Double pricePerUnitArea = null;
        if (listing.getProperty() != null && listing.getProperty().getPricePerUnitArea() != null) {
            pricePerUnitArea = listing.getProperty().getPricePerUnitArea();
            currentValue = size * pricePerUnitArea;
        }
        if (currentValue == 0.0D && listing.getTotalPrice() != null) {   
            currentValue = listing.getTotalPrice();
        }
        else {
            if (listing.getOtherPrices() != null && !listing.getOtherPrices().isEmpty()) {
                for (PortfolioListingPrice listingPrice : listing.getOtherPrices()) {
                    currentValue += listingPrice.getAmount();
                }
            }
        }
        return currentValue;
    }

    /**
     * Calculates the overall return
     * 
     * @param originalValue
     * @param currentValue
     * @return
     */
    public static OverallReturn getOverAllReturn(Double originalValue, Double currentValue) {
        OverallReturn overallReturn = new OverallReturn();
        if(originalValue == null || currentValue == null){
            return overallReturn;
        }
        double changeAmt = currentValue - originalValue;
        overallReturn.setChangeAmount(changeAmt);
        if (originalValue == 0.0D) {
            overallReturn.setChangePercent(0);
        }
        else {
            double div = changeAmt / originalValue;
            div = div * 100;
            overallReturn.setChangePercent(div);
        }
        if (changeAmt < 0) {
            overallReturn.setReturnType(ReturnType.DECLINE);
        }
        else if (changeAmt > 0) {
            overallReturn.setReturnType(ReturnType.APPRECIATION);
        }
        else {
            overallReturn.setReturnType(ReturnType.NOCHANGE);
        }
        return overallReturn;
    }

    public static Map<Integer, List<Image>> getPropertyIdToImageMap(List<Image> propertyImages) {
        Map<Integer, List<Image>> map = new HashMap<>();
        if (propertyImages != null && !propertyImages.isEmpty()) {
            for (Image i : propertyImages) {
                Integer propertyId = new Long(i.getObjectId()).intValue();
                if (map.get(propertyId) == null) {
                    map.put(propertyId, new ArrayList<Image>());
                }
                map.get(propertyId).add(i);
            }
        }
        return map;
    }

    public static Map<Integer, List<Image>> getProjectIdToImageMap(List<Image> projectImages) {
        Map<Integer, List<Image>> map = new HashMap<>();
        if (projectImages != null && !projectImages.isEmpty()) {
            for (Image i : projectImages) {
                Integer projectId = new Long(i.getObjectId()).intValue();
                if (map.get(projectId) == null) {
                    map.put(projectId, new ArrayList<Image>());
                }
                map.get(projectId).add(i);
            }
        }
        return map;
    }

    public static Map<Integer, Project> createProjectIdMap(List<Property> properties) {
        Map<Integer, Project> map = new HashMap<>();
        for (Property p : properties) {
            map.put(p.getProjectId(), p.getProject());
        }
        return map;
    }

}
