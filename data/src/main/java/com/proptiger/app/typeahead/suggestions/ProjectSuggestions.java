package com.proptiger.app.typeahead.suggestions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.data.model.Property;
import com.proptiger.data.model.Typeahead;
import com.proptiger.data.repo.PropertyDao;
import com.proptiger.data.repo.TypeaheadDao;

@Component
public class ProjectSuggestions {

    @Autowired
    private TypeaheadDao typeaheadDao;

    @Autowired
    private PropertyDao  propertyDao;

    public List<Typeahead> getSuggestions(int id, String name, String redirectUrl, int count) {

        List<Typeahead> suggestions = new ArrayList<Typeahead>();

        List<Property> propertyList = propertyDao.getProperties(id);
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
            if (bedrooms > 0 && url != null && !url.isEmpty()) {
                bhkUrlMap.put(bedrooms, property.getURL());
            }
        }

        Typeahead obj;
        for (Entry<Integer, String> mapEntry : bhkUrlMap.entrySet()) {
            obj = new Typeahead();
            obj.setDisplayText(mapEntry.getKey() + " BHK in " + name);
            obj.setRedirectUrl(mapEntry.getValue());
            suggestions.add(obj);
        }

        return suggestions;
    }

}