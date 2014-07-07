package com.proptiger.data.util;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.constants.ResponseErrorMessages;
import com.proptiger.data.internal.dto.ChangePassword;
import com.proptiger.exception.BadRequestException;

/**
 * Password utility to validate new and old passwords
 * 
 * @author Rajeev Pandey
 *
 */
public class PasswordUtils {

    private static final int          REQUIRED_PASS_LEN = 6;
    private static Md5PasswordEncoder passwordEncoder   = new Md5PasswordEncoder();

    /**
     * validate change password requirements and set new password m5d encoded
     * after successful validation
     * 
     * @param changePassword
     */
    public static void validateChangePasword(ChangePassword changePassword) {
        if (changePassword == null) {
            throw new BadRequestException(ResponseCodes.BAD_REQUEST, ResponseErrorMessages.INVALID_PASSWORD);
        }
        if (changePassword.getOldPassword() == null || changePassword.getOldPassword().isEmpty()) {
            throw new BadRequestException(ResponseCodes.BAD_CREDENTIAL, ResponseErrorMessages.OLD_PASSWORD_REQUIRED);
        }
        String encodedPass = validateNewAndConfirmPassword(
                changePassword.getNewPassword(),
                changePassword.getConfirmNewPassword());
        changePassword.setNewPassword(encodedPass);
    }

    /**
     * This method validates password and confirm password and return encoded
     * password if successful
     * 
     * @param newPassword
     * @param confirmPassword
     * @return
     */
    public static String validateNewAndConfirmPassword(String newPassword, String confirmPassword) {
        if (newPassword == null || confirmPassword == null) {
            throw new BadRequestException(ResponseCodes.BAD_REQUEST, ResponseErrorMessages.INVALID_PASSWORD);
        }
        newPassword = newPassword.trim();
        confirmPassword = confirmPassword.trim();
        if (!newPassword.equals(confirmPassword)) {
            throw new BadRequestException(
                    ResponseCodes.BAD_CREDENTIAL,
                    ResponseErrorMessages.NEW_PASS_CONFIRM_PASS_NOT_MATCHED);
        }
        validatePasswordLength(newPassword);
        validateSpecialCharRequirement(newPassword);
        return encodePassword(newPassword);
    }

    public static String encodePassword(String pass) {
        return passwordEncoder.encodePassword(pass, null);
    }

    private static void validateSpecialCharRequirement(String newPassword) {

    }

    public static void validatePasswordLength(String newPassword) {
        if (newPassword == null || newPassword.trim().length() < REQUIRED_PASS_LEN) {
            throw new BadRequestException(
                    ResponseCodes.BAD_REQUEST,
                    "Invalid password length, minumum length required " + REQUIRED_PASS_LEN);
        }
    }

}
