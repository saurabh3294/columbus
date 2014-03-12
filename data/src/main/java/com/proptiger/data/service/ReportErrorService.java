package com.proptiger.data.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.opsworks.model.ResourceNotFoundException;
import com.google.gson.Gson;
import com.proptiger.data.internal.dto.mail.MailBody;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.ProjectDiscussion;
import com.proptiger.data.model.ProjectError;
import com.proptiger.data.model.Property;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.repo.ProjectErrorDao;
import com.proptiger.data.service.portfolio.ProjectDiscussionsService.ProjectDiscussionMailDTO;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.data.util.ResourceType;
import com.proptiger.exception.ResourceNotAvailableException;
import com.proptiger.mail.service.MailSender;
import com.proptiger.mail.service.MailTemplateDetail;
import com.proptiger.mail.service.TemplateToHtmlGenerator;

@Service
public class ReportErrorService {

    @Autowired
    private ProjectErrorDao         projectErrorDao;

    @Autowired
    private PropertyService         propertyService;

    @Autowired
    private PropertyReader          propertyReader;

    @Autowired
    private TemplateToHtmlGenerator mailBodyGenerator;

    @Autowired
    private MailSender              mailSender;

    /**
     * This method will save the error reported for a project or a property.
     * After saving in the database. It will send a mail.
     * 
     * @param projectError
     * @return
     */
    public ProjectError saveReportError(ProjectError projectError) {

        String condition = "";
        /*
         * Error message to be displayed when the project or property Id is not
         * found.
         */
        String errorMessage = "Project Id " + projectError.getProjectId();

        /*
         * Constructing the Json selector for retrieving the project or property
         * info based on given ids.
         */
        condition += ",\"projectId\":" + projectError.getProjectId();
        if (projectError.getPropertyId() != null && projectError.getPropertyId() > 0) {
            condition += ",\"propertyId\":" + projectError.getPropertyId();
            errorMessage = " with Property Id " + projectError.getPropertyId() + " ";
        }
        condition = "{" + condition.substring(1) + "}";
        String jsonSelector = "{\"paging\":{\"rows\":1},\"filters\":{\"and\":[{\"equal\":" + condition + "}]}}";

        Selector selector = new Gson().fromJson(jsonSelector, Selector.class);
        List<Property> properties = propertyService.getProperties(selector);

        if (properties == null || properties.isEmpty())
            throw new ResourceNotFoundException(errorMessage + " does not exists.");

        ProjectError saveprojError = projectErrorDao.save(projectError);
        if (saveprojError == null)
            throw new PersistenceException("The reported error could not be saved.");

        sendMailOnProjectError(saveprojError, properties.get(0));
        return saveprojError;

    }

    /**
     * This method will send the mail to recipients with details about the error
     * reported by user.
     * 
     * @param projectError
     * @param property
     * @return
     */
    private boolean sendMailOnProjectError(ProjectError projectError, Property property) {

        String[] mailTo = propertyReader.getRequiredProperty("mail.report.error.to.recipient").split(",");

        String[] mailCC = propertyReader.getRequiredProperty("mail.report.error.cc.recipient").split(",");

        MailBody mailBody = mailBodyGenerator.generateMailBody(
                MailTemplateDetail.PROJECT_PROPERTY_ERROR_POST,
                new ReportErrorDTO(projectError, property));

        return mailSender.sendMailUsingAws(mailTo, mailCC, null, mailBody.getBody(), mailBody.getSubject());

    }

    /**
     * The class is used for accessing the property and projectError information
     * used for constructing the mail template for sending the mail.
     * 
     * @author mukand
     */
    public static class ReportErrorDTO {
        public ReportErrorDTO(ProjectError projectError, Property property) {
            this.property = property;
            this.projectError = projectError;
        }

        public ProjectError projectError;
        public Property     property;

        public ProjectError getProjectError() {
            return projectError;
        }

        public void setProjectError(ProjectError projectError) {
            this.projectError = projectError;
        }

        public Property getProperty() {
            return property;
        }

        public void setProperty(Property property) {
            this.property = property;
        }
    }
}
