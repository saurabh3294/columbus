package com.proptiger.data.notification.processor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.ForumUser;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;
import com.proptiger.data.service.marketplace.ListingService;

@Service
public class DefaultNotificationMessageProcessor implements NotificationMessageProcessor {
    
    @Autowired
    private ListingService listingService;
    
    @Override
    public List<ForumUser> getDefaultUserList(NotificationTypePayload payload) {
        // TODO Auto-generated method stub
        //listingService.getListing(userId, payload.getPrimaryKeyValue(), new FIQLSelector());
        
        return null;
    }

}
