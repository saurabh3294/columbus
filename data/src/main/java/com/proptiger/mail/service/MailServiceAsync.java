package com.proptiger.mail.service;

import java.util.concurrent.Future;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;


/**
 * Mail sending methods with and without ack
 * @author Rajeev Pandey
 *
 */
@Service
public class MailServiceAsync {
	@Autowired
	private JavaMailSenderImpl mailSender;

	@Async
	public void sendMail(String[] mailTo, String mailFromUserName,
			String mailFromPassword, String mailText, String subject)
			throws MailException {
		MimeMessagePreparator preparator = createMimeMessagePreparator(mailTo,
				mailFromUserName, mailFromPassword, mailText, subject);
		this.mailSender.send(preparator);
	}
	
	@Async
	public void sendMail(String[] mailTo, String mailFromUserName,
			String mailFromPassword, String mailText, String subject, String from)
			throws MailException {
		MimeMessagePreparator preparator = createMimeMessagePreparator(mailTo,
				mailFromUserName, mailFromPassword, mailText, subject, from);
		this.mailSender.send(preparator);
	}

	private MimeMessagePreparator createMimeMessagePreparator(
			final String[] mailTo, final String mailFromUserName,
			final String mailFromPassword, final String mailText,
			final String subject, final String from) {

		this.mailSender.setUsername(mailFromUserName);
		this.mailSender.setPassword(mailFromPassword);

		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
				message.setTo(mailTo);
				message.setSubject(subject);
				message.setText(mailText, true);
				if(from != null) {
					message.setFrom(new InternetAddress(from));
				}
				
			}
		};
		return preparator;
	}
	
	private MimeMessagePreparator createMimeMessagePreparator(
			final String[] mailTo, final String mailFromUserName,
			final String mailFromPassword, final String mailText,
			final String subject) {
		// call the overloaded method with 'null' for for the 'from' field
		return createMimeMessagePreparator(mailTo, mailFromUserName, mailFromPassword, mailText, subject, null);
	}

	@Async
	public Future<Boolean> sendMailWithAck(String[] mailTo,
			String mailFromUserName, String mailFromPassword, String mailText,
			String subject) {
		MimeMessagePreparator preparator = createMimeMessagePreparator(mailTo,
				mailFromUserName, mailFromPassword, mailText, subject);
		try {
			this.mailSender.send(preparator);
			return new AsyncResult<Boolean>(true);
		} catch (MailException ex) {
			ex.printStackTrace();
			return new AsyncResult<Boolean>(false);
		}
	}
	
	@Async
	public Future<Boolean> sendMailWithAck(String[] mailTo,
			String mailFromUserName, String mailFromPassword, String mailText,
			String subject, String from) {
		MimeMessagePreparator preparator = createMimeMessagePreparator(mailTo,
				mailFromUserName, mailFromPassword, mailText, subject, from);
		System.out.println("Sending email from: " + from);
		try {
			this.mailSender.send(preparator);
			return new AsyncResult<Boolean>(true);
		} catch (MailException ex) {
			return new AsyncResult<Boolean>(false);
		}
	}
}