package com.proptiger.app.typeahead.suggestions;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import com.proptiger.data.model.Typeahead;

@Component
public class LandmarkSuggestions {

    private String[][] suggestionTemplates = {{ "Properties near %s", "/maps/%s/filters?geo=%s,%s" }};

    public List<Typeahead> getSuggestions(int id, Typeahead result, int count) {
        List<Typeahead> suggestions = new ArrayList<Typeahead>();
        Typeahead obj;
        String cityName = result.getCity();
        String label = result.getLabel();
        Double latitude = result.getLatitude();
        Double longitude = result.getLongitude();

        for (String[] template : suggestionTemplates) {
            obj = new Typeahead();
            obj.setDisplayText(String.format(template[0], label));
            obj.setRedirectUrl(String.format(template[1], "", latitude, longitude));
            suggestions.add(obj);
        }
        return suggestions;
    }
}
