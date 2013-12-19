package com.proptiger.mail.service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Rajeev Pandey
 *
 */
public enum MailType {
	LISTING_ADD_MAIL_TO_USER("portfolio_listing_add"),
	LISTING_HOME_LOAN_CONFIRM_TO_USER("portfolio_listing_loan"),
	LISTING_HOME_LOAN_CONFIRM_TO_INTERNAL("listing_loan_internal"),
	INTERESTED_TO_SELL_PROPERTY_INTERNAL("interested_to_sell_internal"),
	INTERESTED_TO_SELL_PROPERTY_USER("interested_to_sell_user");
	
	private static Map<String, MailType> mailTypeMap = new HashMap<>();
	
	static{
		mailTypeMap.put("portfolio_listing_add", LISTING_ADD_MAIL_TO_USER);
		mailTypeMap.put("portfolio_listing_loan", LISTING_HOME_LOAN_CONFIRM_TO_USER);
		mailTypeMap.put("listing_loan_internal", LISTING_HOME_LOAN_CONFIRM_TO_INTERNAL);
		mailTypeMap.put("interested_to_sell_internal", INTERESTED_TO_SELL_PROPERTY_INTERNAL);
	}
	private MailType(String str){
		this.type = str;
	}
	String type;
	
	@Override
	public String toString() {
		return this.type;
	}
	
	/**
	 * This method will return null if no type found for specified type string
	 * @param type
	 * @return
	 */
	public static MailType valueOfString(String type){
		return mailTypeMap.get(type);
	}
}
