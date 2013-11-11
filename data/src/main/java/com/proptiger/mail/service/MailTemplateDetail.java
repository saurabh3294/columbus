package com.proptiger.mail.service;

/**
 * @author Rajeev Pandey
 *
 */
public enum MailTemplateDetail {
	ADD_NEW_PORTFOLIO_LISTING("ListingAddMail",
			"mailtemplate/add_new_portfolio_listing.vm"
			,"PropTiger | New Property Added"),
	
	PORTFOLIO_LISTING_LOAN_REQUEST("ListingLoanRequestMail",
			"mailtemplate/home-loan-Email-Template.vm"
			,"PropTiger | Home Loan Requirement Confirmation");
	
	private MailTemplateDetail(String key, String filename, String subject){
		this.key = key;
		this.templateFileName = filename;
		this.subject = subject;
	}
	
	private String templateFileName;
	private String key;
	private String subject;

	public String getTemplateFileName() {
		return templateFileName;
	}

	public void setTemplateFileName(String templateFileName) {
		this.templateFileName = templateFileName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
}
