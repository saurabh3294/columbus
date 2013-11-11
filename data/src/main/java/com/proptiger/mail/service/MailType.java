package com.proptiger.mail.service;

/**
 * @author Rajeev Pandey
 *
 */
public enum MailType {
	PORTFOLIO_LISTING_ADD("portfolio-listing-add"),
	PORTFOLIO_LISTING_HOME_LOAN_REQUEST("portfolio-listing-loan");
	
	private MailType(String str){
		this.type = str;
	}
	String type;
	
}
