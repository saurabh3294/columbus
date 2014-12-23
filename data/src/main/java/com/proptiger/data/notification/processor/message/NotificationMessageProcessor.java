package com.proptiger.data.notification.processor.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.cms.Property;
import com.proptiger.core.model.proptiger.PortfolioListing;
import com.proptiger.core.model.user.User;
import com.proptiger.data.model.WordpressPost;
import com.proptiger.data.notification.enums.NotificationTypeUserStrategy;
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

    /**
     * Returns the NotificationMessagePayload for the given users and strategy
     * 
     * @param userList
     * @param ntGenerated
     * @return
     */
    public Map<Integer, NotificationMessagePayload> getNotificationMessagePayload(
            NotificationTypeGenerated ntGenerated,
            List<User> userList,
            NotificationTypeUserStrategy strategy) {

        NotificationTypePayload notificationTypePayload = ntGenerated.getNotificationTypePayload();
        // Assuming that the primary key is the property id
        Integer propertyId = Integer.parseInt((String) notificationTypePayload.getPrimaryKeyValue());
        List<PortfolioListing> portfolioListings = getPortfolioListingsByPropertyId(propertyId, userList, strategy);
        return createDefaultNMPayloadByPropertyListings(portfolioListings, ntGenerated.getNotificationTypePayload());
    }

    /**
     * Returns the list of projectIds corresponding to the given primary key
     * 
     * @param primaryKey
     * @return
     */
    public List<Integer> getProjectIdsByPrimaryKey(Integer primaryKey) {
        // Assuming that the primary key is the property id
        List<Integer> projectIds = new ArrayList<Integer>();
        projectIds.add(propertyService.getProjectIdFromPropertyId(primaryKey));
        return projectIds;
    }

    protected List<PortfolioListing> getPortfolioListingsByProperties(
            List<Property> properties,
            List<User> userList,
            NotificationTypeUserStrategy strategy) {

        boolean includeUsers = Boolean.TRUE;
        if (NotificationTypeUserStrategy.MinusUnsubscribed.equals(strategy)) {
            includeUsers = Boolean.FALSE;
        }

        return portfolioService.getActivePortfolioListingsByPropertiesAndUsers(properties, userList, includeUsers);
    }

    protected List<PortfolioListing> getPortfolioListingsByPropertyId(
            Integer propertyId,
            List<User> userList,
            NotificationTypeUserStrategy strategy) {
        logger.debug("Getting portfolioListings for propertyId: " + propertyId);
        Property property = propertyService.getProperty(propertyId);
        List<Property> properties = new ArrayList<Property>();
        if (property != null) {
            properties.add(property);
        }
        return getPortfolioListingsByProperties(properties, userList, strategy);
    }

    protected List<PortfolioListing> getPortfolioListingsByProjectId(
            Integer projectId,
            List<User> userList,
            NotificationTypeUserStrategy strategy) {
        logger.debug("Getting properties for project id: " + projectId);
        List<Property> properties = propertyService.getPropertiesByProjectId(projectId);
        return getPortfolioListingsByProperties(properties, userList, strategy);
    }

    protected List<PortfolioListing> getPortfolioListingsByLocalityId(
            Integer localityId,
            List<User> userList,
            NotificationTypeUserStrategy strategy) {
        logger.debug("Getting properties for locality id: " + localityId);
        List<Property> properties = propertyService.getPropertiesByLocalityId(localityId);
        return getPortfolioListingsByProperties(properties, userList, strategy);
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

    public PortfolioService getPortfolioService() {
        return portfolioService;
    }

    public void setPortfolioService(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    public PropertyService getPropertyService() {
        return propertyService;
    }

    public void setPropertyService(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

}
