package com.proptiger.data.notification.model;

import java.util.HashMap;
import java.util.Map;

import com.proptiger.data.event.constants.MediumType;
import com.proptiger.data.model.BaseModel;
import com.proptiger.data.notification.sender.EmailSender;
import com.proptiger.data.notification.sender.MediumSender;

public class MediumTypeConfig extends BaseModel {
    private static final long serialVersionUID = 5217123915811730145L;
    
    public static Map<String, MediumTypeConfig> mediumTypeConfig;
    static {
        mediumTypeConfig = new HashMap<String, MediumTypeConfig>();
        mediumTypeConfig.put(MediumType.EMAIL.name(), new MediumTypeConfig(EmailSender.class));
    }

    private Class<EmailSender>   senderClassName = EmailSender.class;
    private MediumSender        mediumSenderObject;
    
    public MediumTypeConfig(Class<EmailSender> senderClassName) {
        if (senderClassName != null) {
            this.senderClassName = senderClassName;
        }
    }

    public MediumTypeConfig () {
    }
    
    public Class<EmailSender> getSenderClassName() {
        return senderClassName;
    }

    public void setSenderClassName(Class<EmailSender> senderClassName) {
        this.senderClassName = senderClassName;
    }

    public MediumSender getMediumSenderObject() {
        return mediumSenderObject;
    }

    public void setMediumSenderObject(MediumSender mediumSenderObject) {
        this.mediumSenderObject = mediumSenderObject;
    }
}