package com.proptiger.app.typeahead.suggestions;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.proptiger.data.model.Typeahead;

@Component
public class CitySuggestions {
    
    private String[][] suggestionTemplates = {
            { "Affordable Flats in %s", "affordable-flats" },
            { "Resale property in %s", "resale-property" },
            { "Luxury projects in %s", "luxury-projects" },
            { "Ready to move flats in %s", "ready-to-move-flats" },
            { "Under construction property in %s", "under-construction-property" }};

    public List<Typeahead> getSuggestions(int id, String name, String redirectUrl, int count) {
        List<Typeahead> suggestions = new ArrayList<Typeahead>();
        Typeahead obj;
        for (String[] template : suggestionTemplates) {
            obj = new Typeahead();
            obj.setDisplayText(String.format(template[0], name));
            obj.setRedirectUrl(name.toLowerCase() + "/" + template[1]);
            suggestions.add(obj);
        }
        return suggestions;
    }

}
