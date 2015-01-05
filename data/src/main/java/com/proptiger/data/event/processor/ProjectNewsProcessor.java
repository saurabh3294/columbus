package com.proptiger.data.event.processor;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.core.event.model.payload.NewsEventTypePayload;
import com.proptiger.core.model.event.EventGenerated;
import com.proptiger.data.event.service.EventTypeProcessorService;
import com.proptiger.data.model.WordpressTerms;
import com.proptiger.data.service.BlogNewsService;
import com.proptiger.data.service.ProjectService;

@Component
public class ProjectNewsProcessor extends DBEventProcessor {

    private static Logger             logger     = LoggerFactory.getLogger(ProjectNewsProcessor.class);

    private static final String       PROJECT_ID = "project_id";

    @Autowired
    private EventTypeProcessorService eventTypeProcessorService;

    @Autowired
    private BlogNewsService           blogNewsService;

    @Autowired
    private ProjectService            projectService;

    /**
     * This is used to populate project id and the news details for this event
     */
    @Override
    public EventGenerated populateEventSpecificData(EventGenerated event) {
        NewsEventTypePayload payload = (NewsEventTypePayload) event.getEventTypePayload();
        String transactionId = (String) payload.getTransactionId();
        Long postId = payload.getPostId();
        Long termTaxonomyId = payload.getTermTaxonomyId();

        // Getting relevant tags
        WordpressTerms term = blogNewsService.getTermDetailsByTermTaxonomyId(termTaxonomyId);
        List<WordpressTerms> categories = blogNewsService.getCategoriesByPostId(postId);
        List<String> cities = new ArrayList<String>();
        for (WordpressTerms category : categories) {
            cities.add(category.getName());
        }

        // Getting project id
        Integer projectId = projectService.getProjectIdByTagName(term.getName(), cities);
        if (projectId == null) {
            logger.error("Discarding event of eventType: " + event.getEventType().getName()
                    + " with transactionId: "
                    + transactionId
                    + " as projectId is null for termTaxonomyId: "
                    + termTaxonomyId
                    + " and newsPostId: "
                    + postId);
            return null;
        }

        // Populating payload with project id
        payload.setPrimaryKeyName(PROJECT_ID);
        payload.setPrimaryKeyValue(projectId.toString());

        return event;
    }

}
