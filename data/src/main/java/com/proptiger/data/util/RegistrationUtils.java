package com.proptiger.data.util;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.validator.routines.EmailValidator;

import com.proptiger.core.constants.ResponseCodes;
import com.proptiger.core.constants.ResponseErrorMessages;
import com.proptiger.core.model.user.UserContactNumber;
import com.proptiger.data.internal.dto.RegisterUser;
import com.proptiger.exception.BadRequestException;

/**
 * Util class to validate user registration data
 * 
 * @author Rajeev Pandey
 * 
 */
public class RegistrationUtils {

    private static final int REQUIRED_USERNAME_LEN = 2;

    /**
     * Validate new user registration data, and encode password
     * 
     * @param register
     */
    public static void validateRegistration(RegisterUser register) {
        /*
         * to make it to work for new website that does not take confirm
         * password field on UI.
         */
        if (register.getConfirmPassword() == null) {
            register.setConfirmPassword(register.getPassword());
        }
        validateName(register.getFullName());
        validateEmail(register.getEmail());
        String encodedPass = PasswordUtils.validateNewAndConfirmPassword(
                register.getPassword(),
                register.getConfirmPassword());
        register.setPassword(encodedPass);

        if (register.getCountryId() == null) {
            throw new BadRequestException(ResponseCodes.BAD_REQUEST, ResponseErrorMessages.INVALID_COUNTRY);
        }
        validateContactNumber(register.getContactNumbers());
    }

    private static void validateContactNumber(Set<UserContactNumber> contacts) {
        // TODO can check minimum and maximum length of phone number as well
        if(contacts != null){
            Iterator<UserContactNumber> it = contacts.iterator();
            while(it.hasNext()){
                UserContactNumber contact = it.next();
                if (contact.getContactNumber() == null || contact.getContactNumber().isEmpty()) {
                    throw new BadRequestException(ResponseCodes.BAD_REQUEST, ResponseErrorMessages.INVALID_CONTACT_NUMBER);
                }
            }
        }
        else{
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
