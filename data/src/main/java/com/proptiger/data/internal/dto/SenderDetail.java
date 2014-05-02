package com.proptiger.data.internal.dto;

public class SenderDetail {
	private String mailTo;
	private String mailCC;

	private String senderName;
	private String senderEmail;
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSenderEmail() {
		return senderEmail;
	}

	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}

	public String getMailTo() {
		return mailTo;
	}

	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}

	public String getMailCC() {
		return mailCC;
	}

	public void setMailCC(String mailCC) {
		this.mailCC = mailCC;
	}

}