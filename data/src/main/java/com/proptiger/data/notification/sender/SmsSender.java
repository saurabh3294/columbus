package com.proptiger.data.notification.sender;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.proptiger.core.model.user.UserContactNumber;
import com.proptiger.data.internal.dto.mail.DefaultMediumDetails;
import com.proptiger.data.internal.dto.mail.MediumDetails;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.service.user.UserService;

@Service
public class SmsSender implements MediumSender {

    private static Logger       logger         = LoggerFactory.getLogger(SmsSender.class);

    private static final String USERNAME_KEY   = "username";
    private static final String PASSWORD_KEY   = "password";
    private static final String SMS_TO_KEY     = "to";
    private static final String SMS_FROM_KEY   = "from";
    private static final String SMS_TEXT       = "text";
    private static final String CONTACT_PREFIX = "91";

    @Value("${app.sms.baseUrl}")
    private String              BASE_URL;

    @Value("${app.sms.username}")
    private String              USERNAME;

    @Value("${app.sms.password}")
    private String              PASSWORD;

    @Value("${app.sms.senderId}")
    private String              SENDER_ID;

    @Autowired
    private UserService         userService;

    @Override
    public boolean send(String template, NotificationGenerated nGenerated) {

        MediumDetails mediumDetails = nGenerated.getNotificationMessagePayload().getMediumDetails();
        DefaultMediumDetails defaultMediumDetails = null;
        if (mediumDetails != null) {
            defaultMediumDetails = (DefaultMediumDetails) mediumDetails;
            if (defaultMediumDetails.getMessage() != null) {
                template = defaultMediumDetails.getMessage();
            }
        }

        Integer userId = nGenerated.getUserId();
        if (userId == null) {
            logger.error("Found null User Id while sending SMS.");
            return false;
        }

        UserContactNumber userContactNumber = userService.getTopPriorityContact(userId);
        if (userContactNumber == null) {
            logger.error("No contact number found for UserId: " + userId + " while sending SMS.");
            return false;
        }

        String contact = userContactNumber.getContactNumber();

        Map<String, String> urlVariables = new HashMap<String, String>();
        urlVariables.put(USERNAME_KEY, USERNAME);
        urlVariables.put(PASSWORD_KEY, PASSWORD);
        urlVariables.put(SMS_TO_KEY, CONTACT_PREFIX + contact);
        urlVariables.put(SMS_FROM_KEY, SENDER_ID);
        urlVariables.put(SMS_TEXT, template);

        RestTemplate restTemplate = new RestTemplate();
        logger.info("Sending SMS request to BaseURL: " + BASE_URL
                + " for contact: "
                + contact
                + " with message = \""
                + template
                + "\"");
        logger.debug("urlVariables: " + urlVariables.toString());
        String response = restTemplate.getForObject(BASE_URL, String.class, urlVariables);
        logger.info("Received SMS response \"" + response + "\" for contact: " + contact);

        return true;
    }

}
