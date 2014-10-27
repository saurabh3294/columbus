package com.proptiger.columbus.thandlers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.proptiger.columbus.model.Typeahead;

@Component
public abstract class RootTHandler {

    protected TemplateTypes type;

    public TemplateTypes getType() {
        return type;
    }

    public void setType(TemplateTypes type) {
        this.type = type;
    }

    protected Typeahead getTypeaheadObjectByIdTextAndURL(String id, String displayText, String redirectUrl) {
        Typeahead typeahead = new Typeahead();
        typeahead.setId("Typeahead-Template-" + id);
        typeahead.setDisplayText(displayText);
        typeahead.setRedirectUrl(redirectUrl);
        typeahead.setType(typeahead.getId());
        typeahead.setIsSuggestion(true);
        return typeahead;
    }

    /* Abstract Methods */

    public abstract List<Typeahead> getResults(String query, Typeahead typeahead, String city, int rows);

    public abstract Typeahead getTopResult(String query, Typeahead typeahead, String city);
}