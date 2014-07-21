package com.proptiger.data.service.mail;

import java.io.IOException;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.proptiger.data.internal.dto.mail.MailDetails;
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.exception.ProAPIException;

/**
 * Using amazon web service to send mails
 * 
 * @author Rajeev Pandey
 * 
 */
@Service
public class AmazonMailSender {

    private static Logger                  logger = LoggerFactory.getLogger(AmazonMailSender.class);
    private AmazonSimpleEmailServiceClient emailServiceClient;
    private String                         from;

    @Autowired
    private PropertyReader                 propertyReader;

    @PostConstruct
    protected void init() throws IOException {
        PropertiesCredentials credentials = new PropertiesCredentials(this.getClass().getClassLoader()
                .getResourceAsStream("amazon-credential.properties"));
        // Retrieve the AWS Access Key ID and Secret Key from
        // amazon-credential.properties.
        credentials.getAWSAccessKeyId();
        credentials.getAWSSecretKey();
        emailServiceClient = new AmazonSimpleEmailServiceClient(credentials);
        from = propertyReader.getRequiredProperty(PropertyKeys.MAIL_FROM_NOREPLY);
    }
    @Async
    public void sendMail(MailDetails mailDetails)
            throws MailException {
        // Construct an object to contain the recipient address.
        validateFromAndToAddress(mailDetails.getMailTo());
        validateSubject(mailDetails.getSubject());
        Destination destination = new Destination().withToAddresses(mailDetails.getMailTo());
        if (mailDetails.getMailCC() != null && mailDetails.getMailCC().length > 0)
            destination.withCcAddresses(mailDetails.getMailCC());
        if (mailDetails.getMailBCC() != null && mailDetails.getMailBCC().length > 0)
            destination.withBccAddresses(mailDetails.getMailBCC());

        // Create the subject and body of the message.
        Content mailSubject = new Content().withData(mailDetails.getSubject());
        Content textBody = new Content().withData(mailDetails.getBody());
        Body body = new Body().withHtml(textBody);

        // Create a message with the specified subject and body.
        Message message = new Message().withSubject(mailSubject).withBody(body);

        // Assemble the email.
        SendEmailRequest request = new SendEmailRequest().withSource(from).withDestination(destination)
                .withMessage(message);
        logger.debug("Sending mails to {}", Arrays.toString(mailDetails.getMailTo()));
        SendEmailResult result = emailServiceClient.sendEmail(request);
        logger.debug("Mail sent id {}", result.getMessageId());
    }

	private void validateSubject(String subject) {
		if (subject == null || subject.isEmpty()) {
            throw new ProAPIException("Subject is empty");
        }
	}

	private void validateFromAndToAddress(String[] mailTo) {
		if (from == null || from.isEmpty()) {
            logger.debug("from email-Id is null or Empty");
            throw new ProAPIException("from email-Id is null or Empty");
        }
        if (mailTo == null || mailTo.length == 0) {
            logger.debug("To email-Id is null or Empty");
            throw new ProAPIException("from email-Id is null or Empty");
        }
	}
}
