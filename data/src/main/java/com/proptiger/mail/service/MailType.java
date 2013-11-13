package com.proptiger.mail.service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Rajeev Pandey
 *
 */
public enum MailType {
	PORTFOLIO_LISTING_ADD("portfolio_listing_add"),
	PORTFOLIO_LISTING_HOME_LOAN_REQUEST("portfolio_listing_loan"),
	INTERESTED_TO_SELL_PROPERTY_INTERNAL("interested_to_sell_internal");
	
	private static Map<String, MailType> mailTypeMap = new HashMap<>();
	
	static{
		mailTypeMap.put("portfolio_listing_add", PORTFOLIO_LISTING_ADD);
		mailTypeMap.put("portfolio_listing_loan", PORTFOLIO_LISTING_HOME_LOAN_REQUEST);
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
