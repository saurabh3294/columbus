package com.proptiger.columbus.thandlers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.proptiger.columbus.repo.TemplateInfoDao;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.util.HttpRequestUtil;

@Component
public abstract class RootTHandler {


    protected TemplateTypes   type;
    protected HttpRequestUtil httpRequestUtil;
    protected TemplateInfoDao templateInfoDao;

    public TemplateTypes getType() {
        return type;
    }

    public void setType(TemplateTypes type) {
        this.type = type;
    }

    public void setHttpRequestUtil(HttpRequestUtil httpRequestUtil) {
        this.httpRequestUtil = httpRequestUtil;
    }

    public void setTemplateInfoDao(TemplateInfoDao templateInfoDao) {
        this.templateInfoDao = templateInfoDao;
    }

    protected Typeahead getTypeaheadObjectByIdTextAndURL(
            String id,
            String displayText,
            String redirectUrl,
            String redirectUrlFilters) {
        Typeahead typeahead = new Typeahead();
        typeahead.setId("Typeahead-Template-" + id);
        typeahead.setDisplayText(displayText);
        typeahead.setRedirectUrl(redirectUrl);
        typeahead.setRedirectUrlFilters(redirectUrlFilters);
        typeahead.setType(typeahead.getId());
        typeahead.setSuggestion(true);
        return typeahead;
    }

    /* Abstract Methods */

    public abstract void initialize();

    public abstract List<Typeahead> getResults(String query, Typeahead template, String city, int cityId, int rows);

    public abstract Typeahead getTopResult(String query, Typeahead template, String city, int cityId);
}