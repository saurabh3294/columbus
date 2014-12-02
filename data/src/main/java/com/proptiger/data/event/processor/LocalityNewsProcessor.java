package com.proptiger.data.event.processor;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.payload.NewsEventTypePayload;
import com.proptiger.data.event.service.EventTypeProcessorService;
import com.proptiger.data.model.WordpressTerms;
import com.proptiger.data.service.BlogNewsService;
import com.proptiger.data.service.LocalityService;

@Component
public class LocalityNewsProcessor extends DBEventProcessor {

    private static Logger             logger      = LoggerFactory.getLogger(LocalityNewsProcessor.class);

    private static final String       LOCALITY_ID = "locality_id";

    @Autowired
    private EventTypeProcessorService eventTypeProcessorService;

    @Autowired
    private BlogNewsService           blogNewsService;

    @Autowired
    private LocalityService           localityService;

    /**
     * This is used to populate locality id and the news details for this event
     */
    @Override
    public EventGenerated populateEventSpecificData(EventGenerated event) {
        NewsEventTypePayload payload = (NewsEventTypePayload) event.getEventTypePayload();
        String transactionId = (String) payload.getTransactionId();
        Long postId = (Long) payload.getPostId();
        Long termTaxonomyId = (Long) payload.getTermTaxonomyId();

        // Getting relevant tags
        WordpressTerms term = blogNewsService.getTermDetailsByTermTaxonomyId(termTaxonomyId);
        List<WordpressTerms> categories = blogNewsService.getCategoriesByPostId(postId);
        List<String> cities = new ArrayList<String>();
        for (WordpressTerms category : categories) {
            cities.add(category.getName());
        }

        // Getting locality id
        Integer localityId = localityService.getLocalityIdByTagName(term.getName(), cities);
        if (localityId == null) {
            logger.error("Discarding event of eventType: " + event.getEventType().getName()
                    + " with transactionId: "
                    + transactionId
                    + " as localityId is null for termTaxonomyId: "
                    + termTaxonomyId
                    + " and newsPostId: "
                    + postId);
            return null;
        }

        // Populating payload with locality id
        payload.setPrimaryKeyName(LOCALITY_ID);
        payload.setPrimaryKeyValue(localityId.toString());

        return event;
    }

}
