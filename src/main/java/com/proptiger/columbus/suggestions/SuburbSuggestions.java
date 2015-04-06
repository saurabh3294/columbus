package com.proptiger.columbus.suggestions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.proptiger.columbus.model.TypeaheadConstants;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.util.UtilityClass;

@Component
public class SuburbSuggestions {

    private String templateId = "Typeahead-Suggestion-Suburb";

    private enum SuggestionType {

        Affordable("Affordable apartments in %s", "%s/affordable-flats-in-%s", "affordable-flats"), Luxury(
                "Luxury projects in %s", "%s/luxury-projects-in-%s", "luxury-projects"), NewLaunch(
                "New apartments in %s", "%s/new-apartments-for-sale-in-%s", "new-apartments"), UnderConstruction(
                "Under construction projects in %s", "%s/under-construction-property-in-%s",
                "under-construction-property"), Resale("Resale property in %s", "%s/resale-property-in-%s",
                "resale-property");

        String displayTextFormat, redirectUrlFormat, typeaheadIdFormat;

        SuggestionType(String displayTextFormat, String redirectUrlFormat, String typeaheadIdFormat) {
            this.displayTextFormat = displayTextFormat;
            this.redirectUrlFormat = displayTextFormat;
            this.typeaheadIdFormat = typeaheadIdFormat;
        }
    }

    public List<Typeahead> getSuggestions(int id, Typeahead topResult, int count) {

        List<Typeahead> suggestions = new ArrayList<Typeahead>();

        List<SuggestionType> suggestionTypeList = getRelevantSuggestionTypes(topResult, count);

        for (SuggestionType st : suggestionTypeList) {
            suggestions.add(makeTypeaheadObjectForSuggestionType(st, id, topResult));
        }

        return suggestions;
    }

    private List<SuggestionType> getRelevantSuggestionTypes(Typeahead topResult, int count) {
        List<SuggestionType> suggestionList = new ArrayList<SuggestionType>();
        
        int projectCountNewLaunch = UtilityClass.safeUnbox(topResult.getEntityProjectCountNewLaunch(), 0);
        projectCountNewLaunch *= (TypeaheadConstants.suggestionNewLaunchMultiplier);
        
        int projectCountUnderConst = UtilityClass.safeUnbox(topResult.getEntityProjectCountUnderConstruction(), 0);
        int projectCountAffordable = UtilityClass.safeUnbox(topResult.getEntityProjectCountAffordable(), 0);
        int projectCountLuxury = UtilityClass.safeUnbox(topResult.getEntityProjectCountLuxury(), 0);
        int projectCountCompleted = UtilityClass.safeUnbox(topResult.getEntityProjectCountCompleted(), 0);
        
        Map<Integer, SuggestionType> map = new TreeMap<Integer, SuggestionType>(Collections.reverseOrder());
        map.put(projectCountNewLaunch, SuggestionType.NewLaunch);
        map.put(projectCountUnderConst, SuggestionType.UnderConstruction);
        map.put(projectCountAffordable, SuggestionType.Affordable);
        map.put(projectCountLuxury, SuggestionType.Luxury);
        map.put(projectCountCompleted, SuggestionType.Resale);
        
        for(Entry<Integer, SuggestionType> entry : map.entrySet()){
            if(entry.getKey() > TypeaheadConstants.suggestionProjectCountTheshold){
                suggestionList.add(entry.getValue());
            }
        }
        
        return UtilityClass.getFirstNElementsOfList(suggestionList, count);
    }

    private Typeahead makeTypeaheadObjectForSuggestionType(SuggestionType st, int suburbId, Typeahead topResult) {

        String resultlabel = topResult.getLabel();
        String cityName = topResult.getCity();
        String redirectUrl = topResult.getRedirectUrl();

        Typeahead typeahead = new Typeahead();
        typeahead.setId(templateId + "-" + st.typeaheadIdFormat);
        typeahead.setType(typeahead.getId());
        typeahead.setDisplayText(String.format(st.displayTextFormat, resultlabel));
        typeahead.setRedirectUrl(String.format(st.redirectUrlFormat, cityName, makeSuburbRedirectUrl(redirectUrl)).toLowerCase());
        typeahead.setSuggestion(true);
        return typeahead;
    }
    
    /*
     * Hardcoded URL generation here. extracting form format ::
     * noida/property-sale-noida-expressway-10049
     */
    /* TODO :: include suburb_id and suburb_name while solr indexing */
    private String makeSuburbRedirectUrl(String redirectUrl) {
        return (StringUtils.split(redirectUrl, '/')[1].substring("property-sale-".length()));
    }
    
}
