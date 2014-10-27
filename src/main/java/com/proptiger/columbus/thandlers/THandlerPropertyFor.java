package com.proptiger.columbus.thandlers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.reflect.TypeToken;
import com.proptiger.columbus.model.Typeahead;
import com.proptiger.core.model.cms.Locality;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;

public class THandlerPropertyFor extends RootTHandler {

    private String localityFilter = "locality=%s";

    @Override
    public List<Typeahead> getResults(String query, Typeahead typeahead, String city, int rows) {

        /* restrict results to top 2 localities for now */
        rows = Math.min(rows, 3);

        List<Typeahead> results = new ArrayList<Typeahead>();

        results.add(getTopResult(query, typeahead, city));

        List<Locality> topLocalities = getTopLocalities(city);
        String redirectURL;
        for (Locality locality : topLocalities) {
            redirectURL = getRedirectUrl(city);
            redirectURL = addLocalityFilterToRedirectURL(redirectURL, locality.getLabel());
            results.add(getTypeaheadObjectByIdTextAndURL(
                    this.getType().toString(),
                    (this.getType().getText() + " " + locality.getLabel()),
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
            case PropertyForSaleIn:
                redirectUrl = String.format(URLGenerationConstants.GenericURLPropertyForSale, city.toLowerCase());
                break;
            case PropertyForResaleIn:
                redirectUrl = String.format(URLGenerationConstants.GenericURLPropertyForResale, city.toLowerCase());
                break;
            default:
                break;
        }

        return redirectUrl;
    }

    @Override
    public Typeahead getTopResult(String query, Typeahead typeahead, String city) {
        String displayText = (this.getType().getText() + " " + city);
        String redirectUrl = getRedirectUrl(city);
        return (getTypeaheadObjectByIdTextAndURL(this.getType().toString(), displayText, redirectUrl));
    }

    private List<Locality> getTopLocalities(String cityName) {
        List<Locality> topLocalities = HttpRequestUtil.getInternalApiResultAsTypeList(URI.create(UriComponentsBuilder
                .fromUriString(
                        PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL) + PropertyReader
                                .getRequiredPropertyAsString(PropertyKeys.LOCALITY_API_URL)
                                + "?"
                                + URLGenerationConstants.Selector
                                + String.format(URLGenerationConstants.SelectorGetLocalityNamesByCityName, cityName))
                .build().encode().toString()), new TypeToken<ArrayList<Locality>>() {}.getType());
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
