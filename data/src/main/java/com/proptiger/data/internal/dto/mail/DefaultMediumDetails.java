package com.proptiger.data.internal.dto.mail;


public class DefaultMediumDetails extends MediumDetails {

    private String message;

    public DefaultMediumDetails() {

    }

    public DefaultMediumDetails(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
