package com.proptiger.columbus.suggestions;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.proptiger.core.model.Typeahead;

@Component
public class BuilderSuggestions {

    private String templateId          = "Typeahead-Suggestion-Builder";

    /* Assumptions */
    /* 1. projects-by-builder URL has the following format : /dlf-100002 */
    /*
     * 2. projects-by-builder-in-city URL has the following format :
     * /gurgaon/dlf-100002
     */

    /* TODO :: Discuss picking these from DB table seo-footer directly. */
    String[][]     suggestionTemplates = {
                                       // { "New launches by %s",
                                       // "new-launch-project-by-%s",
                                       // "new-launch-project" },
            { "Upcoming projects by %s", "upcoming-project-by-%s", "upcoming-project" },
            // { "New projects by %s", "new-project-by-%s", "new-project" },
            { "Completed properties by %s", "completed-property-by-%s", "completed-property" },
            { "Ongoing projects by %s", "ongoing-project-by-%s", "ongoing-project" } };

    public List<Typeahead> getSuggestions(int id, Typeahead topResult, int count) {
        
        String name = topResult.getLabel();
        String redirectUrl = topResult.getRedirectUrl();
        
        List<Typeahead> suggestions = new ArrayList<Typeahead>();
        Typeahead obj;
        for (String[] template : suggestionTemplates) {
            obj = new Typeahead();
            obj.setDisplayText(String.format(template[0], name));
            obj.setRedirectUrl(String.format(template[1], redirectUrl));
            obj.setId(templateId + "-" + template[2]);
            obj.setType(obj.getId());
            obj.setSuggestion(true);
            suggestions.add(obj);
        }
        return suggestions;
    }

    public List<Typeahead> getSuggestionsByCity(
            int id,
            String name,
            String redirectUrl,
            String cityName,
            String cityUrlPrefix,
            int count) {
        List<Typeahead> suggestions = new ArrayList<Typeahead>();
        Typeahead obj;
        for (String[] template : suggestionTemplates) {
            obj = new Typeahead();
            obj.setDisplayText(String.format(template[0], name) + " in" + cityName);
            obj.setRedirectUrl(cityName.toLowerCase() + "/" + String.format(template[1], redirectUrl));
            obj.setId(templateId);
            obj.setSuggestion(true);
            suggestions.add(obj);
        }
        return suggestions;
    }

}
