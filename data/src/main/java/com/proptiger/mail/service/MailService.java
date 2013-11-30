package com.proptiger.mail.service;

import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.proptiger.exception.ProAPIException;


/**
 * Mail service to provide methods to send mails
 * @author Rajeev Pandey
 *
 */
@Service
public class MailService {
	
	private static Logger logger = LoggerFactory.getLogger(MailService.class);
	
	@Autowired
	private MailBodyGenerator mailBodyGenerator;
	@Autowired
	private AmazonMailSender amazonMailSender;
	
	/**
	 * Sending mail using amazon mail service in asynch manner, so call to this method will
	 * return immediately.
	 * @param mailTo
	 * @param mailContent
	 * @param subject
	 */
	@Async
	public boolean sendMailUsingAws(String[] mailTo, String mailContent, String subject){
		if(mailTo == null || mailTo.length == 0){
			throw new ProAPIException("To address is empty");
		}
		if(subject == null || subject.isEmpty()){
			throw new ProAPIException("Subject is empty");
		}
		return amazonMailSender.sendMail(mailTo, mailContent, subject);
	}
	/**
	 * This method accepts to address as string, and expects to be comma separated if that string contains multiple
	 * email ids
	 * @param mailTo
	 * @param mailContent
	 * @param subject
	 * @return
	 */
	@Async
	public boolean sendMailUsingAws(String mailTo, String mailContent, String subject){
		if(mailTo != null && !mailTo.isEmpty()){
			String[] toList = mailTo.split(",");
			return sendMailUsingAws(toList, mailContent, subject);
		}
		else{
			throw new ProAPIException("To address is empty");
		}
	}
	/**
	 * @param mailTo
	 * @param mailContent
	 * @param subject
	 * @return
	 */
	@Async
	public Future<Boolean> sendMailUsingAwsWithAck(String[] mailTo, String mailContent, String subject){
		if(mailTo == null || mailTo.length == 0){
			throw new ProAPIException("To address is empty");
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
