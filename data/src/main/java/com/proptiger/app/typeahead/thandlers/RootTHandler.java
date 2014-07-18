package com.proptiger.app.typeahead.thandlers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.proptiger.data.model.Typeahead;
import com.proptiger.data.service.LocalityService;

@Component
public abstract class RootTHandler {
    
    protected TemplateTypes type;
    
    protected LocalityService localityService;

    public TemplateTypes getType() {
        return type;
    }
    
    public void setType(TemplateTypes type) {
        this.type = type;
    }

    public void setLocalityService(LocalityService localityService) {
        this.localityService = localityService;
    }

    protected Typeahead getTypeaheadObjectByTextAndURL(String displayText, String redirectUrl) {
        Typeahead typeahead = new Typeahead();
        typeahead.setDisplayText(displayText);
        typeahead.setRedirectUrl(redirectUrl);
        return typeahead;
    }
    
    /* Abstract Methods */

    public abstract List<Typeahead> getResults(Typeahead typeahead, String city, int rows);
    
    public abstract Typeahead getTopResult(Typeahead typeahead, String city);

}
