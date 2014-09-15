package com.proptiger.data.notification.sender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.ForumUser;

@Service
public class MarketplaceAppSender implements MediumSender {

    @Autowired
    private AndroidSender androidSender;

    @Override
    public void send(String template, ForumUser forumUser, String typeName) {
        androidSender.sendToMarketplaceApp(template, forumUser, typeName);
    }
}
