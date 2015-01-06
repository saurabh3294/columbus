package com.proptiger.data.notification.processor.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.proptiger.PortfolioListing;
import com.proptiger.core.model.user.User;
import com.proptiger.data.notification.enums.NotificationTypeUserStrategy;
import com.proptiger.data.notification.model.NotificationTypeGenerated;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;

@Service
public class GoalPriceNotificationMessageProcessor extends NotificationMessageProcessor {

    private static Logger logger = LoggerFactory.getLogger(GoalPriceNotificationMessageProcessor.class);

    @Override
    public Map<Integer, NotificationMessagePayload> getNotificationMessagePayload(
            NotificationTypeGenerated ntGenerated,
            List<User> userList,
            NotificationTypeUserStrategy strategy) {

        NotificationTypePayload notificationTypePayload = ntGenerated.getNotificationTypePayload();
        Integer propertyId = Integer.parseInt((String) notificationTypePayload.getPrimaryKeyValue());
        List<PortfolioListing> portfolioListings = getPortfolioListingsByPropertyId(propertyId, userList, strategy);

        List<PortfolioListing> newPortfolioListings = new ArrayList<PortfolioListing>();

        for (PortfolioListing portfolioListing : portfolioListings) {
            Double goalAmount = portfolioListing.getGoalAmount();
            if (goalAmount == null || goalAmount == 0 || (Double) notificationTypePayload.getNewValue() < goalAmount) {
                logger.debug("Skipping Goal Price Notification Creation as goal price is not met for user id : " + portfolioListing
                        .getUserId());
                continue;
            }
            newPortfolioListings.add(portfolioListing);
        }

        return createDefaultNMPayloadByPropertyListings(newPortfolioListings, notificationTypePayload);
    }
}