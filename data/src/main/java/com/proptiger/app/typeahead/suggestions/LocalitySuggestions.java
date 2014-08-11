package com.proptiger.app.typeahead.suggestions;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import com.proptiger.data.model.Typeahead;

@Component
public class LocalitySuggestions {

    private String templateId = "Typeahead-Suggestion-Locality";
    
    private String[][] suggestionTemplates = {
            { "Affordable Flats in %s", "affordable-flats-in-%s" },
            { "Resale property in %s", "resale-property-in-%s" },
            { "Luxury projects in %s", "luxury-projects-in-%s" },
            { "Ready to move flats in %s", "ready-to-move-flats-in-%s" },
            { "Under construction property in %s", "under-construction-property-in-%s" } };

    public List<Typeahead> getSuggestions(int id, String name, String redirectUrl, 
            String cityName, String localityName, int count) {
        List<Typeahead> suggestions = new ArrayList<Typeahead>();
        Typeahead obj;
        for (String[] template : suggestionTemplates) {
            obj = new Typeahead();
            obj.setDisplayText(String.format(template[0], name));
            obj.setRedirectUrl(cityName.toLowerCase() + "/"
                    + String.format(template[1], (localityName.replace(' ', '-') + "-" + id).toLowerCase()));
            obj.setId(templateId);
            suggestions.add(obj);
        }
        return suggestions;
    }
}
