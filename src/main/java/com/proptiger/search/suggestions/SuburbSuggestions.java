package com.proptiger.search.suggestions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.proptiger.core.util.UtilityClass;
import com.proptiger.search.model.Typeahead;

@Component
public class SuburbSuggestions {

	private String templateId = "Typeahead-Suggestion-Suburb";

	private String[][] suggestionTemplates = {
			{ "Affordable apartments in %s", "affordable-flats-in-%s",
					"affordable-flats" },
			{ "Resale property in %s", "resale-property-in-%s",
					"resale-property" },
			{ "Luxury projects in %s", "luxury-projects-in-%s",
					"luxury-projects" },
			{ "Ready to move apartments in %s", "ready-to-move-flats-in-%s",
					"ready-to-move-flats" },
			{ "Under construction property in %s",
					"under-construction-property-in-%s",
					"under-construction-property" } };

	public List<Typeahead> getSuggestions(int id, String name,
			String redirectUrl, String cityName, int count) {
		List<Typeahead> suggestions = new ArrayList<Typeahead>();
		Typeahead obj;
		for (String[] template : suggestionTemplates) {
			obj = new Typeahead();
			obj.setDisplayText(String.format(template[0], name));
			obj.setRedirectUrl(cityName.toLowerCase()
					+ "/"
					+ String.format(template[1],
							makeSuburbRedirectUrl(redirectUrl)));
			obj.setId(templateId + "-" + template[2]);
			obj.setType(obj.getId());
			obj.setIsSuggestion(true);
			suggestions.add(obj);
		}
		Collections.shuffle(suggestions);
		return UtilityClass.getFirstNElementsOfList(suggestions, 2);
	}

	/*
	 * Hardcoded URL generation here. extracting form format ::
	 * noida/property-sale-noida-expressway-10049
	 */
	/* TODO :: include suburb_id and suburb_name while solr indexing */
	private String makeSuburbRedirectUrl(String redirectUrl) {
		return (StringUtils.split(redirectUrl, '/')[1]
				.substring("property-sale-".length()));
	}
}
