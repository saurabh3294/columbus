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
public class LocalityNewsNotificationMessageProcessor extends NotificationMessageProcessor {

    private static Logger logger = LoggerFactory.getLogger(LocalityNewsNotificationMessageProcessor.class);

    @Override
    public Map<Integer, NotificationMessagePayload> getNotificationMessagePayloadByUnsubscribedUserList(
            List<User> unsubscribedUserList,
            NotificationTypeGenerated ntGenerated) {

        NotificationTypePayload notificationTypePayload = ntGenerated.getNotificationTypePayload();
        Integer localityId = Integer.parseInt((String) notificationTypePayload.getPrimaryKeyValue());

        logger.debug("Getting portfolioListings for locality id: " + localityId);
        List<PortfolioListing> portfolioListings = getPortfolioListingsByLocalityId(localityId);
        portfolioListings = removeUsersFromPortfolioListings(unsubscribedUserList, portfolioListings);

        return createNewsNMPayloadByPropertyListings(portfolioListings, notificationTypePayload);
    }
}
