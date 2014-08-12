package com.proptiger.data.notification.model;

import java.util.HashMap;
import java.util.Map;

import com.proptiger.data.model.BaseModel;
import com.proptiger.data.notification.model.payload.DefaultNotificationTypePayload;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;

public class NotificationTypeConfig extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = -5901529876026943753L;

    private Class<? extends NotificationTypePayload>  dataClassName    = DefaultNotificationTypePayload.class;
    private NotificationTypePayload                   notificationTypePayloadObject;

    public static Map<String, NotificationTypeConfig> notificationTypeConfigMap;

    static {
        notificationTypeConfigMap = new HashMap<String, NotificationTypeConfig>();
    }

    NotificationTypeConfig(Class<? extends NotificationTypePayload> dataClassName) {
        if (dataClassName != null) {
            this.dataClassName = dataClassName;
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

}
