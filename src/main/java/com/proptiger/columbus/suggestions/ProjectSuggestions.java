package com.proptiger.columbus.suggestions;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.reflect.TypeToken;
import com.proptiger.columbus.model.Typeahead;
import com.proptiger.columbus.repo.TypeaheadDao;
import com.proptiger.core.enums.UnitType;
import com.proptiger.core.model.cms.Property;
import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;

@Component
public class ProjectSuggestions {
    private HttpRequestUtil httpRequestUtil;
    @Autowired
    private TypeaheadDao    typeaheadDao;

    private String          templateId = "Typeahead-Suggestion-Project";

    public List<Typeahead> getSuggestions(int id, String name, String redirectUrl, int count) {

        List<Typeahead> suggestions = new ArrayList<Typeahead>();

        List<Property> propertyList = httpRequestUtil.getInternalApiResultAsTypeListFromCache(
                getURIForPropertyAPI(id),
                new TypeToken<ArrayList<Property>>() {}.getType());
        if (propertyList == null || propertyList.isEmpty()) {
            return suggestions;
        }

        /*
         * TODO:: Need to get builder name here somehow and include it in
         * display text.
         */

        HashMap<Integer, String> bhkUrlMap = new HashMap<Integer, String>();
        int bedrooms;
        String url;
        for (Property property : propertyList) {
            bedrooms = property.getBedrooms();
            url = property.getURL();
            if (bedrooms > 0 && property.getUnitType().equals(UnitType.Apartment.toString())
                    && url != null
                    && !url.isEmpty()) {
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

    private URI getURIForPropertyAPI(int projectId) {
        FIQLSelector selector = new FIQLSelector();
        selector.addAndConditionToFilter("projectId==" + projectId);
        String stringUrl = PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL) + PropertyReader
                .getRequiredPropertyAsString(PropertyKeys.PROPERTY_API_URL) + "?" + selector.getFIQLForUrl();
        return URI.create(stringUrl);
    }
}