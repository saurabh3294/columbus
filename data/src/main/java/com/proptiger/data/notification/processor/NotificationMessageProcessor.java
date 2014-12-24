package com.proptiger.data.notification.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.cms.Property;
import com.proptiger.core.model.proptiger.PortfolioListing;
import com.proptiger.core.model.user.User;
import com.proptiger.data.model.WordpressPost;
import com.proptiger.data.notification.enums.Tokens;
import com.proptiger.data.notification.model.NotificationTypeGenerated;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;
import com.proptiger.data.service.BlogNewsService;
import com.proptiger.data.service.PropertyService;
import com.proptiger.data.service.user.portfolio.PortfolioService;

@Service
public abstract class NotificationMessageProcessor {

    private static Logger    logger = LoggerFactory.getLogger(NotificationMessageProcessor.class);

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private PropertyService  propertyService;

    @Autowired
    private BlogNewsService  blogNewsService;

    public Map<Integer, NotificationMessagePayload> getNotificationMessagePayloadBySubscribedUserList(
            List<User> userList,
            NotificationTypeGenerated ntGenerated) {
        return new HashMap<Integer, NotificationMessagePayload>();
    }

    public Map<Integer, NotificationMessagePayload> getNotificationMessagePayloadByUnsubscribedUserList(
            List<User> unsubscribedUserList,
            NotificationTypeGenerated ntGenerated) {

        NotificationTypePayload notificationTypePayload = ntGenerated.getNotificationTypePayload();
        Integer propertyId = Integer.parseInt((String) notificationTypePayload.getPrimaryKeyValue());
        List<PortfolioListing> portfolioListings = getPortfolioListingsByPropertyId(propertyId);
        portfolioListings = removeUsersFromPortfolioListings(unsubscribedUserList, portfolioListings);
        return createDefaultNMPayloadByPropertyListings(portfolioListings, ntGenerated.getNotificationTypePayload());
    }

    protected List<PortfolioListing> getPortfolioListingsByPropertyId(Integer propertyId) {
        logger.debug("Getting portfolioListings for property id: " + propertyId);
        return portfolioService.getActivePortfolioListingsByPropertyId(propertyId);
    }

    protected List<PortfolioListing> getPortfolioListingsByProjectId(Integer projectId) {
        Set<PortfolioListing> portfolioListings = new HashSet<PortfolioListing>();
        logger.debug("Getting properties for project id: " + projectId);
        List<Property> propertyList = propertyService.getPropertyIdsByProjectId(projectId);

        for (Property property : propertyList) {
            portfolioListings.addAll(getPortfolioListingsByPropertyId(property.getPropertyId()));
        }

        return new ArrayList<PortfolioListing>(portfolioListings);
    }

    protected List<PortfolioListing> getPortfolioListingsByLocalityId(Integer localityId) {
        Set<PortfolioListing> portfolioListings = new HashSet<PortfolioListing>();
        logger.debug("Getting properties for locality id: " + localityId);
        List<Property> propertyList = propertyService.getPropertyIdsByLocalityId(localityId);

        for (Property property : propertyList) {
            portfolioListings.addAll(getPortfolioListingsByPropertyId(property.getPropertyId()));
        }

        return new ArrayList<PortfolioListing>(portfolioListings);
    }

    protected Map<Integer, NotificationMessagePayload> createDefaultNMPayloadByPropertyListings(
            List<PortfolioListing> portfolioListings,
            NotificationTypePayload ntPayload) {

        Map<Integer, NotificationMessagePayload> payloadMap = new HashMap<Integer, NotificationMessagePayload>();

        if (portfolioListings == null) {
            logger.error("No portfolio listing found.");
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

    protected Map<Integer, NotificationMessagePayload> createNewsNMPayloadByPropertyListings(
            List<PortfolioListing> portfolioListings,
            NotificationTypePayload ntPayload) {

        Map<Integer, NotificationMessagePayload> payloadMap = new HashMap<Integer, NotificationMessagePayload>();

        Long postId = Long.parseLong((String) ntPayload.getExtraAttributes().get("post_id"));
        WordpressPost post = blogNewsService.getNewsDetailsByPostId(postId);
        if (post == null) {
            logger.error("No post found for postId: " + postId);
            return payloadMap;
        }

        for (PortfolioListing portfolioListing : portfolioListings) {
            Map<String, Object> userDataMap = new HashMap<String, Object>();
            userDataMap.put(Tokens.PortfolioNews.NewsTitle.name(), post.getPostTitle());
            userDataMap.put(Tokens.PortfolioNews.NewsBody.name(), post.getPostContent());

            NotificationMessagePayload nmPayload = new NotificationMessagePayload();
            nmPayload.setExtraAttributes(userDataMap);
            nmPayload.setNotificationTypePayload(ntPayload);

            payloadMap.put(portfolioListing.getUserId(), nmPayload);
        }
        return payloadMap;
    }

    protected List<PortfolioListing> removeUsersFromPortfolioListings(
            List<User> users,
            List<PortfolioListing> portfolioListings) {
        Map<Integer, User> userMap = new HashMap<Integer, User>();
        for (User user : users) {
            userMap.put(user.getId(), user);
        }

        List<PortfolioListing> newPortfolioListings = new ArrayList<PortfolioListing>();

        if (portfolioListings == null) {
            return newPortfolioListings;
        }

        for (PortfolioListing portfolioListing : portfolioListings) {
            if (userMap.get(portfolioListing.getUserId()) != null) {
                logger.debug("Ignoring unsubscribed user: " + portfolioListing.getUserId());
                continue;
            }
            newPortfolioListings.add(portfolioListing);
        }
        return newPortfolioListings;
    }

    public PortfolioService getPortfolioService() {
        return portfolioService;
    }

    public void setPortfolioService(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

}
