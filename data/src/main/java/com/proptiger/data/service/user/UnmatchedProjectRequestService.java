package com.proptiger.data.service.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.enums.mail.MailTemplateDetail;
import com.proptiger.data.internal.dto.UnmatchedProjectDetails;
import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.internal.dto.mail.MailBody;
import com.proptiger.data.internal.dto.mail.MailDetails;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.repo.ForumUserDao;
import com.proptiger.data.service.mail.MailSender;
import com.proptiger.data.service.mail.TemplateToHtmlGenerator;
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;

@Service
public class UnmatchedProjectRequestService {

    @Autowired
    private MailSender              mailSender;

    @Autowired
    private TemplateToHtmlGenerator mailBodyGenerator;

    @Autowired
    private ForumUserDao            forumUserDao;

    @Autowired
    private PropertyReader          propertyReader;

    private static Logger           logger = LoggerFactory.getLogger(UnmatchedProjectRequestService.class);

    /**
     * @param unmatchedProjectDetails
     * @param userInfo
     * @return
     */
    public boolean handleUnmatchedProjectRequest(UnmatchedProjectDetails unmatchedProjectDetails, UserInfo userInfo) {
        ForumUser forumUser = forumUserDao.findOne(userInfo.getUserIdentifier());
        unmatchedProjectDetails.setUserEmail(forumUser.getEmail());
        unmatchedProjectDetails.setContact(forumUser.getContact());
        unmatchedProjectDetails.setUserName(forumUser.getUsername());
        MailBody mailBody = mailBodyGenerator.generateMailBody(
                MailTemplateDetail.UNMATCHED_PROJECT_INTERNAL,
                unmatchedProjectDetails);
        String toAddress = propertyReader.getRequiredProperty(PropertyKeys.MAIL_UNMATCHED_PROJECT_INTERNAL_RECIEPIENT);
        logger.debug("Unmatched project request mail to internal {}", toAddress);
        MailDetails mailDetails = new MailDetails(mailBody).setMailTo(toAddress);
        boolean userMailStatus = mailSender.sendMailUsingAws(mailDetails);
        toAddress = forumUser.getEmail();
        logger.debug("Unmatched project request mail to user {}", toAddress);
        mailBody = mailBodyGenerator.generateMailBody(
                MailTemplateDetail.UNMATCHED_PROJECT_USER,
                unmatchedProjectDetails);
        mailDetails = new MailDetails(mailBody).setMailTo(toAddress);
        boolean internalMailStatus = mailSender.sendMailUsingAws(mailDetails);

        return (userMailStatus && internalMailStatus);
    }
}
