package com.proptiger.data.internal.dto.mail;

/**
 * @author Rajeev Pandey
 * 
 */
public class MailDetails extends MailBody {

    private static final long serialVersionUID = -1313419432726880887L;
    
    /*
     * this from mail address will have higher priority than from address being
     * used in AmazonMailSender class
     */
    private String   from;
    private String[] mailTo;
    private String[] mailCC;
    private String[] mailBCC;
    private String   replyTo;

    public MailDetails() {
    }

    public MailDetails(String subject, String body) {
        this.setSubject(subject);
        this.setBody(body);
    }

    public MailDetails(MailBody mailBody) {
        this.setSubject(mailBody.getSubject());
        this.setBody(mailBody.getBody());
    }

    public MailDetails(String[] mailCC, String[] mailBCC, String from) {
        this.mailCC = mailCC;
        this.mailBCC = mailBCC;
        this.from = from;
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

    public String getFrom() {
        return from;
    }

    public MailDetails setFrom(String from) {
        this.from = from;
        return this;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public MailDetails setReplyTo(String replyTo) {
        this.replyTo = replyTo;
        return this;
    }

    @Override
    public String toString() {
        String str = "";
        str += " from: " + from;
        str += " mailTo: " + mailTo;
        str += " mailCC: " + mailCC;
        str += " mailBCC: " + mailBCC;
        str += " replyTo: " + replyTo;
        return str;
    }

}
