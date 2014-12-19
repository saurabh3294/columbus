package com.proptiger.data.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.Typeahead;
import com.proptiger.core.model.cms.Listing;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;

@Service
public class TypeAheadService {
    private Logger          logger                      = LoggerFactory.getLogger(TypeAheadService.class);

    private final String    projectTypeAheadQueryString = "?query=%s&rows=%s&typeAheadType=%s";

    @Autowired
    private HttpRequestUtil requestUtil;

    public List<Typeahead> getTypeaheadResultsFromColumbus(String query, String typeaheadType, int rows) {
        URI uri = URI.create(PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL) + PropertyReader
                .getRequiredPropertyAsString(PropertyKeys.COLUMBUS_TYPEAHEAD_URL)
                + String.format(projectTypeAheadQueryString, query, rows, typeaheadType));
        return requestUtil.getInternalApiResultAsTypeList(uri, Typeahead.class);
    }

    public List<Typeahead> filterTypeAheadContainingListings(
            List<Typeahead> typeaheads,
            List<Listing> listings,
            int finalSize) {
        Map<Integer, Boolean> pidMap = new HashMap<>();
        listings.forEach(l -> pidMap.put(l.getProperty().getProjectId(), true));
        List<Typeahead> finalTypeaheadList = new ArrayList<>();
        for (Typeahead typeahead : typeaheads) {
            Integer projectId = Integer.parseInt(typeahead.getId().substring(18));
            if (pidMap.containsKey(projectId)) {
                finalTypeaheadList.add(typeahead);
            }
        }
        return finalTypeaheadList.subList(0, Math.min(finalSize, finalTypeaheadList.size()));
    }
}
