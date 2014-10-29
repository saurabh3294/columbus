package com.proptiger.data.init;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import com.proptiger.core.model.proptiger.Enquiry;
import com.proptiger.data.util.RegistrationUtils;

public class LeadValidator {

    public HashMap<String, String> validateLead(Enquiry enquiry) {

        HashMap<String, String> leadInvalidations = new HashMap<String, String>();

        if (enquiry.getName() == null || enquiry.getName() == "") {
            leadInvalidations.put("lead_name", "Please enter Name.");
        }

        if (enquiry.getCityName() == null) {
            leadInvalidations.put("lead_city", "Please select a City");
        }

        if (enquiry.getCountry() == null) {
            leadInvalidations.put("lead_country", "Please select Country");
        }

        if (enquiry.getEmail() == null || enquiry.getEmail() == "") {
            leadInvalidations.put("lead_email", "Please enter Email.");
        }
        else {
            if (!RegistrationUtils.validateEmail(enquiry.getEmail())) {
                leadInvalidations.put("lead_email", "Please enter a valid Email.");
            }
        }

        if (enquiry.getPhone() == null) {
            leadInvalidations.put("lead_name", "Please enter Phone.");
        }
        else {

            String phonePattern = "^[_0-9-+]+$";
            Pattern pattern = Pattern.compile(phonePattern);
            Matcher matcher = pattern.matcher(enquiry.getPhone());

            if (!matcher.matches()) {
                leadInvalidations.put("lead_phone", "phone number can contain Numbers, -, +, _ only ");
            }

            String phoneNumber = enquiry.getPhone().replaceAll("\\D", "");
            String phone = null;

            if (enquiry.getCountry().toLowerCase() == "india") {
                if (enquiry.getPhone().substring(0, 2) == "+91") {
                    phone = phoneNumber.substring(2);
                }
                else {
                    phone = StringUtils.trimLeadingCharacter(phoneNumber, '0');
                }

                if (phone.length() != 10) {
                    leadInvalidations.put("lead_phone", "Phone number must be 10 digits for India ");
                }
            }
            else {
                phone = phoneNumber;
            }

            if ((phone.length() < 6) || (phone.length() > 12 && enquiry.getPhone().charAt(0) != '+')
                    || (phone.length() > 15)) {
                leadInvalidations.put("lead_phone", "Phone number must be between 6 and 12 characters in length.");
            }
        }
        return leadInvalidations;
    }

}