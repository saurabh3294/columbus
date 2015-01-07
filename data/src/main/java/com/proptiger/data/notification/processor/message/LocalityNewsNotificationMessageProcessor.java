package com.proptiger.data.notification.processor.message;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.proptiger.PortfolioListing;
import com.proptiger.core.model.user.User;
import com.proptiger.data.notification.enums.NotificationTypeUserStrategy;
import com.proptiger.data.notification.model.NotificationTypeGenerated;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;
import com.proptiger.data.service.ProjectService;

@Service
public class LocalityNewsNotificationMessageProcessor extends NotificationMessageProcessor {

    @Autowired
    private ProjectService projectService;

    @Override
    public Map<Integer, NotificationMessagePayload> getNotificationMessagePayload(
            NotificationTypeGenerated ntGenerated,
            List<User> userList,
            NotificationTypeUserStrategy strategy) {

        NotificationTypePayload notificationTypePayload = ntGenerated.getNotificationTypePayload();
        Integer localityId = Integer.parseInt((String) notificationTypePayload.getPrimaryKeyValue());
        List<PortfolioListing> portfolioListings = getPortfolioListingsByLocalityId(localityId, userList, strategy);
        return createNewsNMPayloadByPropertyListings(portfolioListings, notificationTypePayload);
    }

    @Override
    public List<Integer> getProjectIdsByPrimaryKey(Integer primaryKey) {
        // Assuming that the primary key is the locality id
        return projectService.getProjectIdsFromLocalityId(primaryKey);
    }
}
