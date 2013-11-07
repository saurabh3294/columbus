package com.proptiger.mail.service;

import java.util.concurrent.Future;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;


/**
 * Mail service to provide methods to send mails
 * @author Rajeev Pandey
 *
 */
//@Service
public class MailService {
	
	private static Logger logger = LoggerFactory.getLogger(MailService.class);
	@Autowired
	private AmazonMailSender amazonMailSender;
	
	@Async
	public void sendMailUsingAws(String[] mailTo, String mailContent, String subject){
		if(mailTo == null || mailTo.length == 0){
			throw new IllegalArgumentException("To address is empty");
		}
		try {
			amazonMailSender.sendMail(mailTo, mailContent, subject);
		}catch (MailException ex) {
			logger.error("Mail not sent", ex);
		}
	}
	
	@Async
	public Future<Boolean> sendMailUsingAwsWithAck(String[] mailTo, String mailContent, String subject){
		if(mailTo == null || mailTo.length == 0){
			throw new IllegalArgumentException("To address is empty");
		}
		try {
			amazonMailSender.sendMail(mailTo, mailContent, subject);
			return new AsyncResult<Boolean>(true);
		}catch (MailException ex) {
			logger.error("Mail not sent", ex);
			return new AsyncResult<Boolean>(false);
		}
	}

}
