package com.proptiger.data.notification.processor;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.proptiger.PortfolioListing;
import com.proptiger.core.model.user.User;
import com.proptiger.data.notification.model.NotificationTypeGenerated;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;

@Service
public class ProjectNewsNotificationMessageProcessor extends NotificationMessageProcessor {

    private static Logger logger = LoggerFactory.getLogger(ProjectNewsNotificationMessageProcessor.class);

    @Override
    public Map<Integer, NotificationMessagePayload> getNotificationMessagePayloadByUnsubscribedUserList(
            List<User> unsubscribedUserList,
            NotificationTypeGenerated ntGenerated) {

        NotificationTypePayload notificationTypePayload = ntGenerated.getNotificationTypePayload();
        Integer projectId = Integer.parseInt((String) notificationTypePayload.getPrimaryKeyValue());

        logger.debug("Getting portfolioListings for project id: " + projectId);
        List<PortfolioListing> portfolioListings = getPortfolioListingsByProjectId(projectId);
        portfolioListings = removeUsersFromPortfolioListings(unsubscribedUserList, portfolioListings);

        return createNewsNMPayloadByPropertyListings(portfolioListings, notificationTypePayload);
    }
}
