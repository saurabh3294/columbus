package com.proptiger.data.notification.model.payload;

import java.util.ArrayList;
import java.util.List;

import com.proptiger.core.model.BaseModel;
import com.proptiger.data.internal.dto.mail.MediumDetails;

public class NotificationMessagePayload extends BaseModel {

    /**
     * 
     */
    private static final long                      serialVersionUID                   = -2452873800997415163L;

    private NotificationTypePayload                notificationTypePayload;

    private List<NotificationMessagePayload>       notificationMessagePayloads        = new ArrayList<NotificationMessagePayload>();

    private List<NotificationMessageUpdateHistory> notificationMessageUpdateHistories = new ArrayList<NotificationMessageUpdateHistory>();

    @Deprecated
    private String                                 fromEmail;

    @Deprecated
    private List<String>                           ccList;

    @Deprecated
    private List<String>                           bccList;

    private MediumDetails                          mediumDetails;

    public NotificationMessagePayload() {

    }

    public NotificationMessagePayload(NotificationMessagePayload payload) {
        this.notificationTypePayload = payload.getNotificationTypePayload();
        this.notificationMessagePayloads = payload.getNotificationMessagePayloads();
        this.notificationMessageUpdateHistories = payload.getNotificationMessageUpdateHistories();
        this.fromEmail = payload.getFromEmail();
        this.ccList = payload.getCcList();
        this.bccList = payload.getBccList();
        this.mediumDetails = payload.getMediumDetails();
        this.extraAttributes = payload.getExtraAttributes();
    }

    public List<NotificationMessageUpdateHistory> getNotificationMessageUpdateHistories() {
        return notificationMessageUpdateHistories;
    }

    public void setNotificationMessageUpdateHistories(
            List<NotificationMessageUpdateHistory> notificationMessageUpdateHistories) {
        this.notificationMessageUpdateHistories = notificationMessageUpdateHistories;
    }

    public NotificationTypePayload getNotificationTypePayload() {
        return notificationTypePayload;
    }

    public void setNotificationTypePayload(NotificationTypePayload notificationTypePayload) {
        this.notificationTypePayload = notificationTypePayload;
    }

    public List<NotificationMessagePayload> getNotificationMessagePayloads() {
        return notificationMessagePayloads;
    }

    public void setNotificationMessagePayloads(List<NotificationMessagePayload> notificationMessagePayloads) {
        this.notificationMessagePayloads = notificationMessagePayloads;
    }

    @Deprecated
    public String getFromEmail() {
        return fromEmail;
    }

    @Deprecated
    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    @Deprecated
    public List<String> getCcList() {
        return ccList;
    }

    @Deprecated
    public void setCcList(List<String> ccList) {
        this.ccList = ccList;
    }

    @Deprecated
    public List<String> getBccList() {
        return bccList;
    }

    @Deprecated
    public void setBccList(List<String> bccList) {
        this.bccList = bccList;
    }

    public MediumDetails getMediumDetails() {
        return mediumDetails;
    }

    public void setMediumDetails(MediumDetails mediumDetails) {
        this.mediumDetails = mediumDetails;
    }

}
