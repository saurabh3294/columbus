package com.proptiger.app.typeahead.thandlers;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.proptiger.data.model.Builder;
import com.proptiger.data.model.Typeahead;
import com.proptiger.data.pojo.Selector;

public class THandlerProjectsBy extends RootTHandler {

    private String selectorCityFilter = "{\"filters\":{\"and\":[{\"equal\":{\"cityLabel\":%s}}]}}";

    @Override
    public List<Typeahead> getResults(String query, Typeahead typeahead, String city, int rows) {

        /* restrict results to top 2 builders for now */
        rows = Math.min(rows, 2);

        List<Typeahead> results = new ArrayList<Typeahead>();
        List<Builder> topBuilders = getTopBuilders(city);

        String redirectURL;
        for (Builder builder : topBuilders) {
            redirectURL = builder.getUrl();
            results.add(getTypeaheadObjectByIdTextAndURL(this.getType().toString(), (this.getType().getText() + " " + builder.getName()), redirectURL));
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
        Selector selector = (new Gson()).fromJson(
                String.format(selectorCityFilter, cityName),
                Selector.class);
        List<Builder> topBuilders = builderService.getTopBuilders(selector).getResults();
        return topBuilders;
    }

}
