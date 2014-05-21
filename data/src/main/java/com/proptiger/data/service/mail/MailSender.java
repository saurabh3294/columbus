package com.proptiger.data.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.internal.dto.mail.MailDetails;

/**
 * Mail service to provide methods to send mails
 * 
 * @author Rajeev Pandey
 * 
 */
@Service
public class MailSender {
    @Autowired
    private AmazonMailSender        amazonMailSender;

    /**
     * Sending mail using amazon mail service in asynch manner, so call to this
     * method will return immediately.
     * 
     * @param mailTo
     * @param mailCC
     *            TODO
     * @param mainBCC
     *            TODO
     * @param mailContent
     * @param subject
     */
    
    public boolean sendMailUsingAws(MailDetails mailDetails) {
        return amazonMailSender.sendMail(mailDetails);
    }
}
