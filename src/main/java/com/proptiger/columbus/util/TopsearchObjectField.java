package com.proptiger.columbus.util;

import com.fasterxml.jackson.annotation.JsonProperty;


public class TopsearchObjectField {
	@JsonProperty("ID")
	private String id;
    
	@JsonProperty("TYPEAHEAD_LABEL")
	private String typeahead_label;
    
	@JsonProperty("TYPEAHEAD_REDIRECT_URL")
	private String typeahead_redirect_url;
    
	@JsonProperty("ID")
    public String getId() {
        return id;
    }
	
	@JsonProperty("ID")
    public void setId(String id) {
        this.id = id;
    }
    
	@JsonProperty("TYPEAHEAD_LABEL")
    public String getTypeaheadLabel() {
        return typeahead_label;
    }
	
	@JsonProperty("TYPEAHEAD_LABEL")
    public void setTypeaheadLabel(String typeahead_label) {
        this.typeahead_label = typeahead_label;
    }
    
	@JsonProperty("TYPEAHEAD_REDIRECT_URL")
    public String getRedirectUrl() {
        return typeahead_redirect_url;
    }
	
	@JsonProperty("TYPEAHEAD_REDIRECT_URL")
    public void setRedirectUrl(String typeahead_redirect_url) {
        this.typeahead_redirect_url = typeahead_redirect_url;
    }
    
    
    /*@Override
    public String toString() {
		return "Field [ID=" + getId() + ", TYPEAHEAD_LABEL=" + getTypeaheadLabel() + ", TYPEAHEAD_REDIRECT_URL="
                + getRedirectUrl() + "]";
    }*/
	@Override
    public String toString() {
		return id+":"+typeahead_label+":"+typeahead_redirect_url;
    }

}
