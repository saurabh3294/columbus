package com.proptiger.data.service.portfolio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.internal.dto.UnmatchedProjectDetails;
import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.internal.dto.mail.MailBody;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.mail.service.MailBodyGenerator;
import com.proptiger.mail.service.MailService;
import com.proptiger.mail.service.MailTemplateDetail;

@Service
public class UnmatchedProjectRequestService {

	@Autowired
	private MailService mailService;
	
	@Autowired
	private MailBodyGenerator mailBodyGenerator;
	
	@Autowired
	private PropertyReader propertyReader;
	
	/**
	 * @param unmatchedProjectDetails
	 * @param userInfo
	 * @return
	 */
	public boolean handleUnmatchedProjectRequest(
			UnmatchedProjectDetails unmatchedProjectDetails, UserInfo userInfo) {
		unmatchedProjectDetails.setUserEmail(userInfo.getEmail());
		unmatchedProjectDetails.setContact(userInfo.getContact());
		unmatchedProjectDetails.setUserName(userInfo.getName());
		MailBody mailBody = mailBodyGenerator.generateHtmlBody(MailTemplateDetail.UNMATCHED_PROJECT_ADDED, unmatchedProjectDetails);
		String toAddress = propertyReader.getRequiredProperty("mail.unmatched-project.internal.reciepient");
		return mailService.sendMailUsingAws(toAddress, mailBody.getBody(), mailBody.getSubject());
	}
}
