package com.proptiger.columbus.thandlers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import com.proptiger.core.model.Typeahead;
import com.proptiger.core.model.cms.Locality;
import com.proptiger.core.util.CorePropertyKeys;
import com.proptiger.core.util.PropertyReader;

public class THandlerProjectIn extends RootTHandler {

    private static String localityFilter = "locality=%s";

    private static Logger logger         = LoggerFactory.getLogger(THandlerProjectIn.class);

    @Override
    public List<Typeahead> getResults(String query, Typeahead typeahead, String city, int rows) {

        /* restrict results to top 2 localities for now */
        rows = Math.min(rows, 3);

        List<Typeahead> results = new ArrayList<Typeahead>();

        results.add(getTopResult(query, typeahead, city));

        List<Locality> topLocalities = getTopLocalities(city);

        if (topLocalities == null) {
            logger.error("Could not fetch top localities for city " + city);
            return results;
        }

        String redirectURL, taLabel, taID;
        for (Locality locality : topLocalities) {
            redirectURL = getRedirectUrl(city);
            redirectURL = addLocalityFilterToRedirectURL(redirectURL, locality.getLabel());
            taID = this.getType().toString();
            taLabel = (this.getType().getText() + " " + locality.getLabel());
            results.add(getTypeaheadObjectByIdTextAndURL(taID, taLabel, redirectURL));
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
                redirectUrl = String.format(URLGenerationConstants.GenericUrlProjectsIn, city.toLowerCase());
                break;
            case NewProjectsIn:
                redirectUrl = String.format(URLGenerationConstants.GenericUrlNewProjectsIn, city.toLowerCase()); /* TODO */
                break;
            case UpcomingProjectsIn:
                redirectUrl = String.format(URLGenerationConstants.GenericUrlUpcomingProjectsIn, city.toLowerCase());
                break;
            case PreLaunchProjectsIn:
                redirectUrl = String.format(URLGenerationConstants.GenericUrlPreLaunchProjectsIn, city.toLowerCase());
                break;
            case UnderConstProjectsIn:
                redirectUrl = String.format(URLGenerationConstants.GenericUrlUnderConstProjectsIn, city.toLowerCase());
                break;
            case ReadyToMoveProjectsIn:
                redirectUrl = String.format(URLGenerationConstants.GenericUrlReadyToMoveProjectsIn, city.toLowerCase());
                break;
            case AffordableProjectsIn:
                redirectUrl = String.format(URLGenerationConstants.GenericUrlAffordableProjectsIn, city.toLowerCase());
                break;
            case LuxuryProjectsIn:
                redirectUrl = String.format(URLGenerationConstants.GenericUrlLuxuryProjectsIn, city.toLowerCase());
                break;
            case TopProjectsIn:
                redirectUrl = String.format(URLGenerationConstants.GenericUrlProjectsIn, city.toLowerCase());
                break;
            default:
                break;
        }

        return redirectUrl;
    }

    public Typeahead getTopResult(String query, Typeahead typeahead, String city) {
        String displayText = (this.getType().getText() + " " + city);
        String redirectUrl = getRedirectUrl(city);
        return (getTypeaheadObjectByIdTextAndURL(this.getType().toString(), displayText, redirectUrl));
    }

    private List<Locality> getTopLocalities(String cityName) {
        URI uri = URI.create(UriComponentsBuilder
                .fromUriString(
                        PropertyReader.getRequiredPropertyAsString(CorePropertyKeys.PROPTIGER_URL) + PropertyReader
                                .getRequiredPropertyAsString(CorePropertyKeys.LOCALITY_API_URL)
                                + "?"
                                + URLGenerationConstants.Selector
                                + String.format(URLGenerationConstants.SelectorGetLocalityNamesByCityName, cityName))
                .build().encode().toString());

        List<Locality> topLocalities = httpRequestUtil.getInternalApiResultAsTypeListFromCache(uri, Locality.class);
        return topLocalities;
    }

    private String addLocalityFilterToRedirectURL(String redirectUrl, String localityLabel) {
        if (StringUtils.contains(redirectUrl, "?")) {
            redirectUrl += ("&" + String.format(localityFilter, localityLabel));
        }
        else {
            redirectUrl += ("?" + String.format(localityFilter, localityLabel));
        }
        return redirectUrl;
    }
}
