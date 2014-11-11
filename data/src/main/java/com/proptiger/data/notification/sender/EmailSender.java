package com.proptiger.data.notification.sender;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptiger.core.model.user.User;
import com.proptiger.data.internal.dto.mail.MailBody;
import com.proptiger.data.internal.dto.mail.MailDetails;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;
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

    @Autowired
    private TemplateGenerator   templateGenerator;

    @Override
    public boolean send(NotificationGenerated nGenerated) {

        Integer userId = nGenerated.getUserId();
        String typeName = nGenerated.getNotificationType().getName();
        if (userId == null) {
            logger.error("UserId not found for notification generated id: " + nGenerated.getId()
                    + " and typeName: "
                    + typeName
                    + " while sending email.");
            return false;
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            logger.error("No user found with UserId: " + userId
                    + " while sending email for notification generated id: "
                    + nGenerated.getId()
                    + " and typeName: "
                    + typeName);
            return false;
        }

        String emailId = user.getEmail();
        if (emailId == null) {
            logger.error("No email found for UserId: " + userId
                    + " while sending email for notification generated id: "
                    + nGenerated.getId()
                    + " and typeName: "
                    + typeName);
            return false;
        }

        NotificationMessagePayload payload = nGenerated.getNotificationMessagePayload();
        MailDetails mailDetails;
        if (payload.getMediumDetails() != null) {
            mailDetails = (MailDetails) payload.getMediumDetails();
        }
        else {
            mailDetails = new MailDetails();
        }
        mailDetails.setMailTo(emailId);

        MailBody mailBody = null;
        if (mailDetails.getBody() == null || mailDetails.getSubject() == null) {
            mailBody = getMailBody(nGenerated);
        }

        if ((mailDetails.getBody() == null || mailDetails.getSubject() == null) && mailBody == null) {
            logger.error("Email subject/body not found in DB/Payload while sending email for notification generated id: " + nGenerated
                    .getId() + " and typeName: " + typeName);
            return false;
        }

        if (mailDetails.getSubject() == null) {
            mailDetails.setSubject(mailBody.getSubject());
        }

        if (mailDetails.getBody() == null) {
            mailDetails.setBody(mailBody.getBody());
        }

        // TODO: Below code is deprecated,
        // it is added just for backward compatibility.
        if (payload.getFromEmail() != null) {
            mailDetails.setFrom(payload.getFromEmail());
        }
        List<String> ccList = payload.getCcList();
        if (ccList != null && !ccList.isEmpty()) {
            mailDetails.setMailCC(ccList.toArray(new String[ccList.size()]));
        }
        List<String> bccList = payload.getBccList();
        if (bccList != null && !bccList.isEmpty()) {
            mailDetails.setMailBCC(bccList.toArray(new String[bccList.size()]));
        }

        logger.debug("Sending email with mailDetails: " + mailDetails);
        amazonMailSender.sendMail(mailDetails);

        return true;
    }

    private MailBody getMailBody(NotificationGenerated nGenerated) {
        String template = templateGenerator.generatePopulatedTemplate(nGenerated);

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
