package com.proptiger.columbus.thandlers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.columbus.repo.TemplateInfoDao;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.util.HttpRequestUtil;

@Component
public abstract class RootTHandler {

    @Autowired
    protected HttpRequestUtil httpRequestUtil;

    @Autowired
    protected TemplateInfoDao templateInfoDao;

    @Autowired
    private TemplateMap       templateMap;

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

    protected TemplateTypes getTemplateType(Typeahead template) {
        return templateMap.get(template.getTemplateText().toLowerCase().trim());
    }

    protected TemplateTypes getTemplateType(String templateText) {
        return templateMap.get(templateText.toLowerCase().trim());
    }

    /* Abstract Methods */

    public abstract void initialize();

    public abstract List<Typeahead> getResults(String query, Typeahead template, String city, int cityId, int rows);

    public abstract Typeahead getTopResult(String query, Typeahead template, String city, int cityId);
}