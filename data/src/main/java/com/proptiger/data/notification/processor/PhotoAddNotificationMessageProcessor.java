package com.proptiger.data.notification.processor;

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
import com.proptiger.data.notification.model.NotificationTypeGenerated;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;
import com.proptiger.data.service.PropertyService;

@Service
public class PhotoAddNotificationMessageProcessor extends NotificationMessageProcessor {

    private static Logger   logger = LoggerFactory.getLogger(PhotoAddNotificationMessageProcessor.class);

    @Autowired
    private PropertyService propertyService;

    @Override
    public Map<Integer, NotificationMessagePayload> getNotificationMessagePayloadByUnsubscribedUserList(
            List<User> unsubscribedUserList,
            NotificationTypeGenerated ntGenerated) {

        NotificationTypePayload notificationTypePayload = ntGenerated.getNotificationTypePayload();

        Integer projectId = Integer.parseInt((String) notificationTypePayload.getPrimaryKeyValue());

        logger.debug("Getting properties for project id: " + projectId);
        List<Property> propertyList = propertyService.getPropertiesForProject(projectId);

        Map<Integer, NotificationMessagePayload> payloadMap = new HashMap<Integer, NotificationMessagePayload>();

        for (Property property : propertyList) {
            Integer propertyId = property.getPropertyId();

            NotificationTypePayload newNTPayload = NotificationTypePayload.newInstance(ntGenerated
                    .getNotificationTypePayload());
            newNTPayload.setPrimaryKeyName("property_id");
            newNTPayload.setPrimaryKeyValue(propertyId);

            List<PortfolioListing> portfolioListings = getPropertyListingsByPropertyId(unsubscribedUserList, propertyId);

            // TODO: handle cases where user has multiple properties in a
            // particular project.
            payloadMap.putAll(createDefaultNMPayloadByPropertyListings(portfolioListings, newNTPayload));
        }

        return payloadMap;
    }
}
