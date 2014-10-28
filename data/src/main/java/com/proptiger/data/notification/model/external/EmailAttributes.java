package com.proptiger.data.notification.model.external;

import java.util.ArrayList;
import java.util.List;

public class EmailAttributes {

    private String       subject;

    private String       body;

    private List<String> ccList  = new ArrayList<String>();

    private List<String> bccList = new ArrayList<String>();

    private String       fromEmail;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getCcList() {
        return ccList;
    }

    public void setCcList(List<String> ccList) {
        this.ccList = ccList;
    }

    public List<String> getBccList() {
        return bccList;
    }

    public void setBccList(List<String> bccList) {
        this.bccList = bccList;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

}
