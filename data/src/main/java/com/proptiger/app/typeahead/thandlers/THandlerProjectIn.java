package com.proptiger.app.typeahead.thandlers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.Typeahead;
import com.proptiger.data.pojo.Selector;

@Component
public class THandlerProjectIn extends RootTHandler {

    private String selectorCityFilter = "{\"filters\":{\"and\":[{\"equal\":{\"cityLabel\":%s}}]}}";
    
    private String genericUrlProjectsIn               = "%s-real-estate";
    private String genericUrlLuxuryProjectsIn         = "%s/luxury-projects";
    private String genericUrlAffordableProjectsIn     = "%s/affordable-flats";
    private String genericUrlUpcomingProjectsIn       = "%s/upcoming-flats-for-sale";    
    
    private String genericUrlNewProjectsIn            = "%s-real-estate/filters?projectStatus=launch";
    private String genericUrlPreLaunchProjectsIn      = "%s-real-estate/filters?projectStatus=not launched,pre launch";
    private String genericUrlUnderConstProjectsIn     = "%s-real-estate/filters?projectStatus=under construction";
    private String genericUrlReadyToMoveProjectsIn    = "%s-real-estate/filters?projectStatus=ready for possession,occupied";
    
    /* TODO :: ask to use filter usl or footer url if both are there */
    
    private String localityFilter = "?locality=%s";
    
    @Override
    public List<Typeahead> getResults(Typeahead typeahead, String city, int rows) {

        List<Typeahead> results = new ArrayList<Typeahead>();

        results.add(getTopResult(typeahead, city));

        List<Locality> topLocalities = getTopLocalities(city);
        String redirectURL;
        for (Locality locality : topLocalities) {
            redirectURL = getRedirectUrl(this.getType().getText() + " ", city) + (String.format(localityFilter, locality.getLabel()));
            results.add(getTypeaheadObjectByTextAndURL((this.getType().getText() + " " + locality.getLabel()), redirectURL));
            if (results.size() == rows) {
                break;
            }
        }

        return results;
    }

    private String getRedirectUrl(String templateText, String city) {
        String redirectUrl = "";
        TemplateTypes templateType = this.getType();
        switch (templateType) {
            case ProjectsIn:
                redirectUrl = String.format(genericUrlProjectsIn, city.toLowerCase());
                break;
            case NewProjectsIn:
                redirectUrl = String.format(genericUrlNewProjectsIn, city.toLowerCase());  /* TODO */
                break;
            case UpcomingProjectsIn:
                redirectUrl = String.format(genericUrlUpcomingProjectsIn, city.toLowerCase());
                break;
            case PreLaunchProjectsIn:
                redirectUrl = String.format(genericUrlPreLaunchProjectsIn, city.toLowerCase());
                break;
            case UnderConstProjectsIn:
                redirectUrl = String.format(genericUrlUnderConstProjectsIn, city.toLowerCase());
                break;
            case ReadyToMoveProjectsIn:
                redirectUrl = String.format(genericUrlReadyToMoveProjectsIn, city.toLowerCase());
                break;
            case AffordableProjectsIn:
                redirectUrl = String.format(genericUrlAffordableProjectsIn, city.toLowerCase());
                break;
            case LuxuryProjectsIn:
                redirectUrl = String.format(genericUrlLuxuryProjectsIn, city.toLowerCase());
                break;
            case TopProjectsIn:
                redirectUrl = String.format(genericUrlProjectsIn, city.toLowerCase());
                break;
            case TopPropertiesIn:
                redirectUrl = String.format(genericUrlProjectsIn, city.toLowerCase());
                break;
            default:
                break;
        }

        return redirectUrl;
    }

    public Typeahead getTopResult(Typeahead typeahead, String city) {
        String displayText = (this.getType().getText() + " " + city);
        String redirectUrl = getRedirectUrl(this.getType().getText() + " ", city);
        return (getTypeaheadObjectByTextAndURL(displayText, redirectUrl));
    }
    
    private List<Locality> getTopLocalities(String cityName)
    {
        Selector selector = (new Gson()).fromJson(String.format(selectorCityFilter, cityName), Selector.class);
        List<Locality> topLocalities = localityService.getLocalities(selector).getResults();
        return topLocalities;
    }

}
