package com.proptiger.data.notification.model.payload;

import java.util.List;

import com.proptiger.data.notification.model.NotificationGenerated;

public class EmailSenderPayload implements NotificationSenderPayload {

    private String       fromEmail;

    private List<String> ccList;

    private List<String> bccList;

    @Override
    public NotificationSenderPayload populatePayload(NotificationGenerated nGenerated) {
        NotificationMessagePayload payload = nGenerated.getNotificationMessagePayload();
        this.fromEmail = payload.getFromEmail();
        this.ccList = payload.getCcList();
        this.bccList = payload.getBccList();
        return this;
    }

    public List<String> getCcList() {
        return ccList;
    }

    public void setCcList(List<String> ccList) {
        this.ccList = ccList;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public List<String> getBccList() {
        return bccList;
    }

    public void setBccList(List<String> bccList) {
        this.bccList = bccList;
    }

}
