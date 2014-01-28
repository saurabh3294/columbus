package com.proptiger.data.util;

/**
 * Define all constants in this class, either make a group of constants related
 * to particular entity or put that on global scope.
 * 
 * @author Rajeev Pandey
 * 
 */
public class Constants {

	//Global constants start
	public static final String LOGIN_INFO_OBJECT_NAME = "_const_user_object_";
	public static final Integer ADMIN_USER_ID = 57594;
	public static final String REQ_PARAMETER_FOR_USER_ID = "_user_id";
	public static final String JSESSIONID = "JSESSIONID";
	public static final String PHPSESSID_KEY = "PHPSESSID";
	public static final int DEFAULT_NO_OF_ROWS = 10;
	public static final String USERNAME = "username";
	//Global constants end
	
	public static  class AmenityName{
		public static final String AIRPORT = "airport";
		public static final String SCHOOL = "school";
		public static final String BANK = "bank";
		public static final String ATM = "atm";
		public static final String RESTAURANT = "restaurant";
		public static final String GAS_STATION = "gas_station";
	}
	
	public  static class SubscriptionType{
		public static final String FORUM = "forum";
		public static final String REVIEW = "review";
	}
	
	public  static class ForumUserComments{
		public static final boolean FalseReply = false;
		public static final boolean TrueReply = true;
	}
	
	/**
	 * Locality review related constants
	 * @author Rajeev Pandey
	 *
	 */
	public static class LocalityReview{
		public static final String COMMENT_TIME = "commentTime";
		public static final String REVIEW_LABEL = "reviewLabel";
		public static final String REVIEW = "review";
		public static final String REVIEWS = "reviews";
		public static final String TOTAL_REVIEWS = "totalReviews";
		public static final String TOTAL_USERS_BY_RATING = "totalUsersByRating";
	}
	/**
	 * Locality rating related constants
	 * @author Rajeev Pandey
	 *
	 */
	public static class LocalityRating{
		public static final String TOTAL_RATINGS = "totalRatings";
		public static final String AVERAGE_RATINGS = "averageRatings";
	}
	
	/**
	 * Put all cache related constants in this class
	 * @author Rajeev Pandey
	 *
	 */
	public static class Cache{
		public static final String SEPARATR = "";
		public static final String CACHE = "cache";
		public static final String LOCALITY_REVIEW = "loc-review";
		public static final String LOCALITY_REVIEW_CUSTOM_FIELDS = "loc-review-custom";
		public static final String LOCALITY_REVIEW_RATING = "loc-review-rating";
		
		public static final String LOCALITY_RATING = "loc-rating";
		public static final String LOCALITY_RATING_AVG_BY_CATEGORY = "loc-rating-avg-ctg";
		public static final String LOCALITY_RATING_USERS_COUNT_BY_RATING = "loc-rating-user-cnt";
		
	}
}
