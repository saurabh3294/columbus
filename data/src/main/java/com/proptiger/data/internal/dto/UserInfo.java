package com.proptiger.data.internal.dto;


/**
 * @author Rajeev Pandey
 *
 */
public class UserInfo {

	private String name;
	private String email;
	private Integer userIdentifier;
	private String sessionId;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Integer getUserIdentifier() {
		return userIdentifier;
	}
	public void setUserIdentifier(Integer userId) {
		this.userIdentifier = userId;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	
}
