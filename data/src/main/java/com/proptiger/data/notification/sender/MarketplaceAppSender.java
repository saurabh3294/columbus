package com.proptiger.data.notification.sender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.model.NotificationGenerated;

@Service
public class MarketplaceAppSender implements MediumSender {

    @Autowired
    private AndroidSender androidSender;

    @Override
    public boolean send(NotificationGenerated nGenerated) {
        return androidSender.sendToMarketplaceApp(nGenerated);
    }
}
