package com.proptiger.columbus.suggestions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.proptiger.columbus.model.Typeahead;
import com.proptiger.core.util.UtilityClass;

@Component
public class CitySuggestions {

    private String     templateId          = "Typeahead-Suggestion-City";

    private String[][] suggestionTemplates = {
            { "Affordable apartments in %s", "affordable-flats", "affordable-flats" },
            { "Resale property in %s", "resale-property", "resale-property" },
            { "Luxury projects in %s", "luxury-projects", "luxury-projects" },
            { "Ready to move apartments in %s", "ready-to-move-flats", "ready-to-move-flats" },
            { "Under construction property in %s", "under-construction-property", "under-construction-property" } };

    public List<Typeahead> getSuggestions(int id, String name, String redirectUrl, int count) {
        List<Typeahead> suggestions = new ArrayList<Typeahead>();
        Typeahead obj;
        for (String[] template : suggestionTemplates) {
            obj = new Typeahead();
            obj.setDisplayText(String.format(template[0], name));
            obj.setRedirectUrl(name.toLowerCase() + "/" + template[1]);
            obj.setId(templateId + "-" + template[2]);
            obj.setType(obj.getId());
            obj.setIsSuggestion(true);
            suggestions.add(obj);
        }

        return filterByCustomRules(suggestions);
    }

    private List<Typeahead> filterByCustomRules(List<Typeahead> suggestions) {
        Collections.shuffle(suggestions);
        String temp = (suggestions.get(0).getDisplayText() + " " + suggestions.get(1).getDisplayText());
        if (StringUtils.containsIgnoreCase(temp, "Resale property") && StringUtils.containsIgnoreCase(
                temp,
                "Ready to move")) {
            suggestions.remove(0);
        }
        return UtilityClass.getFirstNElementsOfList(suggestions, 2);
    }

}
