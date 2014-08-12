package com.proptiger.data.enums.mail;

/**
 * This file contains the mail template files and subject file matching for
 * different types fo mails
 * 
 * @author Rajeev Pandey
 * 
 */
public enum MailTemplateDetail {
    ADD_NEW_PORTFOLIO_LISTING("ListingAddMail", "mailtemplate/add_new_portfolio_listing.vm",
            "mailtemplate/add_new_portfolio_listing_subject.vm"),

    LISTING_LOAN_REQUEST_USER("ListingLoanRequestMail", "mailtemplate/home-loan-Email-Template.vm",
            "mailtemplate/home-loan-Email-Template-subject.vm"),

    LISTING_LOAN_REQUEST_INTERNAL("ListingLoanRequestMail", "mailtemplate/home-loan-Email-internal.vm",
            "mailtemplate/home-loan-Email-internal-subject.vm"),

    INTERESTED_TO_SELL_PROPERTY_INTERNAL("ListingResaleMail", "mailtemplate/resale-listing-internal-Email.vm",
            "mailtemplate/resale-listing-internal-Email-subject.vm"),

    INTERESTED_TO_SELL_PROPERTY_USER("ListingResaleMail", "mailtemplate/interested-selling-property-Email-User.html",
            "mailtemplate/interested-selling-property-Email-User-subject.vm"),

    UNMATCHED_PROJECT_INTERNAL("UnmatchedProjectDetails", "mailtemplate/unmatched-property-internal-Email.vm",
            "mailtemplate/unmatched-property-internal-Email-subject.vm"), UNMATCHED_PROJECT_USER(
            "UnmatchedProjectDetails", "mailtemplate/unmatched-property-user-Email-Template.vm",
            "mailtemplate/unmatched-property-user-Email-Template-Subject.vm"),

    ADD_NEW_PROJECT_COMMENT("ProjectCommentAddMail", "mailtemplate/comment_post_email_template.vm",
            "mailtemplate/comment_post_email_template_subject.vm"),

    PROJECT_PROPERTY_ERROR_POST("projectPropertyErrorData", "mailtemplate/report_error_template.vm",
            "mailtemplate/report_error_template_subject.vm"),

    SELL_YOUR_PROPERTY("sellYourPropertyData", "mailtemplate/sell-your-property.vm",
            "mailtemplate/sell-your-property-subject.vm"),

    PROJECT_DETAILS_MAIL_TO_USER("projectDetailsMailToUser", "mailtemplate/project/project-details.vm",
            "mailtemplate/project/project-details-subject.vm"),

    RESET_PASSWORD("resetpassword", "mailtemplate/resetpassword/reset-password-mail.vm",
            "mailtemplate/resetpassword/reset-password-mail-subject.vm"),
    
    NEW_USER_REGISTRATION("newregistration", "mailtemplate/register/new-user-registration.vm",
            "mailtemplate/register/new-user-registration-subject.vm");

    private MailTemplateDetail(String key, String body, String subject) {
        this.key = key;
        this.bodyTemplate = body;
        this.subjectTemplate = subject;
    }

    private String bodyTemplate;
    private String key;
    private String subjectTemplate;

    public String getBodyTemplate() {
        return bodyTemplate;
    }

    public void setBodyTemplate(String templateFileName) {
        this.bodyTemplate = templateFileName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSubjectTemplate() {
        return subjectTemplate;
    }

    public void setSubjectTemplate(String subjectTemplate) {
        this.subjectTemplate = subjectTemplate;
    }

}
