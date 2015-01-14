package com.proptiger.data.service;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.proptiger.core.enums.MailTemplateDetail;
import com.proptiger.core.internal.dto.mail.MailBody;
import com.proptiger.core.internal.dto.mail.MailDetails;
import com.proptiger.core.model.cms.Project;
import com.proptiger.core.model.cms.Property;
import com.proptiger.core.service.mail.MailSender;
import com.proptiger.core.service.mail.TemplateToHtmlGenerator;
import com.proptiger.core.util.PropertyReader;
import com.proptiger.data.model.ProjectError;
import com.proptiger.data.repo.ProjectErrorDao;

@Service
public class ErrorReportingService {

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

    @Autowired
    private ProjectService          projectService;

    private static Logger           logger = LoggerFactory.getLogger(ErrorReportingService.class);

    /**
     * This method will save the error reported for a project or a property.
     * After saving in the database. It will send a mail.
     * 
     * @param projectError
     * @return
     */
    public ProjectError saveReportError(ProjectError projectError) {

        Property property = null;
        Project project = null;

        /*
         * Constructing the Json selector for retrieving the project or property
         * info based on given ids.
         */
        if (projectError.getPropertyId() != null && projectError.getPropertyId() > 0) {
            property = propertyService.getProperty(projectError.getPropertyId());
            project = property.getProject();
            projectError.setProjectId(project.getProjectId());
        }
        else if (projectError.getProjectId() != null && projectError.getProjectId() > 0) {
            project = projectService.getProjectData(projectError.getProjectId());
        }

        ProjectError saveprojError = projectErrorDao.save(projectError);
        if (saveprojError == null) {
            throw new PersistenceException("The reported error could not be saved.");
        }

        sendMailOnProjectError(saveprojError, property, project);
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
    private boolean sendMailOnProjectError(ProjectError projectError, Property property, Project project) {
        String mailToAddress = propertyReader.getRequiredProperty("mail.report.error.to.recipient");
        String mailCCAddress = propertyReader.getRequiredProperty("mail.report.error.cc.recipient");
        if (mailToAddress.length() < 1) {
            logger.error("Project/Property Error Reporting is not able to send mail as 'to' mail recipients is empty. The application properties property (mail.report.error.to.recipient) is empty.");
            return false;
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        MailBody mailBody = mailBodyGenerator.generateMailBody(
                MailTemplateDetail.PROJECT_PROPERTY_ERROR_POST,
                new ReportErrorDTO(projectError, property, project, request.getServerName()));
        MailDetails mailDetails = new MailDetails(mailBody).setMailTo(mailToAddress).setMailCC(mailCCAddress);
        return mailSender.sendMailUsingAws(mailDetails);
    }

    /**
     * The class is used for accessing the property and projectError information
     * used for constructing the mail template for sending the mail.
     * 
     * @author mukand
     */
    public static class ReportErrorDTO {
        public ReportErrorDTO(ProjectError projectError, Property property, Project project, String serverName) {
            this.property = property;
            this.projectError = projectError;
            this.project = project;
            this.serverName = serverName;
        }

        public ProjectError projectError;
        public Property     property;
        public Project      project;
        public String       serverName;

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

        public Project getProject() {
            return project;
        }

        public void setProject(Project project) {
            this.project = project;
        }

        public String getServerName() {
            return serverName;
        }

        public void setServerName(String serverName) {
            this.serverName = serverName;
        }
    }
}
