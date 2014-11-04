package com.proptiger.data.notification.model.external;

public class DefaultTemplate extends Template {

    private String template;

    public DefaultTemplate() {

    }

    public DefaultTemplate(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

}
