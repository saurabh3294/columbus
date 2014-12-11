package com.proptiger.data.event.processor.seo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.enums.DataVersion;
import com.proptiger.core.enums.ResidentialFlag;
import com.proptiger.core.enums.Status;
import com.proptiger.core.model.cms.Project;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.payload.DefaultEventTypePayload;
import com.proptiger.data.event.processor.DBEventProcessor;
import com.proptiger.data.service.ProjectService;

@Service
public class SeoProjectDeleteProcessor extends DBEventProcessor {
    private static Logger             logger = LoggerFactory.getLogger(SeoProjectDeleteProcessor.class);
    
    @Autowired
    private ProjectService projectService;

    @Override
    public EventGenerated populateEventSpecificData(EventGenerated event) {
        logger.info(" Populating the Project Delete Event Type Old data.");
        
        DefaultEventTypePayload payload = (DefaultEventTypePayload)event.getEventTypePayload();
        Object newValue = payload.getNewValue();
        if( newValue.getClass().equals(String.class) ){
            if( newValue.equals(DataVersion.Website.name()) || newValue.equals(ResidentialFlag.Residential.name()) || !newValue.equals(Status.Active.name()) ){
                return null;
            }
        }
        
        Project project = projectService.getActiveProjectByIdFromDB(Integer.parseInt(event.getEventTypeUniqueKey()));
        // The project is not active.
        if(project == null){
            return event;
        }
        
        return null;
    }
}
