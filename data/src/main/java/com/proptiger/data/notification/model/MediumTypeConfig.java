package com.proptiger.data.notification.model;

import java.util.HashMap;
import java.util.Map;

import com.proptiger.core.model.BaseModel;
import com.proptiger.data.notification.enums.MediumType;
import com.proptiger.data.notification.model.payload.EmailSenderPayload;
import com.proptiger.data.notification.model.payload.NotificationSenderPayload;
import com.proptiger.data.notification.sender.AndroidSender;
import com.proptiger.data.notification.sender.EmailSender;
import com.proptiger.data.notification.sender.MarketplaceAppSender;
import com.proptiger.data.notification.sender.MediumSender;
import com.proptiger.data.notification.sender.ProptigerAppSender;
import com.proptiger.data.notification.sender.SmsSender;

public class MediumTypeConfig extends BaseModel {

    private static final long                                    serialVersionUID = 5217123915811730145L;

    public static Map<MediumType, MediumTypeConfig>              mediumTypeConfigMap;
    static {
        mediumTypeConfigMap = new HashMap<MediumType, MediumTypeConfig>();
        mediumTypeConfigMap.put(MediumType.Email, new MediumTypeConfig(EmailSender.class, EmailSenderPayload.class));
        mediumTypeConfigMap.put(MediumType.Android, new MediumTypeConfig(AndroidSender.class, null));
        mediumTypeConfigMap.put(MediumType.Sms, new MediumTypeConfig(SmsSender.class, null));
        mediumTypeConfigMap.put(MediumType.ProptigerApp, new MediumTypeConfig(ProptigerAppSender.class, null));
        mediumTypeConfigMap.put(MediumType.MarketplaceApp, new MediumTypeConfig(MarketplaceAppSender.class, null));
    }

    private transient Class<? extends MediumSender>              senderClassName  = EmailSender.class;
    private transient Class<? extends NotificationSenderPayload> payloadClassName = EmailSenderPayload.class;
    private transient MediumSender                               mediumSenderObject;
    private transient NotificationSenderPayload                  notificationSenderPayloadObject;

    public MediumTypeConfig(
            Class<? extends MediumSender> senderClassName,
            Class<? extends NotificationSenderPayload> payloadClassName) {
        if (senderClassName != null) {
            this.senderClassName = senderClassName;
        }
        if (payloadClassName != null) {
            this.payloadClassName = payloadClassName;
        }
    }

    public MediumTypeConfig() {
    }

    public Class<? extends MediumSender> getSenderClassName() {
        return senderClassName;
    }

    public void setSenderClassName(Class<? extends MediumSender> senderClassName) {
        this.senderClassName = senderClassName;
    }

    public MediumSender getMediumSenderObject() {
        return mediumSenderObject;
    }

    public void setMediumSenderObject(MediumSender mediumSenderObject) {
        this.mediumSenderObject = mediumSenderObject;
    }

    public Class<? extends NotificationSenderPayload> getPayloadClassName() {
        return payloadClassName;
    }

    public void setPayloadClassName(Class<? extends NotificationSenderPayload> payloadClassName) {
        this.payloadClassName = payloadClassName;
    }

    public NotificationSenderPayload getNotificationSenderPayloadObject() {
        return notificationSenderPayloadObject;
    }

    public void setNotificationSenderPayloadObject(NotificationSenderPayload notificationSenderPayloadObject) {
        this.notificationSenderPayloadObject = notificationSenderPayloadObject;
    }
}