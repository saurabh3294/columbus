package com.proptiger.data.notification.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.ForumUser;
import com.proptiger.data.model.Listing;
import com.proptiger.data.model.user.portfolio.PortfolioListing;
import com.proptiger.data.notification.enums.Tokens;
import com.proptiger.data.notification.model.NotificationTypeGenerated;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;
import com.proptiger.data.service.marketplace.ListingService;

@Service
public class PriceChangeNotificationMessageProcessor extends NotificationMessageProcessor {

    private static Logger  logger = LoggerFactory.getLogger(PriceChangeNotificationMessageProcessor.class);

    @Autowired
    private ListingService listingService;

    @Override
    public Map<Integer, NotificationMessagePayload> getNotificationMessagePayloadByUnsubscribedUserList(
            List<ForumUser> unsubscribedUserList,
            NotificationTypeGenerated ntGenerated) {

        NotificationTypePayload notificationTypePayload = ntGenerated.getNotificationTypePayload();
        Integer listingId = ((Number) notificationTypePayload.getPrimaryKeyValue()).intValue();

        logger.debug("Getting listing for listing id: " + listingId);
        Listing listing = listingService.getListingByListingId(listingId);
        
        Integer propertyId = listing.getPropertyId();
        
        NotificationTypePayload newNTPayload = NotificationTypePayload.newInstance(ntGenerated.getNotificationTypePayload());
        newNTPayload.setPrimaryKeyName("property_id");
        newNTPayload.setPrimaryKeyValue(propertyId);

        Map<Integer, NotificationMessagePayload> payloadMap = new HashMap<Integer, NotificationMessagePayload>();
        
        List<PortfolioListing> portfolioListings = getPropertyListingsByPropertyId(
                unsubscribedUserList,
                propertyId);
        
        if (portfolioListings == null) {
            logger.debug("No portfolio listing found for property id : " + propertyId);
            return payloadMap;
        }
               
        for (PortfolioListing portfolioListing : portfolioListings) {
            Double percentageDifference = getPercentageDifference(
                    portfolioListing.getBasePrice(),
                    portfolioListing.getListingSize(),
                    (Double) notificationTypePayload.getNewValue());

            Map<String, Object> userDataMap = new HashMap<String, Object>();
            userDataMap.put(Tokens.ProjectName.name(), portfolioListing.getProjectName());
            userDataMap.put(Tokens.PropertyName.name(), portfolioListing.getName());
            userDataMap.put(Tokens.AbsolutePercentageDifference.name(), Math.abs(percentageDifference));
            userDataMap.put(Tokens.PercentageChangeString.name(), getPercentageChangeString(percentageDifference));

            NotificationMessagePayload nmPayload = new NotificationMessagePayload();
            nmPayload.setExtraAttributes(userDataMap);
            nmPayload.setNotificationTypePayload(newNTPayload);

            payloadMap.put(portfolioListing.getUserId(), nmPayload);
        }

        return payloadMap;
    }

    private Double getPercentageDifference(Double totalBasePrice, Double size, Double newPrice) {
        Double totalNewPrice = newPrice * size;
        Double priceDiff = totalNewPrice - totalBasePrice;

        Double percentageDiff = (priceDiff * 100) / totalBasePrice;
        return percentageDiff;
    }

    private String getPercentageChangeString(Double percentageDifference) {
        if (percentageDifference < 0) {
            return "decreased";
        }
        return "increased";
    }
}
