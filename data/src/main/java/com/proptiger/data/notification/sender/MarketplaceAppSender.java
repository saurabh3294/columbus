package com.proptiger.data.notification.sender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.payload.NotificationSenderPayload;

@Service
public class MarketplaceAppSender implements MediumSender {

    @Autowired
    private AndroidSender androidSender;

    @Override
    public boolean send(String template, Integer userId, NotificationGenerated nGenerated, NotificationSenderPayload payload) {
        return androidSender.sendToMarketplaceApp(template, userId, nGenerated);
    }
}
