package com.proptiger.data.event.processor.seo;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.proptiger.core.enums.DataVersion;
import com.proptiger.core.enums.ResidentialFlag;
import com.proptiger.core.enums.Status;
import com.proptiger.core.event.model.payload.DefaultEventTypePayload;
import com.proptiger.core.model.cms.Project;
import com.proptiger.core.model.cms.Property;
import com.proptiger.core.model.event.EventGenerated;
import com.proptiger.core.model.event.EventGenerated.EventStatus;
import com.proptiger.data.event.processor.DBEventProcessor;
import com.proptiger.data.service.ProjectService;
import com.proptiger.data.service.PropertyService;

@Service
public class SeoProjectChangeProcessor extends DBEventProcessor {
    private static Logger  logger = LoggerFactory.getLogger(SeoProjectChangeProcessor.class);

    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private PropertyService propertyService;

    @Override
    public EventGenerated populateEventSpecificData(EventGenerated event) {
        logger.info(" Populating the Project Change Event Type Old data.");

        DefaultEventTypePayload payload = (DefaultEventTypePayload) event.getEventTypePayload();
        Object newValue = payload.getNewValue();
        if (newValue.getClass().equals(String.class)) {
            if (!newValue.equals(DataVersion.Website.name()) && !newValue.equals(ResidentialFlag.Residential.name())
                    && !newValue.equals(Status.Active.name())) {
                return null;
            }
        }

        Project project = projectService.getActiveProjectByIdFromDB(Integer.parseInt(event.getEventTypeUniqueKey()));
        // The project is not active.
        if (project == null) {
            return null;
        }

        return event;
    }

    @Override
    protected EventStatus getVerificationEventStatus(List<EventGenerated> events) {
        EventGenerated event = events.get(0);
        List<Property> properties = propertyService.getActivePropertiesByProjectIdFromDB(Integer.parseInt(event.getEventTypeUniqueKey()));
        if(properties == null || properties.isEmpty()){
            return EventStatus.PendingVerification;
        }
        return EventStatus.Verified;
    }
}
