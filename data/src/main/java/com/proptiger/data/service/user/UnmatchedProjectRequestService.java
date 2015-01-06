package com.proptiger.data.service.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.model.user.User;
import com.proptiger.core.model.user.UserContactNumber;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;
import com.proptiger.data.enums.mail.MailTemplateDetail;
import com.proptiger.data.internal.dto.UnmatchedProjectDetails;
import com.proptiger.data.internal.dto.mail.MailBody;
import com.proptiger.data.internal.dto.mail.MailDetails;
import com.proptiger.data.service.mail.MailSender;
import com.proptiger.data.service.mail.TemplateToHtmlGenerator;

@Service
public class UnmatchedProjectRequestService {

    @Autowired
    private MailSender              mailSender;

    @Autowired
    private TemplateToHtmlGenerator mailBodyGenerator;

    @Autowired
    private PropertyReader          propertyReader;
    

    @Autowired
    private UserService userService;
    
    private static Logger           logger = LoggerFactory.getLogger(UnmatchedProjectRequestService.class);

    /**
     * @param unmatchedProjectDetails
     * @param userInfo
     * @return
     */
    public boolean handleUnmatchedProjectRequest(UnmatchedProjectDetails unmatchedProjectDetails, ActiveUser userInfo) {
        User user = userService.getUserById(userInfo.getUserIdentifier());
        UserContactNumber priorityContact = userService.getTopPriorityContact(user.getId());
        unmatchedProjectDetails.setUserEmail(user.getEmail());
        unmatchedProjectDetails.setContact(Long.valueOf(priorityContact != null? priorityContact.getContactNumber(): "0"));
        unmatchedProjectDetails.setUserName(user.getFullName());
        MailBody mailBody = mailBodyGenerator.generateMailBody(
                MailTemplateDetail.UNMATCHED_PROJECT_INTERNAL,
                unmatchedProjectDetails);
        String toAddress = propertyReader.getRequiredProperty(PropertyKeys.MAIL_UNMATCHED_PROJECT_INTERNAL_RECIEPIENT);
        logger.debug("Unmatched project request mail to internal {}", toAddress);
        MailDetails mailDetails = new MailDetails(mailBody).setMailTo(toAddress);
        boolean userMailStatus = mailSender.sendMailUsingAws(mailDetails);
        toAddress = user.getEmail();
        logger.debug("Unmatched project request mail to user {}", toAddress);
        mailBody = mailBodyGenerator.generateMailBody(
                MailTemplateDetail.UNMATCHED_PROJECT_USER,
                unmatchedProjectDetails);
        mailDetails = new MailDetails(mailBody).setMailTo(toAddress);
        boolean internalMailStatus = mailSender.sendMailUsingAws(mailDetails);

        return (userMailStatus && internalMailStatus);
    }
}
