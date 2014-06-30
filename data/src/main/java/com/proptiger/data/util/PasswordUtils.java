package com.proptiger.data.util;

import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.constants.ResponseErrorMessages;
import com.proptiger.data.internal.dto.ChangePassword;
import com.proptiger.exception.BadRequestException;

/**
 * Password utility to valdate new and old passwords
 * 
 * @author Rajeev Pandey
 *
 */
public class PasswordUtils {

    private static final int REQUIRED_PASS_LEN = 6;

    /**
     * @param changePassword
     */
    public static void validatePasword(ChangePassword changePassword) {
        if (changePassword == null) {
            throw new BadRequestException(ResponseCodes.BAD_REQUEST, ResponseErrorMessages.INVALID_PASSWORD_DETAILS);
        }
        if (changePassword.getNewPassword() == null || changePassword.getConfirmNewPassword().isEmpty()) {
            throw new BadRequestException(ResponseCodes.BAD_REQUEST, ResponseErrorMessages.INVALID_PASSWORD_DETAILS);
        }
        if (changePassword.getOldPassword() == null || changePassword.getOldPassword().isEmpty()) {
            throw new BadRequestException(ResponseCodes.BAD_CREDENTIAL, ResponseErrorMessages.OLD_PASSWORD_REQUIRED);
        }
        String newPassword = changePassword.getNewPassword().trim();
        String confirmPassword = changePassword.getConfirmNewPassword().trim();
        if (!newPassword.equals(confirmPassword)) {
            throw new BadRequestException(
                    ResponseCodes.BAD_CREDENTIAL,
                    ResponseErrorMessages.NEW_PASS_CONFIRM_PASS_NOT_MATCHED);
        }
        validatePasswordLenth(newPassword);
        validateSpecialCharRequirement(newPassword);
    }

    private static void validateSpecialCharRequirement(String newPassword) {

    }

    private static void validatePasswordLenth(String newPassword) {
        if (newPassword.length() < REQUIRED_PASS_LEN) {
            throw new BadRequestException("Invalid password length, minumum length required " + REQUIRED_PASS_LEN);
        }
    }
}
