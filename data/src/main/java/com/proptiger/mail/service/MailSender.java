package com.proptiger.mail.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.proptiger.exception.ProAPIException;

/**
 * Mail service to provide methods to send mails
 * 
 * @author Rajeev Pandey
 * 
 */
@Service
public class MailSender {

    private static Logger           logger = LoggerFactory.getLogger(MailSender.class);

    @Autowired
    private TemplateToHtmlGenerator mailBodyGenerator;
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
    @Async
    public boolean sendMailUsingAws(
            String[] mailTo,
            String[] mailCC,
            String[] mailBCC,
            String mailContent,
            String subject) {
        if (subject == null || subject.isEmpty()) {
            throw new ProAPIException("Subject is empty");
        }
        return amazonMailSender.sendMail(mailTo, mailCC, mailBCC, mailContent, subject);
    }

    /**
     * This method accepts to address as string, and expects to be comma
     * separated if that string contains multiple email ids
     * 
     * @param mailTo
     * @param mailCC
     *            TODO
     * @param mailBCC
     *            TODO
     * @param mailContent
     * @param subject
     * @return
     */
    @Async
    public boolean sendMailUsingAws(String mailTo, String[] mailCC, String[] mailBCC, String mailContent, String subject) {
        String[] toList = mailTo.split(",");
        return sendMailUsingAws(toList, mailCC, mailBCC, mailContent, subject);
    }
}
