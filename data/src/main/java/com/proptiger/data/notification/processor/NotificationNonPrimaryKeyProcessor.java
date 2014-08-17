package com.proptiger.data.notification.processor;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.processor.dto.NotificationByKeyDto;

@Service
public class NotificationNonPrimaryKeyProcessor implements NotificationProcessor{

    @Override
    public void processIntraMerging(NotificationByKeyDto notificationByKey) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void processIntraSuppressing(NotificationByKeyDto notificationByKey) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<NotificationMessage> processInterMerging(
            List<NotificationMessage> notificationMessages,
            Map<NotificationType, List<NotificationGenerated>> generatedNotifications) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void processInterSuppressing(NotificationByKeyDto parent, NotificationByKeyDto child) {
        // TODO Auto-generated method stub
        
    }

    
}
