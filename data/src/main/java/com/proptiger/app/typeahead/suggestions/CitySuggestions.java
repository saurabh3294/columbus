package com.proptiger.app.typeahead.suggestions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

import com.proptiger.data.model.Typeahead;
import com.proptiger.data.util.UtilityClass;

@Component
public class CitySuggestions {
    
    private String templateId = "Typeahead-Suggestion-City";
    
    private String[][] suggestionTemplates = {
            { "Affordable apartments in %s", "affordable-flats", "affordable-flats"},
            { "Resale property in %s", "resale-property", "resale-property"},
            { "Luxury projects in %s", "luxury-projects", "luxury-projects"},
            { "Ready to move apartments in %s", "ready-to-move-flats", "ready-to-move-flats"},
            { "Under construction property in %s", "under-construction-property", "under-construction-property"}};

    public List<Typeahead> getSuggestions(int id, String name, String redirectUrl, int count) {
        List<Typeahead> suggestions = new ArrayList<Typeahead>();
        Typeahead obj;
        for (String[] template : suggestionTemplates) {
            obj = new Typeahead();
            obj.setDisplayText(String.format(template[0], name));
            obj.setRedirectUrl(name.toLowerCase() + "/" + template[1]);
            obj.setId(templateId + "-" + template[2]);
            suggestions.add(obj);
        }
        
        Collections.shuffle(suggestions);
        return UtilityClass.getFirstNElementsOfList(suggestions, 2);
    }

}
