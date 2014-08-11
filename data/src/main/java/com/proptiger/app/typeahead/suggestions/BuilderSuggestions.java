package com.proptiger.app.typeahead.suggestions;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.proptiger.data.model.Typeahead;

@Component
public class BuilderSuggestions {

    private String templateId = "Typeahead-Suggestion-Builder";

    /* Assumptions */
    /* 1. projects-by-builder URL has the following format : /dlf-100002 */
    /* 2. projects-by-builder-in-city URL has the following format : /gurgaon/dlf-100002 */
    
    /* TODO :: Discuss picking these from DB table seo-footer directly. */
    String[][] suggestionTemplates = {
            { "New launches by %s", "new-launch-project-by-%s" },
            { "Upcoming projects by %s", "upcoming-project-by-%s" },
            { "New projects by %s", "new-project-by-%s" },
            { "Completed properties by %s", "completed-property-by-%s" },
            { "Ongoing projects by %s", "ongoing-project-by-%s" } };

    public List<Typeahead> getSuggestions(int id, String name, String redirectUrl, int count) {
        List<Typeahead> suggestions = new ArrayList<Typeahead>();
        Typeahead obj;
        for (String[] template : suggestionTemplates) {
            obj = new Typeahead();
            obj.setDisplayText(String.format(template[0], name));
            obj.setRedirectUrl(String.format(template[1], redirectUrl));
            obj.setId(templateId);
            suggestions.add(obj);
        }
        return suggestions;
    }
    
    public List<Typeahead> getSuggestionsByCity(int id, String name, String redirectUrl, String cityName, String cityUrlPrefix, int count) {
        List<Typeahead> suggestions = new ArrayList<Typeahead>();
        Typeahead obj;
        for (String[] template : suggestionTemplates) {
            obj = new Typeahead();
            obj.setDisplayText(String.format(template[0], name) + " in" + cityName);
            obj.setRedirectUrl(cityName.toLowerCase() + "/" + String.format(template[1], redirectUrl));
            suggestions.add(obj);
            obj.setId(templateId);
        }
        return suggestions;
    }
    
}
