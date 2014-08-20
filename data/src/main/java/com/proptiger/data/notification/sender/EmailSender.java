package com.proptiger.data.notification.sender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.internal.dto.mail.MailBody;
import com.proptiger.data.internal.dto.mail.MailDetails;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.service.mail.AmazonMailSender;

@Service
public class EmailSender implements MediumSender {
    @Autowired
    private AmazonMailSender        amazonMailSender;

    @Override
    public void send(MailBody mailBody, ForumUser forumUser) {
        MailDetails mailDetails = new MailDetails(mailBody).setMailTo(forumUser.getEmail());
        amazonMailSender.sendMail(mailDetails);
    }
}
