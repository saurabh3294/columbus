package com.proptiger.data.notification.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.cms.Listing;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.payload.EventTypePayload;
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

        NotificationTypePayload payload = notificationType.getNotificationTypeConfig()
                .getNotificationTypePayloadObject();

        EventTypePayload eventTypePayload = eventGenerated.getEventTypePayload();
        Integer listingId = ((Number) eventTypePayload.getPrimaryKeyValue()).intValue();

        logger.debug("Getting listing for listing id: " + listingId);
        Listing listing = listingService.getListingByListingId(listingId);

        Integer propertyId = listing.getPropertyId();
        payload.setPrimaryKeyName("property_id");
        payload.setPrimaryKeyValue(propertyId);
        payload.populatePayloadValues(eventTypePayload);
        return payload;
    }
}
