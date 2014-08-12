package com.proptiger.app.typeahead.suggestions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.proptiger.data.model.Typeahead;
import com.proptiger.data.util.UtilityClass;

@Component
public class LocalitySuggestions {

    private String templateId = "Typeahead-Suggestion-Locality";
    
    private String[][] suggestionTemplates = {
            { "Affordable Flats in %s", "affordable-flats-in-%s", "affordable-flats" },
            { "Resale property in %s", "resale-property-in-%s", "resale-property" },
            { "Luxury projects in %s", "luxury-projects-in-%s", "luxury-projects" },
            { "Ready to move flats in %s", "ready-to-move-flats-in-%s", "ready-to-move-flats" },
            { "Under construction property in %s", "under-construction-property-in-%s", "under-construction-property" } };

    public List<Typeahead> getSuggestions(int id, String name, String redirectUrl, 
            String cityName, String localityName, int count) {
        List<Typeahead> suggestions = new ArrayList<Typeahead>();
        Typeahead obj;
        for (String[] template : suggestionTemplates) {
            obj = new Typeahead();
            obj.setDisplayText(String.format(template[0], name));
            obj.setRedirectUrl(cityName.toLowerCase() + "/"
                    + String.format(template[1], (localityName.replace(' ', '-') + "-" + id).toLowerCase()));
            obj.setId(templateId + "-" + template[2]);
            suggestions.add(obj);
        }
        
        Collections.shuffle(suggestions);
        return UtilityClass.getFirstNElementsOfList(suggestions, 2);
    }
}
