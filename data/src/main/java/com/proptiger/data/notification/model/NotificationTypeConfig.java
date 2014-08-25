package com.proptiger.data.notification.model;

import java.util.HashMap;
import java.util.Map;

import com.proptiger.data.notification.model.payload.NotificationTypePayload;
import com.proptiger.data.notification.processor.DefaultNotificationMessageProcessor;
import com.proptiger.data.notification.processor.NotificationMessageProcessor;
import com.proptiger.data.notification.processor.NotificationNonPrimaryKeyProcessor;
import com.proptiger.data.notification.processor.NotificationPrimaryKeyProcessor;

public class NotificationTypeConfig {

    static {
        notificationTypeConfigMap = new HashMap<String, NotificationTypeConfig>();
    }

    private transient Class<? extends NotificationTypePayload>            dataClassName                         = NotificationTypePayload.class;
    private transient Class<? extends NotificationPrimaryKeyProcessor>    primaryKeyProcessorClassName          = NotificationPrimaryKeyProcessor.class;
    private transient Class<? extends NotificationNonPrimaryKeyProcessor> nonPrimaryKeyProcessorClassName       = NotificationNonPrimaryKeyProcessor.class;
    private transient Class<? extends NotificationMessageProcessor>       notificationMessageProcessorClassName = DefaultNotificationMessageProcessor.class;

    private transient NotificationPrimaryKeyProcessor                     primaryKeyProcessorObject;
    private transient NotificationNonPrimaryKeyProcessor                  nonPrimaryKeyProcessorObject;
    private transient NotificationTypePayload                             notificationTypePayloadObject;
    private transient NotificationMessageProcessor                        notificationMessageProcessorObject;

    public transient static Map<String, NotificationTypeConfig>           notificationTypeConfigMap;

    public NotificationTypeConfig(
            Class<? extends NotificationTypePayload> dataClassName,
            Class<? extends NotificationPrimaryKeyProcessor> primaryKeyProcessorClassName,
            Class<? extends NotificationNonPrimaryKeyProcessor> nonPrimaryKeyProcessorClassName,
            Class<? extends NotificationMessageProcessor> notificationMessageProcessorClassName) {
        super();
        if (dataClassName != null) {
            this.dataClassName = dataClassName;
        }
        if (primaryKeyProcessorClassName != null) {
            this.primaryKeyProcessorClassName = primaryKeyProcessorClassName;
        }
        if (nonPrimaryKeyProcessorClassName != null) {
            this.nonPrimaryKeyProcessorClassName = nonPrimaryKeyProcessorClassName;
        }
        if (notificationMessageProcessorClassName != null) {
            this.notificationMessageProcessorClassName = notificationMessageProcessorClassName;
        }
    }

    public NotificationTypeConfig() {
        // TODO Auto-generated constructor stub
    }

    public Class<? extends NotificationTypePayload> getDataClassName() {
        return dataClassName;
    }

    public void setDataClassName(Class<? extends NotificationTypePayload> dataClassName) {
        this.dataClassName = dataClassName;
    }

    public NotificationTypePayload getNotificationTypePayloadObject() {
        return notificationTypePayloadObject;
    }

    public void setNotificationTypePayloadObject(NotificationTypePayload notificationTypePayloadObject) {
        this.notificationTypePayloadObject = notificationTypePayloadObject;
    }

    public static Map<String, NotificationTypeConfig> getNotificationTypeConfigMap() {
        return notificationTypeConfigMap;
    }

    public static void setNotificationTypeConfigMap(Map<String, NotificationTypeConfig> notificationTypeConfigMap) {
        NotificationTypeConfig.notificationTypeConfigMap = notificationTypeConfigMap;
    }

    public Class<? extends NotificationPrimaryKeyProcessor> getPrimaryKeyProcessorClassName() {
        return primaryKeyProcessorClassName;
    }

    public void setPrimaryKeyProcessorClassName(
            Class<? extends NotificationPrimaryKeyProcessor> primaryKeyProcessorClassName) {
        this.primaryKeyProcessorClassName = primaryKeyProcessorClassName;
    }

    public Class<? extends NotificationNonPrimaryKeyProcessor> getNonPrimaryKeyProcessorClassName() {
        return nonPrimaryKeyProcessorClassName;
    }

    public void setNonPrimaryKeyProcessorClassName(
            Class<? extends NotificationNonPrimaryKeyProcessor> nonPrimaryKeyProcessorClassName) {
        this.nonPrimaryKeyProcessorClassName = nonPrimaryKeyProcessorClassName;
    }

    public NotificationPrimaryKeyProcessor getPrimaryKeyProcessorObject() {
        return primaryKeyProcessorObject;
    }

    public void setPrimaryKeyProcessorObject(NotificationPrimaryKeyProcessor primaryKeyProcessorObject) {
        this.primaryKeyProcessorObject = primaryKeyProcessorObject;
    }

    public NotificationNonPrimaryKeyProcessor getNonPrimaryKeyProcessorObject() {
        return nonPrimaryKeyProcessorObject;
    }

    public void setNonPrimaryKeyProcessorObject(NotificationNonPrimaryKeyProcessor nonPrimaryKeyProcessorObject) {
        this.nonPrimaryKeyProcessorObject = nonPrimaryKeyProcessorObject;
    }

    public Class<? extends NotificationMessageProcessor> getNotificationMessageProcessorClassName() {
        return notificationMessageProcessorClassName;
    }

    public void setNotificationMessageProcessorClassName(
            Class<? extends NotificationMessageProcessor> notificationMessageProcessorClassName) {
        this.notificationMessageProcessorClassName = notificationMessageProcessorClassName;
    }

    public NotificationMessageProcessor getNotificationMessageProcessorObject() {
        return notificationMessageProcessorObject;
    }

    public void setNotificationMessageProcessorObject(NotificationMessageProcessor notificationMessageProcessorObject) {
        this.notificationMessageProcessorObject = notificationMessageProcessorObject;
    }

}
