package com.proptiger.search.suggestions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.core.enums.UnitType;
import com.proptiger.core.model.cms.Property;
import com.proptiger.search.model.Typeahead;
import com.proptiger.search.repo.TypeaheadDao;

@Component
public class ProjectSuggestions {

    @Autowired
    private TypeaheadDao typeaheadDao;

    private String templateId = "Typeahead-Suggestion-Project";

    public List<Typeahead> getSuggestions(int id, String name, String redirectUrl, int count) {

        List<Typeahead> suggestions = new ArrayList<Typeahead>();

        //List<Property> propertyList = propertyDao.getProperties(id);
        List<Property> propertyList = null;
        if (propertyList == null || propertyList.isEmpty()) {
            return suggestions;
        }
        
        /* TODO:: Need to get builder name here somehow and include it in display text. */
        
        HashMap<Integer, String> bhkUrlMap = new HashMap<Integer, String>();
        int bedrooms;
        String url;
        for (Property property : propertyList) {
            bedrooms = property.getBedrooms();
            url = property.getURL();
            if (bedrooms > 0 && property.getUnitType().equals(UnitType.Apartment.toString())  && url != null && !url.isEmpty()) {
                bhkUrlMap.put(bedrooms, property.getURL());
            }
        }

        Typeahead obj;
        for (Entry<Integer, String> mapEntry : bhkUrlMap.entrySet()) {
            obj = new Typeahead();
            obj.setDisplayText(mapEntry.getKey() + " BHK in " + name);
            obj.setRedirectUrl(mapEntry.getValue());
            obj.setId(templateId);
            obj.setType(obj.getId());
            obj.setIsSuggestion(true);
            suggestions.add(obj);
        }

        return suggestions;
    }

}