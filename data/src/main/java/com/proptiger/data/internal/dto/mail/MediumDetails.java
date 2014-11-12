package com.proptiger.data.internal.dto.mail;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.proptiger.core.model.BaseModel;
import com.proptiger.data.notification.enums.MediumType;
import com.proptiger.data.notification.util.MediumDetailsSerializerDeserializer;

@JsonDeserialize(using = MediumDetailsSerializerDeserializer.class)
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
