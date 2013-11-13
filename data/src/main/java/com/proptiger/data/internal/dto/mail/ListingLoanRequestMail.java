package com.proptiger.data.internal.dto.mail;

/**
 * @author Rajeev Pandey
 *
 */
public class ListingLoanRequestMail {

	private String userName;
	private String projectCity;
	private String projectName;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getProjectCity() {
		return projectCity;
	}
	public void setProjectCity(String projectCity) {
		this.projectCity = projectCity;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	
}
