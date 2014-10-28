package com.proptiger.data.event.processor;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.core.util.DateUtil;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.payload.DefaultEventTypePayload;
import com.proptiger.data.event.service.EventTypeProcessorService;

@Component
public class PriceChangeProcessor extends DBEventProcessor {
    
    private static Logger             logger = LoggerFactory.getLogger(PriceChangeProcessor.class);

    @Autowired
    private EventTypeProcessorService eventTypeProcessorService;

    @Override
    public boolean populateEventSpecificData(EventGenerated event) {
        logger.info(" Populating the Event Type Old data.");

        /**
         * TODO for Now getting the transaction. Remove this query and getting
         * the fields need from raw Event by using field selector in the
         * database and get data from the payload.
         **/
        Map<String, Object> transactionRow = eventTypeProcessorService.getEventTransactionRow(event);
        if (transactionRow == null) {
            logger.error(" Transaction Row Not found " + event.getEventTypePayload().getTransactionId());
            return false;
        }

        Date effectiveDate = (Date) transactionRow.get("effective_date");
        Date firstDayOfMonth = DateUtil.getFirstDayOfCurrentMonth(event.getEventTypePayload()
                .getTransactionDateKeyValue());

        /**
         * PortfolioPriceChange, Only current month price changes are to be
         * accepted. Rest are to be discarded.
         */
        logger.debug(" FIRST DAY OF MONTH " + firstDayOfMonth + " EFFECTIVE DATE " + effectiveDate);
        /*
         * if(!effectiveDate.equals(firstDayOfMonth)){ return false; }
         */

        Double oldValue = eventTypeProcessorService.getPriceChangeOldValue(event, effectiveDate);
        if (oldValue == null) {
            logger.debug(" OLD Value not found. ");
            return false;
        }

        DefaultEventTypePayload defaultEventTypePayload = (DefaultEventTypePayload) event.getEventTypePayload();
        /**
         * checking the old value with new value. IF they both are equal then
         * discard the event. TODO later persist these events but mark them
         * discarded.
         */
        Number newValueNumber = (Number) defaultEventTypePayload.getNewValue();
        Double newValue = newValueNumber.doubleValue();

        logger.debug(" OLD PRICE " + oldValue + " NEW VALUE " + newValue);
        // TODO to move the equality to common place.
        // TODO to handle the null new value.
        if (newValue.equals(oldValue)) {
            return false;
        }
        defaultEventTypePayload.setOldValue(oldValue);

        return true;
    }

}
