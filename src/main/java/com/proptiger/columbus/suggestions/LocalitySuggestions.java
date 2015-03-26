package com.proptiger.columbus.suggestions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.proptiger.columbus.model.TypeaheadConstants;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.util.UtilityClass;

@Component
public class LocalitySuggestions {

    private String     templateId          = "Typeahead-Suggestion-Locality";

    private String[][] suggestionTemplatesPrimary = {
            { "Affordable apartments in %s", "affordable-flats-in-%s", "affordable-flats" },
            { "Luxury projects in %s", "luxury-projects-in-%s", "luxury-projects" },
            { "Ready to move apartments in %s", "ready-to-move-flats-in-%s", "ready-to-move-flats" },
            { "Under construction property in %s", "under-construction-property-in-%s", "under-construction-property" } };

    private String[][] suggestionTemplatesResale = {
            { "Resale property in %s", "resale-property-in-%s", "resale-property" } };

    public List<Typeahead> getSuggestions(int id, Typeahead topResult, int count) {
        
        String name = topResult.getLabel();
        String cityName = topResult.getCity();
        String localityName = topResult.getLocality();

        String[][] suggestionTemplates; 
        Float localitySoldPerc = topResult.getLocalitySoldPercentage();
        if(localitySoldPerc != null && localitySoldPerc.intValue() > TypeaheadConstants.localityResaleThreshold){
            suggestionTemplates = suggestionTemplatesResale;
        }
        else{
            suggestionTemplates = suggestionTemplatesPrimary;
        }

        List<Typeahead> suggestions = new ArrayList<Typeahead>();
        Typeahead obj;
        for (String[] template : suggestionTemplates) {
            obj = new Typeahead();
            obj.setDisplayText(String.format(template[0], name));
            obj.setRedirectUrl(cityName.toLowerCase() + "/"
                    + String.format(template[1], (localityName.replace(' ', '-') + "-" + id).toLowerCase()));
            obj.setId(templateId + "-" + template[2]);
            obj.setType(obj.getId());
            obj.setSuggestion(true);
            suggestions.add(obj);
        }

        return filterByCustomRules(suggestions, topResult);
    }

    private List<Typeahead> filterByCustomRules(List<Typeahead> suggestions, Typeahead topResult) {
        
        Collections.shuffle(suggestions);
        return UtilityClass.getFirstNElementsOfList(suggestions, 2);
    }

}
