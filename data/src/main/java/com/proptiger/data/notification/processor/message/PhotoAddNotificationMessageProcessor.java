package com.proptiger.data.notification.processor.message;

import java.util.ArrayList;
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
import com.proptiger.data.service.PropertyService;

@Service
public class PhotoAddNotificationMessageProcessor extends NotificationMessageProcessor {

    @Autowired
    private PropertyService propertyService;

    @Override
    public Map<Integer, NotificationMessagePayload> getNotificationMessagePayload(
            NotificationTypeGenerated ntGenerated,
            List<User> userList,
            NotificationTypeUserStrategy strategy) {

        NotificationTypePayload notificationTypePayload = ntGenerated.getNotificationTypePayload();
        Integer projectId = Integer.parseInt((String) notificationTypePayload.getPrimaryKeyValue());
        List<PortfolioListing> portfolioListings = getPortfolioListingsByProjectId(projectId, userList, strategy);
        return createDefaultNMPayloadByPropertyListings(portfolioListings, notificationTypePayload);
    }

    @Override
    public List<Integer> getProjectIdsByPrimaryKey(Integer primaryKey) {
        // Assuming that the primary key is the project id
        List<Integer> projectIds = new ArrayList<Integer>();
        projectIds.add(primaryKey);
        return projectIds;
    }
}
