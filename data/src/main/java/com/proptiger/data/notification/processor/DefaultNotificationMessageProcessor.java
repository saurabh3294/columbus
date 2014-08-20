package com.proptiger.data.notification.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.ForumUser;
import com.proptiger.data.model.Listing;
import com.proptiger.data.model.user.portfolio.PortfolioListing;
import com.proptiger.data.notification.enums.Tokens;
import com.proptiger.data.notification.model.NotificationTypeGenerated;
import com.proptiger.data.notification.model.payload.DefaultNotificationTypePayload;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;
import com.proptiger.data.service.marketplace.ListingService;
import com.proptiger.data.service.user.portfolio.PortfolioService;

@Service
public class DefaultNotificationMessageProcessor implements NotificationMessageProcessor {

    @Autowired
    private ListingService   listingService;

    @Autowired
    private PortfolioService portfolioService;

    @Override
    public Map<ForumUser, NotificationMessagePayload> getNotificationMessagePayloadBySubscribedUserList(
            List<ForumUser> userList,
            NotificationTypeGenerated ntGenerated) {
        // TODO Auto-generated method stub
        return new HashMap<ForumUser, NotificationMessagePayload>();
    }

    @Override
    public Map<ForumUser, NotificationMessagePayload> getNotificationMessagePayloadByUnsubscribedUserList(
            List<ForumUser> unsubscribedUserList,
            NotificationTypeGenerated ntGenerated) {

        Map<ForumUser, NotificationMessagePayload> payloadMap = new HashMap<ForumUser, NotificationMessagePayload>();

        Map<Integer, ForumUser> unsubscribedUserMap = new HashMap<Integer, ForumUser>();
        for (ForumUser user : unsubscribedUserList) {
            unsubscribedUserMap.put(user.getUserId(), user);
        }

        NotificationTypePayload notificationTypePayload = ntGenerated.getNotificationTypePayload();
        Integer listingId = (Integer) notificationTypePayload.getPrimaryKeyValue();
        Listing listing = listingService.getListingByListingId(listingId);

        Integer propertyId = listing.getPropertyId();
        notificationTypePayload.setPrimaryKeyName("property_id");
        notificationTypePayload.setPrimaryKeyValue(propertyId);

        List<PortfolioListing> portfolioListings = portfolioService.getActivePortfolioListingsByPropertyId(propertyId);
        for (PortfolioListing portfolioListing : portfolioListings) {
            if (unsubscribedUserMap.get(portfolioListing.getUserId()) != null) {
                continue;
            }

            DefaultNotificationTypePayload defaultNTPayload = (DefaultNotificationTypePayload) notificationTypePayload;
            Double percentageDifference = getPercentageDifference(
                    portfolioListing.getBasePrice(),
                    portfolioListing.getListingSize(),
                    (Double) defaultNTPayload.getNewValue());

            Map<String, Object> userDataMap = new HashMap<String, Object>();
            userDataMap.put(Tokens.ProjectName.name(), portfolioListing.getProjectName());
            userDataMap.put(Tokens.PropertyName.name(), portfolioListing.getName());

            // TODO: why are we using base price. why not old price
            userDataMap.put(Tokens.AbsolutePercentageDifference.name(), Math.abs(percentageDifference));
            userDataMap.put(Tokens.PercentageChangeString.name(), getPercentageChangeString(percentageDifference));

            NotificationMessagePayload nmPayload = new NotificationMessagePayload();
            nmPayload.setExtraAttributes(userDataMap);
            nmPayload.setNotificationTypePayload((DefaultNotificationTypePayload)notificationTypePayload);
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
            return " decreased";
        }
        return " increased";
    }
}
