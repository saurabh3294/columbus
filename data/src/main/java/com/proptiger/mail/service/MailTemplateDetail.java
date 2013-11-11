package com.proptiger.mail.service;

/**
 * @author Rajeev Pandey
 *
 */
public enum MailTemplateDetail {
	ADD_NEW_PORTFOLIO_LISTING("ListingAddMail",
			"mailtemplate/add_new_portfolio_listing.vm"
			,"mailtemplate/add_new_portfolio_listing_subject.vm"),
	
	PORTFOLIO_LISTING_LOAN_REQUEST("ListingLoanRequestMail",
			"mailtemplate/home-loan-Email-Template.vm"
			,"mailtemplate/home-loan-Email-Template-subject.vm"),
	
	RESALE_LISTING_INTERNAL("ListingResaleMail",
			"mailtemplate/resale-listing-internal-Email.vm"
			,"mailtemplate/resale-listing-internal-Email-subject.vm");
	
	private MailTemplateDetail(String key, String filename, String subject){
		this.key = key;
		this.bodyTemplate = filename;
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
