package com.proptiger.data.internal.dto;

import java.io.Serializable;


/**
 * @author Rajeev Pandey
 *
 */
public class UserInfo implements Serializable{

	private static final long serialVersionUID = -3022788419586557079L;
	private String name;
	private String email;
	private Integer userIdentifier;
	private String sessionId;
	private long contact;
	
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
	public long getContact() {
		return contact;
	}
	public void setContact(long contact) {
		this.contact = contact;
	}
	
	
}
