package com.proptiger.data.notification.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.internal.dto.mail.MailBody;
import com.proptiger.data.internal.dto.mail.MailDetails;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.service.mail.AmazonMailSender;

@Service
public class EmailSender implements MediumSender {

    private static Logger    logger = LoggerFactory.getLogger(EmailSender.class);

    @Autowired
    private AmazonMailSender amazonMailSender;

    @Override
    public void send(MailBody mailBody, ForumUser forumUser) {
        String emailId = forumUser.getEmail();
        
        /*
         * For testing, please add a test email id below to avoid sending
         * unnecessary emails to actual users
         */
        emailId = "midl-team+test@proptiger.com";
        
        MailDetails mailDetails = new MailDetails(mailBody).setMailTo(emailId);
        logger.debug("Sending email " + mailBody.getBody() + " to : " + emailId);
        amazonMailSender.sendMail(mailDetails);
    }
}
