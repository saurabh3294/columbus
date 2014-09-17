package com.proptiger.data.notification.sender;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptiger.data.internal.dto.mail.MailBody;
import com.proptiger.data.internal.dto.mail.MailDetails;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.service.mail.AmazonMailSender;

@Service
public class EmailSender implements MediumSender {

    private static Logger       logger  = LoggerFactory.getLogger(EmailSender.class);

    private static final String SUBJECT = "subject";
    private static final String BODY    = "body";

    @Autowired
    private AmazonMailSender    amazonMailSender;

    @Override
    public void send(String template, ForumUser forumUser, String typeName) {
        String emailId = forumUser.getEmail();
        MailBody mailBody = getMailBody(template);

        MailDetails mailDetails = new MailDetails(mailBody).setMailTo(emailId);
        logger.debug("Sending email " + mailBody.getBody() + " to : " + emailId);
        amazonMailSender.sendMail(mailDetails);
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
