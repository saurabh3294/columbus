package com.proptiger.columbus.suggestions;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.columbus.repo.TypeaheadDao;
import com.proptiger.columbus.util.PropertyKeys;
import com.proptiger.core.enums.UnitType;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.model.cms.Property;
import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.core.util.CorePropertyKeys;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.PropertyReader;

@Component
public class ProjectSuggestions {
    @Autowired
    private HttpRequestUtil httpRequestUtil;
    @Autowired
    private TypeaheadDao    typeaheadDao;

    private String          templateId = "Typeahead-Suggestion-Project";

    public List<Typeahead> getSuggestions(int id, Typeahead topResult, int count) {

        String name = topResult.getLabel();

        List<Typeahead> suggestions = new ArrayList<Typeahead>();

        List<Property> propertyList = httpRequestUtil.getInternalApiResultAsTypeListFromCache(
                getURIForPropertyAPI(id),
                PropertyReader.getRequiredPropertyAsInt(PropertyKeys.INTERNAL_API_SLA_MS),
                Property.class);
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
            obj.setSuggestion(true);
            suggestions.add(obj);
        }

        return suggestions;
    }

    private URI getURIForPropertyAPI(int projectId) {
        FIQLSelector selector = new FIQLSelector();
        selector.addAndConditionToFilter("projectId==" + projectId);
        selector.addField("bedrooms").addField("unitType").addField("URL");
        String stringUrl = PropertyReader.getRequiredPropertyAsString(CorePropertyKeys.PROPTIGER_URL) + PropertyReader
                .getRequiredPropertyAsString(CorePropertyKeys.PROPERTY_API_URL) + "?" + selector.getStringFIQL();
        return URI.create(stringUrl);
    }
}
