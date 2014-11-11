package com.proptiger.data.event.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.cms.Project;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.service.ProjectService;

@Service
public class SeoProjectDeleteProcessor extends DBEventProcessor {
    private static Logger             logger = LoggerFactory.getLogger(SeoProjectDeleteProcessor.class);
    
    @Autowired
    private ProjectService projectService;

    @Override
    public boolean populateEventSpecificData(EventGenerated event) {
        logger.info(" Populating the Project Delete Event Type Old data.");
        
        Project project = projectService.getActiveProjectByIdFromDB(Integer.parseInt(event.getEventTypeUniqueKey()));
        // The project is not active.
        if(project == null){
            return true;
        }
        
        return false;
    }
}
