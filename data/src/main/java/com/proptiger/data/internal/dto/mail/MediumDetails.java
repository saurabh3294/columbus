package com.proptiger.data.internal.dto.mail;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.proptiger.core.model.BaseModel;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DefaultMediumDetails.class, name = "DefaultMediumDetails"),
        @JsonSubTypes.Type(value = MailDetails.class, name = "MailDetails") })
public abstract class MediumDetails extends BaseModel {

    private static final long serialVersionUID = 8471921365095921737L;

}
