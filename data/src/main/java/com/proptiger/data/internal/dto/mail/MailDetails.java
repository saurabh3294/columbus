package com.proptiger.data.internal.dto.mail;

/**
 * @author Rajeev Pandey
 * 
 */
public class MailDetails extends MailBody {

	private String[] mailTo;
	private String[] mailCC;
	private String[] mailBCC;
	
	public MailDetails() {
	}

	public MailDetails(MailBody mailBody) {
		this.setSubject(mailBody.getSubject());
		this.setBody(mailBody.getBody());
	}

	public String[] getMailTo() {
		return mailTo;
	}

	public MailDetails setMailTo(String mailTo) {
		if (mailTo != null && !mailTo.isEmpty()) {
			this.mailTo = mailTo.split(",");
		}
		return this;
	}

	public MailDetails setMailTo(String[] mailTo) {
		this.mailTo = mailTo;
		return this;
	}

	public String[] getMailCC() {
		return mailCC;
	}

	public MailDetails setMailCC(String mailCC) {
		if (mailCC != null && !mailCC.isEmpty()) {
			this.mailCC = mailCC.split(",");
		}
		return this;
	}

	public MailDetails setMailCC(String[] mailCC) {
		this.mailCC = mailCC;
		return this;
	}

	public String[] getMailBCC() {
		return mailBCC;
	}

	public MailDetails setMailBCC(String mailBCC) {
		if (mailBCC != null && !mailBCC.isEmpty()) {
			this.mailBCC = mailBCC.split(",");
		}
		return this;
	}

	public MailDetails setMailBCC(String[] mailBCC) {
		this.mailBCC = mailBCC;
		return this;
	}
	
}
