package com.proptiger.data.notification.sender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProptigerAppSender implements MediumSender {

    @Autowired
    private AndroidSender androidSender;

    @Override
    public boolean send(String template, Integer userId, String typeName) {
        return androidSender.sendToProptigerApp(template, userId, typeName);
    }

}
