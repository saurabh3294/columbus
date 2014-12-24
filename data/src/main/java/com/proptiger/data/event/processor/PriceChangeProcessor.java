package com.proptiger.data.event.processor;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.core.event.model.payload.DefaultEventTypePayload;
import com.proptiger.core.event.model.payload.EventTypePayload;
import com.proptiger.core.model.event.EventGenerated;
import com.proptiger.data.event.service.EventTypeProcessorService;

@Component
public class PriceChangeProcessor extends DBEventProcessor {

    private static Logger             logger = LoggerFactory.getLogger(PriceChangeProcessor.class);

    @Autowired
    private EventTypeProcessorService eventTypeProcessorService;

    /**
     * This is used to populate the event specific data for price change events.
     * The latest price for a property with effective date less than the current
     * month is treated as the Old price of a property for a price change event.
     * The same will be updated in the payload.
     */
    @Override
    public EventGenerated populateEventSpecificData(EventGenerated event) {
        EventTypePayload payload = event.getEventTypePayload();

        /**
         * TODO for Now getting the transaction. Remove this query and getting
         * the fields need from raw Event by using field selector in the
         * database and get data from the payload.
         **/
        Map<String, Object> transactionRow = eventTypeProcessorService.getEventTransactionRow(event);
        if (transactionRow == null) {
            logger.error("Transaction Row Not found for transactionID: " + payload.getTransactionId()
                    + " and eventType: "
                    + event.getEventType().getName()
                    + " primaryKey: "
                    + payload.getPrimaryKeyValue()
                    + ". Hence, removing the event.");
            return null;
        }

        // Getting the effective date for the current price change event
        Calendar effectiveDate = Calendar.getInstance();
        effectiveDate.setTime((Date) transactionRow.get("effective_date"));

        // Getting the date on which price was changed
        Calendar transactionDate = Calendar.getInstance();
        transactionDate.setTime(payload.getTransactionDateKeyValue());

        /**
         * PortfolioPriceChange, Only current month price changes are to be
         * accepted. Rest are to be discarded.
         */
        if (effectiveDate.get(Calendar.YEAR) != transactionDate.get(Calendar.YEAR) || effectiveDate.get(Calendar.MONTH) != transactionDate
                .get(Calendar.MONTH)) {
            logger.error("Changing the price for month other than the current month for transactionID: " + payload
                    .getTransactionId()
                    + " and eventType: "
                    + event.getEventType().getName()
                    + " primaryKey: "
                    + payload.getPrimaryKeyValue()
                    + ". Current Month: "
                    + transactionDate
                    + " and Price change Month: "
                    + effectiveDate
                    + ". Hence, removing the event.");
            return null;
        }

        // Getting the old price for the current event
        Double oldValue = eventTypeProcessorService.getPriceChangeOldValue(event, effectiveDate.getTime());
        if (oldValue == null) {
            logger.error("Old price not found for transactionID: " + payload.getTransactionId()
                    + " and eventType: "
                    + event.getEventType().getName()
                    + " primaryKey: "
                    + payload.getPrimaryKeyValue()
                    + ". Hence, removing the event.");
            return null;
        }

        DefaultEventTypePayload defaultEventTypePayload = (DefaultEventTypePayload) payload;
        /**
         * checking the old value with new value. IF they both are equal then
         * discard the event. TODO later persist these events but mark them
         * discarded.
         */
        Number newValueNumber = (Number) defaultEventTypePayload.getNewValue();
        Double newValue = newValueNumber.doubleValue();

        // TODO to move the equality to common place.
        // TODO to handle the null new value.
        if (newValue.equals(oldValue)) {
            logger.error("No price change found for transactionID: " + payload.getTransactionId()
                    + " and eventType: "
                    + event.getEventType().getName()
                    + " primaryKey: "
                    + payload.getPrimaryKeyValue()
                    + ". OldPrice: "
                    + oldValue
                    + " and NewPrice: "
                    + newValue
                    + ". Hence, removing the event.");
            return null;
        }
        defaultEventTypePayload.setOldValue(oldValue);
        return event;
    }

}
