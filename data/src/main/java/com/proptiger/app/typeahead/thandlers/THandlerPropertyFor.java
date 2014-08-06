package com.proptiger.app.typeahead.thandlers;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.Typeahead;
import com.proptiger.data.pojo.Selector;

public class THandlerPropertyFor extends RootTHandler {

    private String genericURLPropForSale    = "%s/property-sale&s";
    private String genericURLPropForResale    = "%s/property-sale&s/filters?listingType=true";

//    private String genericURLApttForSale    = "%s/apartments-flats-sale";
//    private String genericURLVillForSale    = "%s/villas-sale";
//    private String genericURLPlotsForSale   = "%s/sites-plots-sale";
//    private String genericURLNewApttForSale = "%s/new-apartments-for-sale";
    
    private String selectorCityFilter = "{\"filters\":{\"and\":[{\"equal\":{\"cityLabel\":%s}}]}}";
    
    @Override
    public List<Typeahead> getResults(String query, Typeahead typeahead, String city, int rows) {

        List<Typeahead> results = new ArrayList<Typeahead>();

        results.add(getTopResult(query, typeahead, city));

        List<Locality> topLocalities = getTopLocalities(city);
        String redirectURL;
        for (Locality locality : topLocalities) {
            redirectURL = getRedirectUrl();
            redirectURL = String.format(redirectURL, city, ("-" + locality.getLabel() + "-" + locality.getLocalityId()));
            results.add(getTypeaheadObjectByTextAndURL((this.getType().getText() + " " + locality.getLabel()), redirectURL));
            if (results.size() == rows) {
                break;
            }
        }

        return results;
    }

    private String getRedirectUrl() {
        String redirectUrl = "";
        TemplateTypes templateType = this.getType();
        switch (templateType) {
            case PropertyForSaleIn:
                redirectUrl = genericURLPropForSale;
                break;
            case PropertyForResaleIn:
                redirectUrl = genericURLPropForResale;
                break;
            default:
                break;
        }
        
        return redirectUrl;
   }
    
    @Override
    public Typeahead getTopResult(String query, Typeahead typeahead, String city) {
        String displayText = (this.getType().getText() + " " + city);
        String redirectUrl = getRedirectUrl();
        redirectUrl = String.format(redirectUrl, city.toLowerCase(), "");
        return (getTypeaheadObjectByTextAndURL(displayText, redirectUrl));
    }

    private List<Locality> getTopLocalities(String cityName) {
        Selector selector = (new Gson()).fromJson(String.format(selectorCityFilter, cityName), Selector.class);
        List<Locality> topLocalities = localityService.getLocalities(selector).getResults();
        return topLocalities;
    }

}
