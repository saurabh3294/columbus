package com.proptiger.mail.service;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.proptiger.data.internal.dto.MailObject;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.exception.ProAPIException;


/**
 * Mail service to provide methods to send mails
 * @author Rajeev Pandey
 *
 */
@Service
public class MailService {
	
	@Autowired
	private MailServiceAsync mailServiceAsync;
	
	@Autowired
	private PropertyReader propertyReader;
	
	private String host;
	private String port;
	private String defaultFromAddress;
	
	@PostConstruct
	protected void init(){
		host = propertyReader.getRequiredProperty("mail.host");
		port = propertyReader.getRequiredProperty("mail.port");
		defaultFromAddress = propertyReader.getRequiredProperty("mail.defaultFromAddress");
	}

	public void sendMail(Integer userId, MailObject mailObject){
		
	}
	
	public void sendEmail(String[] mailTo, String mailFromUserName,
			String mailFromPassword, String mailText, String subject) {
		this.sendEmail(mailTo, mailFromUserName, mailFromPassword, mailText, subject, this.defaultFromAddress);
	}

	public void sendEmail(String[] mailTo, String mailFromUserName,
			String mailFromPassword, String mailText, String subject, String from) {
		final String username = mailFromUserName;
        final String password = mailFromPassword;
		
		Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", this.host);
        props.put("mail.smtp.port", this.port);
		
		Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                      protected PasswordAuthentication getPasswordAuthentication() {
                              return new PasswordAuthentication(username, password);
                      }
                });
		
	  try {

          Message message = new MimeMessage(session);
          message.setFrom(new InternetAddress(mailFromUserName));
          message.setRecipients(Message.RecipientType.TO,
                  InternetAddress.parse(StringUtils.arrayToCommaDelimitedString(mailTo)));
          message.setSubject(subject);
          message.setContent(mailText, "text/html; charset=utf-8");
          Transport.send(message);
          
      } catch (MessagingException e) {
              throw new ProAPIException(e);
      }
	}
	
	public boolean sendEmailWithAck(String[] mailTo, String mailFromUserName,
			String mailFromPassword, String mailText, String subject)
			throws InterruptedException, ExecutionException {
		Future<Boolean> future = mailServiceAsync.sendMailWithAck(mailTo,
				mailFromUserName, mailFromPassword, mailText, subject);
		return future.get();
	}
	
	public boolean sendEmailWithAck(String[] mailTo, String mailFromUserName,
			String mailFromPassword, String mailText, String subject, String from)
			throws InterruptedException, ExecutionException {
		Future<Boolean> future = mailServiceAsync.sendMailWithAck(mailTo,
				mailFromUserName, mailFromPassword, mailText, subject, from);
		return future.get();
	}

}
