package com.proptiger.mail.service;

import java.io.IOException;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.proptiger.data.util.PropertyReader;

/**
 * Using amazon web service to send mails
 * @author Rajeev Pandey
 *
 */
@Service
public class AmazonMailSender {

	private static Logger logger = LoggerFactory.getLogger(AmazonMailSender.class);
	private AmazonSimpleEmailServiceClient emailServiceClient;
	private String from;
	
	@Autowired
	private PropertyReader propertyReader;
	
	@PostConstruct
	protected void init() throws IOException{
		PropertiesCredentials credentials = new PropertiesCredentials(
				this.getClass()
		        .getClassLoader()
		        .getResourceAsStream("amazon-credential.properties"));
		// Retrieve the AWS Access Key ID and Secret Key from amazon-credential.properties.
		credentials.getAWSAccessKeyId();
        credentials.getAWSSecretKey();
		emailServiceClient = new AmazonSimpleEmailServiceClient(credentials);
		from = propertyReader.getRequiredProperty("mail.from.noreply");
	}
	
	public boolean sendMail(String[] mailTo, String mailContent, String subject)
			throws MailException {
		// Construct an object to contain the recipient address.
        Destination destination = new Destination().withToAddresses(mailTo);
        
        // Create the subject and body of the message.
        Content mailSubject = new Content().withData(subject);
        Content textBody = new Content().withData(mailContent); 
        Body body = new Body().withHtml(textBody);
        
        // Create a message with the specified subject and body.
        Message message = new Message().withSubject(mailSubject).withBody(body);
        
        // Assemble the email.
		SendEmailRequest request = new SendEmailRequest().withSource(from)
				.withDestination(destination).withMessage(message);
		logger.debug("Sending mails to {}", Arrays.toString(mailTo));
        SendEmailResult result = emailServiceClient.sendEmail(request);
        logger.debug("Mail sent id {}", result.getMessageId());
        return true;
	}
}
