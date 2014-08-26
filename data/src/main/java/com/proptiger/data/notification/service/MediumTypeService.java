package com.proptiger.data.notification.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.model.MediumTypeConfig;
import com.proptiger.data.notification.model.NotificationGenerated;

@Service
public class MediumTypeService {
    @Autowired
    private ApplicationContext  applicationContext;

    public void setNotificationMediumSender(List<NotificationGenerated> ntGeneratedList) {
        for(NotificationGenerated ntGenerated : ntGeneratedList) {
            populateMediumSenderConfig(ntGenerated);
        }
    }

    private void populateMediumSenderConfig(NotificationGenerated ntGenerated) {
        String mediumName = ntGenerated.getNotificationMedium().getName().name();
        MediumTypeConfig mediumTypeConfig = MediumTypeConfig.mediumTypeConfig.get(mediumName);
        
        if (mediumTypeConfig == null) {
            mediumTypeConfig = new MediumTypeConfig();
        }
        
        setMediumTypeConfigAttribute(mediumTypeConfig);
        ntGenerated.getNotificationMedium().setMediumTypeConfig(mediumTypeConfig);
    }

    private void setMediumTypeConfigAttribute(MediumTypeConfig mediumTypeConfig) {
        try {
            mediumTypeConfig.setMediumSenderObject(applicationContext.getBean(mediumTypeConfig.getSenderClassName()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
