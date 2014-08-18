package com.proptiger.data.notification.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proptiger.data.notification.model.MediumTypeConfig;
import com.proptiger.data.notification.model.NotificationGenerated;

@Service
public class MediumTypeService {

    public void setNotificationMediumSender(List<NotificationGenerated> ntGeneratedList) {
        for(NotificationGenerated ntGenerated : ntGeneratedList) {
            populateMediumSenderConfig(ntGenerated);
        }
    }

    private void populateMediumSenderConfig(NotificationGenerated ntGenerated) {
        String mediumName = ntGenerated.getNotificationMedium().getName();
        MediumTypeConfig mediumTypeConfig = MediumTypeConfig.mediumTypeConfig.get(mediumName);
        
        if (mediumTypeConfig == null) {
            mediumTypeConfig = new MediumTypeConfig();
        }
        
        setMediumTypeConfigAttribute(mediumTypeConfig);
        ntGenerated.getNotificationMedium().setMediumTypeConfig(mediumTypeConfig);
    }

    private void setMediumTypeConfigAttribute(MediumTypeConfig mediumTypeConfig) {
        try {
            mediumTypeConfig.setMediumSenderObject(mediumTypeConfig.getSenderClassName().newInstance());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
