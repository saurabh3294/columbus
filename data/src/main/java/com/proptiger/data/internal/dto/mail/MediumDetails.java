package com.proptiger.data.internal.dto.mail;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.proptiger.core.model.BaseModel;
import com.proptiger.data.notification.enums.MediumType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DefaultMediumDetails.class, name = "DefaultMediumDetails"),
        @JsonSubTypes.Type(value = MailDetails.class, name = "MailDetails") })
public abstract class MediumDetails extends BaseModel {

    private static final long serialVersionUID = 8471921365095921737L;

    private MediumType        mediumType;

    public MediumType getMediumType() {
        return mediumType;
    }

    public void setMediumType(MediumType mediumType) {
        this.mediumType = mediumType;
    }

}
