package com.proptiger.columbus.suggestions;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.columbus.repo.TypeaheadDao;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.UtilityClass;

@Component
public class ProjectSuggestions {

    @Autowired
    private HttpRequestUtil httpRequestUtil;

    @Autowired
    private TypeaheadDao    typeaheadDao;

    private static String   templateId           = "Typeahead-Suggestion-Project";

    private static String   entityIdFilterFormat = "{\"equal\":{\"propertyId\":%s}}\"";

    public List<Typeahead> getSuggestions(int id, Typeahead topResult, int count) {

        String projectName = topResult.getLabel();

        List<Typeahead> suggestionList = new ArrayList<Typeahead>();

        List<String> projectPropertyList = topResult.getProjectPropertyInfo();
        if (projectPropertyList == null) {
            return suggestionList;
        }

        Typeahead suggestion;
        for (String propertyInfo : projectPropertyList) {
            suggestion = getSuggestionObjectFromPropertyInfoString(propertyInfo, projectName);
            if (suggestion != null) {
                suggestionList.add(suggestion);
            }
        }

        return UtilityClass.getFirstNElementsOfList(suggestionList, count);
    }

    private Typeahead getSuggestionObjectFromPropertyInfoString(String propertyInfo, String projectName) {
        Typeahead typeahead = new Typeahead();
        String[] tokens = propertyInfo.split(";");
        int bhk = Integer.parseInt(tokens[0]);
        String url = tokens[1];
        String propertyId = tokens[2];
        typeahead.setDisplayText(bhk + " BHK in " + projectName);
        typeahead.setRedirectUrl(url);
        typeahead.setRedirectUrlFilters(String.format(entityIdFilterFormat, propertyId));
        typeahead.setId(templateId);
        typeahead.setType(typeahead.getId());
        typeahead.setSuggestion(true);
        return typeahead;
    }
}
