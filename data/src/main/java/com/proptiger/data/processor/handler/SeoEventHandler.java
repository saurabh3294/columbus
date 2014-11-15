package com.proptiger.data.processor.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.enums.DomainObject;
import com.proptiger.data.event.enums.EventTypeName;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.service.EventGeneratedService;
import com.proptiger.data.model.seo.URLCategories;
import com.proptiger.data.notification.model.Subscriber.SubscriberName;
import com.proptiger.data.service.seo.URLCategoriesService;

@Service
public class SeoEventHandler {
    private static Logger         logger = LoggerFactory.getLogger(SeoEventHandler.class);

    @Autowired
    private EventGeneratedService eventGeneratedService;
    
    @Autowired
    private URLCategoriesService urlCategoriesService;

    public int generateUrls(int numberOfEvents) {
        List<EventGenerated> events = eventGeneratedService.getLatestGeneratedEventsBySubscriber(
                SubscriberName.Seo,
                getUrlGeneratorEventTypeList(),
                numberOfEvents);
        logger.info("Fetched "+events.size()+" events for url generation.");
        Map<DomainObject, List<EventGenerated>> groupEventsMap = groupEventsByEventType(events);
        Map<DomainObject, List<URLCategories>> groupCategoryMap = urlCategoriesService.getAllUrlCategoryByDomainObject(); 
        
        for(Map.Entry<DomainObject, List<EventGenerated>> entry:groupEventsMap.entrySet()){
            
        }
        
        return 1;
    }

    protected Map<DomainObject, List<EventGenerated>> groupEventsByEventType(List<EventGenerated> eventsGenerated) {
        Map<DomainObject, List<EventGenerated>> mapEvents = new HashMap<DomainObject, List<EventGenerated>>();
        List<EventGenerated> groupEvents;// = new ArrayList<EventGenerated>();
        
        DomainObject domainObject;
        for (EventGenerated eventGenerated : eventsGenerated) {
            domainObject = DomainObject.getDomainInstance(Long.parseLong(eventGenerated.getEventTypeUniqueKey()));
            groupEvents = mapEvents.get(domainObject);
            if (groupEvents == null) {
                groupEvents = new ArrayList<EventGenerated>();
            }
            groupEvents.add(eventGenerated);
            mapEvents.put(domainObject, groupEvents);
        }

        return mapEvents;
    }
    
    private List<String> getUrlGeneratorEventTypeList() {
        List<String> eventTypeNames = new ArrayList<String>();
        eventTypeNames.add(EventTypeName.BuilderGenerateUrl.getEventTypeName());
        eventTypeNames.add(EventTypeName.ProjectGenerateUrl.getEventTypeName());
        eventTypeNames.add(EventTypeName.CityGenerateUrl.getEventTypeName());
        eventTypeNames.add(EventTypeName.PropertyGenerateUrl.getEventTypeName());
        eventTypeNames.add(EventTypeName.SuburbGenerateUrl.getEventTypeName());
        eventTypeNames.add(EventTypeName.LocalityGenerateUrl.getEventTypeName());

        return eventTypeNames;
    }
}
