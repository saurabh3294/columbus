package com.proptiger.data.notification.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.user.User;
import com.proptiger.data.model.user.portfolio.PortfolioListing;
import com.proptiger.data.notification.enums.Tokens;
import com.proptiger.data.notification.model.NotificationTypeGenerated;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;
import com.proptiger.data.service.user.portfolio.PortfolioService;

@Service
public abstract class NotificationMessageProcessor {

    private static Logger    logger = LoggerFactory.getLogger(NotificationMessageProcessor.class);

    @Autowired
    private PortfolioService portfolioService;

    public Map<Integer, NotificationMessagePayload> getNotificationMessagePayloadBySubscribedUserList(
            List<User> userList,
            NotificationTypeGenerated ntGenerated) {
        return new HashMap<Integer, NotificationMessagePayload>();
    }

    public Map<Integer, NotificationMessagePayload> getNotificationMessagePayloadByUnsubscribedUserList(
            List<User> unsubscribedUserList,
            NotificationTypeGenerated ntGenerated) {

        NotificationTypePayload notificationTypePayload = ntGenerated.getNotificationTypePayload();
        Integer propertyId = ((Number) notificationTypePayload.getPrimaryKeyValue()).intValue();
        List<PortfolioListing> portfolioListings = getPropertyListingsByPropertyId(unsubscribedUserList, propertyId);
        return createDefaultNMPayloadByPropertyListings(portfolioListings, ntGenerated.getNotificationTypePayload());
    }

    protected List<PortfolioListing> getPropertyListingsByPropertyId(List<User> unsubscribedUserList, Integer propertyId) {

        Map<Integer, User> unsubscribedUserMap = new HashMap<Integer, User>();
        for (User user : unsubscribedUserList) {
            unsubscribedUserMap.put(user.getId(), user);
        }

        logger.debug("Getting portfolioListings for property id: " + propertyId);
        List<PortfolioListing> portfolioListings = portfolioService.getActivePortfolioListingsByPropertyId(propertyId);
        List<PortfolioListing> newPortfolioListings = new ArrayList<PortfolioListing>();

        for (PortfolioListing portfolioListing : portfolioListings) {
            if (unsubscribedUserMap.get(portfolioListing.getUserId()) != null) {
                logger.debug("Ignoring unsubscribed user : " + portfolioListing.getUserId());
                continue;
            }
            newPortfolioListings.add(portfolioListing);
        }
        return newPortfolioListings;
    }

    protected Map<Integer, NotificationMessagePayload> createDefaultNMPayloadByPropertyListings(
            List<PortfolioListing> portfolioListings,
            NotificationTypePayload ntPayload) {

        Map<Integer, NotificationMessagePayload> payloadMap = new HashMap<Integer, NotificationMessagePayload>();

        if (portfolioListings == null) {
            logger.debug("No portfolio listing found.");
            return payloadMap;
        }

        for (PortfolioListing portfolioListing : portfolioListings) {
            Map<String, Object> userDataMap = new HashMap<String, Object>();
            userDataMap.put(Tokens.PortfolioProjectUpdates.ProjectName.name(), portfolioListing.getProjectName());
            userDataMap.put(Tokens.PortfolioProjectUpdates.PropertyName.name(), portfolioListing.getName());

            NotificationMessagePayload nmPayload = new NotificationMessagePayload();
            nmPayload.setExtraAttributes(userDataMap);
            nmPayload.setNotificationTypePayload(ntPayload);

            payloadMap.put(portfolioListing.getUserId(), nmPayload);
        }
        return payloadMap;
    }

    public PortfolioService getPortfolioService() {
        return portfolioService;
    }

    public void setPortfolioService(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

}
