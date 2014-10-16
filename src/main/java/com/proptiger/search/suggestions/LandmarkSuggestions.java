package com.proptiger.search.suggestions;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.proptiger.search.model.Typeahead;


@Component
public class LandmarkSuggestions {

	private String templateId = "Typeahead-Suggestion-Landmark";

	private String dummyLocalityUrl = "/gurgaon/property-sale-sector-110a-51970";

	private double defaultMapRadius = 5; // in km

	private String[][] suggestionTemplates = { { "Properties near %s",
			"/maps/" + dummyLocalityUrl + "/filters?geo=%s,%s,%s" } };

	public List<Typeahead> getSuggestions(int id, Typeahead result, int count) {
		List<Typeahead> suggestions = new ArrayList<Typeahead>();
		Typeahead obj;
		String label = result.getLabel();
		Double latitude = result.getLatitude();
		Double longitude = result.getLongitude();

		for (String[] template : suggestionTemplates) {
			obj = new Typeahead();
			obj.setDisplayText(String.format(template[0], label));
			obj.setRedirectUrl(String.format(template[1], defaultMapRadius,
					latitude, longitude));
			obj.setId(templateId);
			obj.setType(obj.getId());
			obj.setIsSuggestion(true);
			suggestions.add(obj);
		}
		return suggestions;
	}
}
