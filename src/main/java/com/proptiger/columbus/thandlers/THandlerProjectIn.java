package com.proptiger.columbus.thandlers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.reflect.TypeToken;
import com.proptiger.columbus.model.Typeahead;
import com.proptiger.core.model.cms.Locality;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;

@Component
public class THandlerProjectIn extends RootTHandler {

	private String localityFilter = "locality=%s";

	@Override
	public List<Typeahead> getResults(String query, Typeahead typeahead,
			String city, int rows) {

		/* restrict results to top 2 localities for now */
		rows = Math.min(rows, 3);

		List<Typeahead> results = new ArrayList<Typeahead>();

		results.add(getTopResult(query, typeahead, city));

		List<Locality> topLocalities = getTopLocalities(city);
		String redirectURL, taLabel, taID;
		for (Locality locality : topLocalities) {
			redirectURL = getRedirectUrl(city);
			redirectURL = addLocalityFilterToRedirectURL(redirectURL,
					locality.getLabel());
			taID = this.getType().toString();
			taLabel = (this.getType().getText() + " " + locality.getLabel());
			results.add(getTypeaheadObjectByIdTextAndURL(taID, taLabel,
					redirectURL));
			if (results.size() == rows) {
				break;
			}
		}

		return results;
	}

	private String getRedirectUrl(String city) {
		String redirectUrl = "";
		TemplateTypes templateType = this.getType();
		switch (templateType) {
		case ProjectsIn:
			redirectUrl = String.format(
					URLGenerationConstants.GenericUrlProjectsIn,
					city.toLowerCase());
			break;
		case NewProjectsIn:
			redirectUrl = String.format(
					URLGenerationConstants.GenericUrlNewProjectsIn,
					city.toLowerCase()); /* TODO */
			break;
		case UpcomingProjectsIn:
			redirectUrl = String.format(
					URLGenerationConstants.GenericUrlUpcomingProjectsIn,
					city.toLowerCase());
			break;
		case PreLaunchProjectsIn:
			redirectUrl = String.format(
					URLGenerationConstants.GenericUrlPreLaunchProjectsIn,
					city.toLowerCase());
			break;
		case UnderConstProjectsIn:
			redirectUrl = String.format(
					URLGenerationConstants.GenericUrlUnderConstProjectsIn,
					city.toLowerCase());
			break;
		case ReadyToMoveProjectsIn:
			redirectUrl = String.format(
					URLGenerationConstants.GenericUrlReadyToMoveProjectsIn,
					city.toLowerCase());
			break;
		case AffordableProjectsIn:
			redirectUrl = String.format(
					URLGenerationConstants.GenericUrlAffordableProjectsIn,
					city.toLowerCase());
			break;
		case LuxuryProjectsIn:
			redirectUrl = String.format(
					URLGenerationConstants.GenericUrlLuxuryProjectsIn,
					city.toLowerCase());
			break;
		case TopProjectsIn:
			redirectUrl = String.format(
					URLGenerationConstants.GenericUrlProjectsIn,
					city.toLowerCase());
			break;
		default:
			break;
		}

		return redirectUrl;
	}

	public Typeahead getTopResult(String query, Typeahead typeahead, String city) {
		String displayText = (this.getType().getText() + " " + city);
		String redirectUrl = getRedirectUrl(city);
		return (getTypeaheadObjectByIdTextAndURL(this.getType().toString(),
				displayText, redirectUrl));
	}

	private List<Locality> getTopLocalities(String cityName) {
		URI uri = URI
				.create(UriComponentsBuilder
						.fromUriString(
								PropertyReader
										.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL)
										+ PropertyReader
												.getRequiredPropertyAsString(PropertyKeys.LOCALITY_API_URL)
										+ "?"
										+ URLGenerationConstants.Selector
										+ String.format(
												URLGenerationConstants.SelectorGetLocalityNamesByCityName,
												cityName)).build().encode()
						.toString());
		List<Locality> topLocalities = HttpRequestUtil
				.getInternalApiResultAsTypeList(uri,
						new TypeToken<ArrayList<Locality>>() {
						}.getType());
		return topLocalities;
	}

	private String addLocalityFilterToRedirectURL(String redirectUrl,
			String localityLabel) {
		if (StringUtils.contains(redirectUrl, "?")) {
			redirectUrl += ("&" + String.format(localityFilter, localityLabel));
		} else {
			redirectUrl += ("?" + String.format(localityFilter, localityLabel));
		}
		return redirectUrl;
	}
}
