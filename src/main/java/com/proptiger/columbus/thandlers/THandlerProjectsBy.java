package com.proptiger.columbus.thandlers;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.proptiger.columbus.model.TemplateInfo;
import com.proptiger.columbus.util.PropertyKeys;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.model.cms.Builder;
import com.proptiger.core.util.CorePropertyKeys;
import com.proptiger.core.util.PropertyReader;

@Component
public class THandlerProjectsBy extends RootTHandler {

    private static Logger logger = LoggerFactory.getLogger(THandlerProjectsBy.class);

    private TemplateInfo  templateInfo;

    @Override
    @PostConstruct
    public void initialize() {
        templateInfo = templateInfoDao.findByTemplateType(TemplateTypes.PropertyBy.name());
    }

    @Override
    public List<Typeahead> getResults(String query, Typeahead template, String city, int cityId, int rows) {

        /* restrict results to top 2 builders for now */
        rows = Math.min(rows, 2);

        List<Typeahead> results = new ArrayList<Typeahead>();
        List<Builder> topBuilders = getTopBuilders(city);

        if (topBuilders == null) {
            logger.error("Could not fetch top builders for city " + city);
            return results;
        }

        String id, displayText, redirectUrl;
        String redirectUrlFilters = templateInfo.getRedirectUrlFilters();
        Typeahead t;
        for (Builder builder : topBuilders) {
            id = getTemplateType(template).toString();
            displayText = templateInfo.getDisplayTextFormat() + " " + builder.getName();
            redirectUrl = String.format(templateInfo.getRedirectUrlFormat(), city.toLowerCase(), builder.getUrl());
            redirectUrlFilters = String.format(redirectUrlFilters, getBuilderCityFilter(cityId, builder.getId()));
            t = getTypeaheadObjectByIdTextAndURL(id, displayText, redirectUrl, redirectUrlFilters);
            results.add(t);
            if (results.size() == rows) {
                break;
            }
        }

        return results;
    }

    @Override
    public Typeahead getTopResult(String query, Typeahead template, String city, int cityId) {

        List<Typeahead> results = getResults(query, template, city, cityId, 1);
        if (results != null && !results.isEmpty()) {
            return results.get(0);
        }
        else
            return null;
    }

    private List<Builder> getTopBuilders(String cityName) {
        List<Builder> topBuilders = httpRequestUtil
                .getInternalApiResultAsTypeListFromCache(
                        URI.create(UriComponentsBuilder
                                .fromUriString(
                                        PropertyReader.getRequiredPropertyAsString(CorePropertyKeys.PROPTIGER_URL) + PropertyReader
                                                .getRequiredPropertyAsString(CorePropertyKeys.BUILDER_API_URL)
                                                + "?"
                                                + URLGenerationConstants.SELECTOR
                                                + String.format(
                                                        URLGenerationConstants.SELECTOR_GET_BUILDERNAMES_BY_CITYNAME,
                                                        cityName)).build().encode().toString()),
                        PropertyReader.getRequiredPropertyAsInt(PropertyKeys.INTERNAL_API_SLA_MS),
                        Builder.class);

        Collections.sort(topBuilders, new Comparator<Builder>() {
            @Override
            public int compare(Builder o1, Builder o2) {
                return o2.getProjectCount() - o1.getProjectCount();
            }
        });
        return topBuilders;
    }

    private String getBuilderCityFilter(int cityId, int builderId) {
        String builderFilter = String.format(URLGenerationConstants.BUILDER_ID_FILTER_FORMAT, builderId);
        String cityFilter = String.format(URLGenerationConstants.CITY_ID_FILTER_FORMAT, cityId);
        return String.format(cityFilter + "," + builderFilter);
    }
}
