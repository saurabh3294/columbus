package com.proptiger.data.enums.user;

import com.proptiger.core.constants.ResponseCodes;
import com.proptiger.core.constants.ResponseErrorMessages;
import com.proptiger.core.exception.BadRequestException;
import com.proptiger.core.util.Constants;

public enum UserAttributeTypes {
    PAN, PASSPORT, VOTER_ID, DRIVING_LICENSE, AADHAAR_ID, OTP_DISABLE;

    private String userAttributeType;

    public String getUserAttributeType() {
        return this.userAttributeType;
    }

    public static UserAttributeTypes getUserAttributeTypesEnum(String userAttributeType) {
        for (UserAttributeTypes attributeType : UserAttributeTypes.values()) {
            if (userAttributeType.equalsIgnoreCase(attributeType.name())) {
                return attributeType;
            }
        }
        return null;
    }

    public static void validate(String attributeKey, String attributeValue) {

        switch (getUserAttributeTypesEnum(attributeKey)) {
            case PAN:
            case DRIVING_LICENSE:
            case PASSPORT:
            case VOTER_ID:
            case AADHAAR_ID:
                if (attributeValue == null || attributeValue.isEmpty()) {
                    throw new BadRequestException(
                            ResponseCodes.BAD_REQUEST,
                            ResponseErrorMessages.User.INVALID_ATTRIBUTE_VALUE);
                }
                break;

            case OTP_DISABLE:
                if (attributeValue == null || !(attributeValue.equals(Constants.User.OTP_ATTRIBUTE_VALUE_TRUE) || attributeValue
                        .equals(Constants.User.OTP_ATTRIBUTE_VALUE_FALSE))) {
                    throw new BadRequestException(
                            ResponseCodes.BAD_REQUEST,
                            ResponseErrorMessages.User.INVALID_ATTRIBUTE_VALUE);
                }
                break;

            default:
                break;
        }
    }
}
