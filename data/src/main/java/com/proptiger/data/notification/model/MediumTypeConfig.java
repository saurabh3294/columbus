package com.proptiger.data.notification.model;

import java.util.HashMap;
import java.util.Map;

import com.proptiger.core.model.BaseModel;
import com.proptiger.data.internal.dto.mail.DefaultMediumDetails;
import com.proptiger.data.internal.dto.mail.MailDetails;
import com.proptiger.data.internal.dto.mail.MediumDetails;
import com.proptiger.data.notification.enums.MediumType;
import com.proptiger.data.notification.sender.AndroidSender;
import com.proptiger.data.notification.sender.EmailSender;
import com.proptiger.data.notification.sender.MarketplaceAppSender;
import com.proptiger.data.notification.sender.MediumSender;
import com.proptiger.data.notification.sender.ProptigerAppSender;
import com.proptiger.data.notification.sender.SmsSender;

public class MediumTypeConfig extends BaseModel {

    private static final long                        serialVersionUID       = 5217123915811730145L;

    public static Map<MediumType, MediumTypeConfig>  mediumTypeConfigMap;
    static {
        mediumTypeConfigMap = new HashMap<MediumType, MediumTypeConfig>();
        mediumTypeConfigMap.put(MediumType.Email, new MediumTypeConfig(EmailSender.class, MailDetails.class));
        mediumTypeConfigMap.put(MediumType.Android, new MediumTypeConfig(
                AndroidSender.class,
                DefaultMediumDetails.class));
        mediumTypeConfigMap.put(MediumType.Sms, new MediumTypeConfig(SmsSender.class, DefaultMediumDetails.class));
        mediumTypeConfigMap.put(MediumType.ProptigerApp, new MediumTypeConfig(
                ProptigerAppSender.class,
                DefaultMediumDetails.class));
        mediumTypeConfigMap.put(MediumType.MarketplaceApp, new MediumTypeConfig(
                MarketplaceAppSender.class,
                DefaultMediumDetails.class));
    }

    private transient Class<? extends MediumSender>  senderClassName        = EmailSender.class;
    private transient Class<? extends MediumDetails> mediumDetailsClassName = DefaultMediumDetails.class;
    private transient MediumSender                   mediumSenderObject;

    public MediumTypeConfig(
            Class<? extends MediumSender> senderClassName,
            Class<? extends MediumDetails> mediumDetailsClassName) {
        if (senderClassName != null) {
            this.senderClassName = senderClassName;
        }
        if (mediumDetailsClassName != null) {
            this.mediumDetailsClassName = mediumDetailsClassName;
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

    public Class<? extends MediumDetails> getMediumDetailsClassName() {
        return mediumDetailsClassName;
    }

    public void setMediumDetailsClassName(Class<? extends MediumDetails> mediumDetailsClassName) {
        this.mediumDetailsClassName = mediumDetailsClassName;
    }

}