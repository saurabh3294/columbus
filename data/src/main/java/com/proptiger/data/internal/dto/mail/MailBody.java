package com.proptiger.data.internal.dto.mail;

/**
 * @author Rajeev Pandey
 * 
 */
public class MailBody extends MediumDetails {

    private static final long serialVersionUID = 2215518147415411577L;
    
    private String subject;
    private String body;
    
    public String getSubject() {
        return subject;
    }

    public MailBody setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getBody() {
        return body;
    }

    public MailBody setBody(String body) {
        this.body = body;
        return this;
    }

}
