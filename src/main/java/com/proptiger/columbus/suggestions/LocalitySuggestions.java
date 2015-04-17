package com.proptiger.columbus.suggestions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.columbus.model.TypeaheadConstants;
import com.proptiger.columbus.util.Pair;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.util.UtilityClass;

@Component
public class LocalitySuggestions {

    private String templateId = "Typeahead-Suggestion-Locality";
    
    @Autowired
    private CustomPairComparatorIntToGeneric<SuggestionType> pairComparator;
    
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
            this.redirectUrlFormat = redirectUrlFormat;
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
        int projectCountResale = UtilityClass.safeUnbox(topResult.getEntityProjectCountResale(), 0);

        List<Pair<Integer, SuggestionType>> pairList = new ArrayList<Pair<Integer, SuggestionType>>();
        pairList.add(new Pair<Integer, SuggestionType>(projectCountNewLaunch, SuggestionType.NewLaunch));
        pairList.add(new Pair<Integer, SuggestionType>(projectCountUnderConst, SuggestionType.UnderConstruction));
        pairList.add(new Pair<Integer, SuggestionType>(projectCountAffordable, SuggestionType.Affordable));
        pairList.add(new Pair<Integer, SuggestionType>(projectCountLuxury, SuggestionType.Luxury));
        pairList.add(new Pair<Integer, SuggestionType>(projectCountResale, SuggestionType.Resale));
        
        Collections.sort(pairList, pairComparator);
        
        for (Pair<Integer, SuggestionType> pair : pairList){
            if (pair.getFirst() > TypeaheadConstants.suggestionProjectCountTheshold) {
                suggestionList.add(pair.getSecond());
            }
        }

        return UtilityClass.getFirstNElementsOfList(suggestionList, count);
    }

    private Typeahead makeTypeaheadObjectForSuggestionType(SuggestionType st, int localityId, Typeahead topResult) {

        String resultlabel = topResult.getLabel();
        String cityName = topResult.getCity();
        String localityName = topResult.getLocality();

        Typeahead typeahead = new Typeahead();
        String localityNameProcessed = (localityName.replace(' ', '-') + "-" + localityId);
        typeahead.setId(templateId + "-" + st.typeaheadIdFormat);
        typeahead.setType(typeahead.getId());
        typeahead.setDisplayText(String.format(st.displayTextFormat, resultlabel));
        typeahead.setRedirectUrl(String.format(st.redirectUrlFormat, cityName, localityNameProcessed).toLowerCase());
        typeahead.setSuggestion(true);
        return typeahead;
    }
    
    

}
