package com.proptiger.data.util;

/**
 * Define all constants in this class, either make a group of constants related
 * to particular entity or put that on global scope.
 * 
 * @author Rajeev Pandey
 * 
 */
public class Constants {

	// Single sign on
	public static final String LOGIN_INFO_OBJECT_NAME = "_const_user_object_";
	public static final Integer ADMIN_USER_ID = 57594;
	public static final String REQ_PARAMETER_FOR_USER_ID = "_user_id";
	public static final String JSESSIONID = "JSESSIONID";
	public static final String PHPSESSID_KEY = "PHPSESSID";
	public static final int DEFAULT_NO_OF_ROWS = 10;
	
	public class AmenityName{
		public static final String AIRPORT = "airport";
		public static final String SCHOOL = "school";
		public static final String BANK = "bank";
		public static final String ATM = "atm";
		public static final String RESTAURANT = "restaurant";
		public static final String GAS_STATION = "gas_station";
	}
	
	public class SubscriptionType{
		public static final String FORUM = "forum";
		public static final String REVIEW = "review";
	}
	
	public class ForumUserComments{
		public static final String FalseReply = "F";
		public static final String TrueReply = "T";
	}
}
