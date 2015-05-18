package com.proptiger.columbus.util;

public class TopsearchObjectField {

    private String id;

    private String typeaheadLabel;

    private String typeaheadRedirectUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeaheadLabel() {
        return typeaheadLabel;
    }

    public void setTypeaheadLabel(String typeaheadLabel) {
        this.typeaheadLabel = typeaheadLabel;
    }

    public String getRedirectUrl() {
        return typeaheadRedirectUrl;
    }

    public void setRedirectUrl(String typeaheadRedirectUrl) {
        this.typeaheadRedirectUrl = typeaheadRedirectUrl;
    }

    @Override
    public String toString() {
        return id + ":" + typeaheadLabel + ":" + typeaheadRedirectUrl;
    }

}
