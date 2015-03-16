package com.proptiger.columbus.thandlers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import com.proptiger.core.model.Typeahead;
import com.proptiger.core.model.cms.Builder;
import com.proptiger.core.util.CorePropertyKeys;
import com.proptiger.core.util.PropertyReader;

public class THandlerProjectsBy extends RootTHandler {

    private static Logger logger = LoggerFactory.getLogger(THandlerProjectsBy.class);

    @Override
    public List<Typeahead> getResults(String query, Typeahead typeahead, String city, int rows) {

        /* restrict results to top 2 builders for now */
        rows = Math.min(rows, 2);

        List<Typeahead> results = new ArrayList<Typeahead>();
        List<Builder> topBuilders = getTopBuilders(city);

        if (topBuilders == null) {
            logger.error("Could not fetch top builders for city " + city);
            return results;
        }

        String redirectURL;
        for (Builder builder : topBuilders) {
            redirectURL = builder.getUrl();
            results.add(getTypeaheadObjectByIdTextAndURL(
                    this.getType().toString(),
                    (this.getType().getText() + " " + builder.getName()),
                    redirectURL));
            if (results.size() == rows) {
                break;
            }
        }

        return results;
    }

    @Override
    public Typeahead getTopResult(String query, Typeahead typeahead, String city) {

        List<Typeahead> results = getResults(query, typeahead, city, 1);
        if (results != null && !results.isEmpty()) {
            return results.get(0);
        }
        else
            return null;
    }

    private List<Builder> getTopBuilders(String cityName) {
        List<Builder> topBuilders = httpRequestUtil.getInternalApiResultAsTypeListFromCache(URI
                .create(UriComponentsBuilder
                        .fromUriString(
                                PropertyReader.getRequiredPropertyAsString(CorePropertyKeys.PROPTIGER_URL) + PropertyReader
                                        .getRequiredPropertyAsString(CorePropertyKeys.BUILDER_API_URL)
                                        + "?"
                                        + URLGenerationConstants.Selector
                                        + String.format(
                                                URLGenerationConstants.SelectorGetBuilderNamesByCityName,
                                                cityName)).build().encode().toString()), Builder.class);
        return topBuilders;
    }

}
