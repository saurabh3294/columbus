package com.proptiger.data.service.portfolio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.internal.dto.UnmatchedProjectDetails;
import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.internal.dto.mail.MailBody;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.repo.ForumUserDao;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.mail.service.MailBodyGenerator;
import com.proptiger.mail.service.MailSender;
import com.proptiger.mail.service.MailTemplateDetail;

@Service
public class UnmatchedProjectRequestService {

	@Autowired
	private MailSender mailSender;
	
	@Autowired
	private MailBodyGenerator mailBodyGenerator;
	
	@Autowired
	private ForumUserDao forumUserDao;
	
	@Autowired
	private PropertyReader propertyReader;
	
	private static Logger logger = LoggerFactory.getLogger(UnmatchedProjectRequestService.class);
	/**
	 * @param unmatchedProjectDetails
	 * @param userInfo
	 * @return
	 */
	public boolean handleUnmatchedProjectRequest(
			UnmatchedProjectDetails unmatchedProjectDetails, UserInfo userInfo) {
		ForumUser forumUser = forumUserDao.findOne(userInfo.getUserIdentifier());
		unmatchedProjectDetails.setUserEmail(forumUser.getEmail());
		unmatchedProjectDetails.setContact(forumUser.getContact());
		unmatchedProjectDetails.setUserName(forumUser.getUsername());
		MailBody mailBody = mailBodyGenerator.generateHtmlBody(MailTemplateDetail.UNMATCHED_PROJECT_INTERNAL, unmatchedProjectDetails);
		String toAddress = propertyReader.getRequiredProperty("mail.unmatched-project.internal.reciepient");
		logger.debug("Unmatched project request mail to internal {}",toAddress);
		boolean userMailStatus =  mailSender.sendMailUsingAws(toAddress, mailBody.getBody(), mailBody.getSubject());
		toAddress = forumUser.getEmail();
		logger.debug("Unmatched project request mail to user {}",toAddress);
		mailBody = mailBodyGenerator.generateHtmlBody(MailTemplateDetail.UNMATCHED_PROJECT_USER, unmatchedProjectDetails);
		boolean internalMailStatus = mailSender.sendMailUsingAws(toAddress, mailBody.getBody(), mailBody.getSubject());
		
		return (userMailStatus && internalMailStatus);
	}
}
