package com.proptiger.data.internal.dto.mail;

public class DefaultMediumDetails extends MediumDetails {

    private static final long serialVersionUID = 1750574121396836687L;
    
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
