package com.proptiger.data.util;

import org.apache.commons.validator.routines.EmailValidator;

import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.constants.ResponseErrorMessages;
import com.proptiger.data.internal.dto.Register;
import com.proptiger.exception.BadRequestException;

/**
 * Util class to validate user registration data
 * 
 * @author Rajeev Pandey
 *
 */
public class RegistrationUtils {

    private static final int REQUIRED_CONTACT_LEN  = 5;
    private static final int REQUIRED_USERNAME_LEN = 2;

    /**
     * Validate new user registration data, and encode password
     * 
     * @param register
     */
    public static void validateRegistration(Register register) {
        /*
         * to make it to work for new website that does not take confirm
         * password field on UI.
         */
        if (register.getConfirmPassword() == null) {
            register.setConfirmPassword(register.getPassword());
        }
        validateName(register.getUserName());
        validateEmail(register.getEmail());
        String encodedPass = PasswordUtils.validateNewAndConfirmPassword(
                register.getPassword(),
                register.getConfirmPassword());

        if (register.getCountryId() == null) {
            throw new BadRequestException(ResponseCodes.BAD_REQUEST, ResponseErrorMessages.INVALID_COUNTRY);
        }
        validateContactNumber(register.getContact());
        register.setPassword(encodedPass);
    }

    private static void validateContactNumber(Long contact) {
        //TODO can check minimum and maximum length of phone number as well
        if (contact == null || contact <= 0) {
            throw new BadRequestException(ResponseCodes.BAD_REQUEST, ResponseErrorMessages.INVALID_CONTACT_NUMBER);
        }
    }

    private static void validateEmail(String email) {
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new BadRequestException(ResponseCodes.BAD_REQUEST, ResponseErrorMessages.INVALID_EMAIL);
        }
    }

    private static void validateName(String username) {
        if (username == null || username.trim().length() < REQUIRED_USERNAME_LEN) {
            throw new BadRequestException(
                    ResponseCodes.BAD_REQUEST,
                    ResponseErrorMessages.INVALID_USERNAME_NAME_LEN + ", required length " + REQUIRED_USERNAME_LEN);
        }
    }

}
