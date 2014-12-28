package com.proptiger.data.notification.processor.type;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.event.model.payload.DefaultEventTypePayload;
import com.proptiger.core.event.model.payload.EventTypePayload;
import com.proptiger.core.model.cms.Listing;
import com.proptiger.core.model.event.EventGenerated;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;
import com.proptiger.data.service.marketplace.ListingService;

@Service
public class PriceChangeNotificationTypeProcessor extends NotificationTypeProcessor {

    private static Logger  logger = LoggerFactory.getLogger(PriceChangeNotificationTypeProcessor.class);

    @Autowired
    private ListingService listingService;

    @Override
    public NotificationTypePayload getNotificationTypePayload(
            EventGenerated eventGenerated,
            NotificationType notificationType) {

        EventTypePayload eventTypePayload = eventGenerated.getEventTypePayload();
        return getNotificationTypePayload(eventTypePayload);
    }

    private NotificationTypePayload getNotificationTypePayload(EventTypePayload eventTypePayload) {

        NotificationTypePayload payload = new NotificationTypePayload();
        payload.populatePayloadValues((DefaultEventTypePayload) eventTypePayload);

        Integer listingId = Integer.parseInt((String) eventTypePayload.getPrimaryKeyValue());
        logger.debug("Getting listing for listing id: " + listingId);
        Listing listing = listingService.getListingByListingId(listingId);
        Integer propertyId = listing.getPropertyId();
        payload.setPrimaryKeyName("property_id");
        payload.setPrimaryKeyValue(propertyId.toString());

        if (eventTypePayload.getChildEventTypePayloads() != null) {
            List<NotificationTypePayload> childPayloads = new ArrayList<NotificationTypePayload>();
            for (EventTypePayload childEventTypePayload : eventTypePayload.getChildEventTypePayloads()) {
                childPayloads.add(getNotificationTypePayload(childEventTypePayload));
            }
            payload.setChildNotificationTypePayloads(childPayloads);
        }

        return payload;
    }

}
