package com.proptiger.app.typeahead.thandlers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.Typeahead;
import com.proptiger.data.pojo.Selector;

public class THandlerPropertyFor extends RootTHandler {

    private String genericURLPropForSale    = "%s/property-sale";
    private String genericURLPropForResale    = "%s/property-sale/filters?listingType=true";

//    private String genericURLApttForSale    = "%s/apartments-flats-sale";
//    private String genericURLVillForSale    = "%s/villas-sale";
//    private String genericURLPlotsForSale   = "%s/sites-plots-sale";
//    private String genericURLNewApttForSale = "%s/new-apartments-for-sale";
    
    private String selectorCityFilter = "{\"filters\":{\"and\":[{\"equal\":{\"cityLabel\":%s}}]}}";
    
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
            results.add(getTypeaheadObjectByIdTextAndURL(this.getType().toString(), (this.getType().getText() + " " + locality.getLabel()), redirectURL));
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
                redirectUrl = String.format(genericURLPropForSale, city.toLowerCase());
                break;
            case PropertyForResaleIn:
                redirectUrl = String.format(genericURLPropForResale, city.toLowerCase());
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
        Selector selector = (new Gson()).fromJson(String.format(selectorCityFilter, cityName), Selector.class);
        List<Locality> topLocalities = localityService.getLocalities(selector).getResults();
        return topLocalities;
    }
    
    private String addLocalityFilterToRedirectURL(String redirectUrl, String localityLabel)
    {
        if(StringUtils.contains(redirectUrl, "?")){
            redirectUrl += ("&" + String.format(localityFilter, localityLabel));
        }
        else{
            redirectUrl += ("?" + String.format(localityFilter, localityLabel));
        }
        return redirectUrl;
    }

}
