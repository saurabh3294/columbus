package com.proptiger.data.internal.dto.mail;

import com.proptiger.data.notification.enums.MediumType;

public class DefaultMediumDetails extends MediumDetails {

    private static final long serialVersionUID = 1750574121396836687L;
    
    private String message;

    public DefaultMediumDetails() {

    }

    public DefaultMediumDetails(MediumType mediumType) {
        this.setMediumType(mediumType);
    }
    
    public DefaultMediumDetails(MediumType mediumType, String message) {
        this.setMediumType(mediumType);
        this.setMessage(message);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
