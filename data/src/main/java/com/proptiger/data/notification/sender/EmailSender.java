package com.proptiger.data.notification.sender;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptiger.data.internal.dto.mail.MailBody;
import com.proptiger.data.internal.dto.mail.MailDetails;
import com.proptiger.data.model.user.User;
import com.proptiger.data.notification.model.payload.EmailSenderPayload;
import com.proptiger.data.notification.model.payload.NotificationSenderPayload;
import com.proptiger.data.service.mail.AmazonMailSender;
import com.proptiger.data.service.user.UserService;

@Service
public class EmailSender implements MediumSender {

    private static Logger       logger  = LoggerFactory.getLogger(EmailSender.class);

    private static final String SUBJECT = "subject";
    private static final String BODY    = "body";

    @Autowired
    private AmazonMailSender    amazonMailSender;

    @Autowired
    private UserService         userService;

    @Override
    public boolean send(String template, Integer userId, String typeName, NotificationSenderPayload payload) {

        if (userId == null || template == null || payload == null) {
            logger.error("Found UserId: " + userId
                    + " template: "
                    + template
                    + " payload: "
                    + payload
                    + " typeName: "
                    + typeName
                    + " while sending email.");
            return false;
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            logger.error("No user found with UserId: " + userId + " while sending email.");
            return false;
        }

        String emailId = user.getEmail();
        if (emailId == null) {
            logger.error("No email found for UserId: " + userId + " while sending email.");
            return false;
        }

        MailBody mailBody = getMailBody(template);

        MailDetails mailDetails = new MailDetails(mailBody).setMailTo(emailId);
        EmailSenderPayload emailSenderPayload = (EmailSenderPayload) payload;
        if (emailSenderPayload.getFromEmail() != null) {
            mailDetails.setFrom(emailSenderPayload.getFromEmail());
        }
        List<String> ccList = emailSenderPayload.getCcList();
        if (ccList != null && !ccList.isEmpty()) {
            mailDetails.setMailCC(ccList.toArray(new String[ccList.size()]));
        }
        List<String> bccList = emailSenderPayload.getBccList();
        if (bccList != null && !bccList.isEmpty()) {
            mailDetails.setMailBCC(bccList.toArray(new String[bccList.size()]));
        }
        logger.debug("Sending email with mailDetails: " + mailDetails);
        amazonMailSender.sendMail(mailDetails);

        return true;
    }

    private MailBody getMailBody(String template) {
        if (template == null || template.isEmpty()) {
            logger.info("Template is null.");
            return null;
        }

        HashMap<String, String> mailContentMap = getMailContentFromJsonTemplate(template);
        if (mailContentMap == null || mailContentMap.isEmpty()) {
            return null;
        }

        String subject = mailContentMap.get(SUBJECT);
        String body = mailContentMap.get(BODY);

        if (subject == null || subject.isEmpty() || body == null || body.isEmpty()) {
            logger.info("Subject or Body is null or empty");
            return null;
        }

        // creating mail body and setting mail subject and mail body.
        MailBody mailBody = new MailBody();
        mailBody.setSubject(subject);
        mailBody.setBody(body);
        return mailBody;
    }

    private HashMap<String, String> getMailContentFromJsonTemplate(String template) {
        HashMap<String, String> map = new HashMap<String, String>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            map = mapper.readValue(template, HashMap.class);
            logger.debug("MailContentMap: " + map.toString());
            return map;
        }
        catch (Exception e) {
            logger.error("Error while getting MailContent From JsonTemplate.", e.getStackTrace().toString());
        }
        return null;
    }
}
