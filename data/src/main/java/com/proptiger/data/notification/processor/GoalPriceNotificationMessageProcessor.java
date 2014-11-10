package com.proptiger.data.notification.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.cms.Listing;
import com.proptiger.core.model.proptiger.PortfolioListing;
import com.proptiger.core.model.user.User;
import com.proptiger.data.notification.model.NotificationTypeGenerated;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;
import com.proptiger.data.service.marketplace.ListingService;

@Service
public class GoalPriceNotificationMessageProcessor extends NotificationMessageProcessor {

    private static Logger  logger = LoggerFactory.getLogger(GoalPriceNotificationMessageProcessor.class);

    @Autowired
    private ListingService listingService;

    @Override
    public Map<Integer, NotificationMessagePayload> getNotificationMessagePayloadByUnsubscribedUserList(
            List<User> unsubscribedUserList,
            NotificationTypeGenerated ntGenerated) {

        NotificationTypePayload notificationTypePayload = ntGenerated.getNotificationTypePayload();
        Integer listingId = ((Number) notificationTypePayload.getPrimaryKeyValue()).intValue();

        logger.debug("Getting listing for listing id: " + listingId);
        Listing listing = listingService.getListingByListingId(listingId);

        Integer propertyId = listing.getPropertyId();

        NotificationTypePayload newNTPayload = NotificationTypePayload.newInstance(ntGenerated
                .getNotificationTypePayload());
        newNTPayload.setPrimaryKeyName("property_id");
        newNTPayload.setPrimaryKeyValue(propertyId);

        List<PortfolioListing> portfolioListings = getPropertyListingsByPropertyId(unsubscribedUserList, propertyId);
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

        return createDefaultNMPayloadByPropertyListings(newPortfolioListings, newNTPayload);
    }
}
