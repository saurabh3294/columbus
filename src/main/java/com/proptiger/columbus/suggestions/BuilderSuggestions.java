package com.proptiger.columbus.suggestions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.columbus.model.TypeaheadConstants;
import com.proptiger.columbus.util.Pair;
import com.proptiger.columbus.util.TypeaheadUtils;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.util.UtilityClass;

@Component
public class BuilderSuggestions {

    private String                                           templateId = "Typeahead-Suggestion-Builder";

    @Autowired
    private CustomPairComparatorIntToGeneric<SuggestionType> pairComparator;

    private enum SuggestionType {

        Upcoming("Upcoming projects by %s", "upcoming-project-by-%s-%s", "upcoming-project"), Completed(
                "Completed properties by %s", "completed-property-by-%s-%s", "completed-property"), UnderConstruction(
                "Under Construction projects by %s", "under-construction-property-by-%s-%s",
                "under-construction-property");

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

        int projectCountUpcoming = UtilityClass.safeUnbox(topResult.getEntityProjectCountNewLaunch(), 0);
        int projectCountUnderConst = UtilityClass.safeUnbox(topResult.getEntityProjectCountUnderConstruction(), 0);
        int projectCountUpCompleted = UtilityClass.safeUnbox(topResult.getEntityProjectCountCompleted(), 0);

        List<Pair<Integer, SuggestionType>> pairList = new ArrayList<Pair<Integer, SuggestionType>>();
        pairList.add(new Pair<Integer, SuggestionType>(projectCountUpcoming, SuggestionType.Upcoming));
        pairList.add(new Pair<Integer, SuggestionType>(projectCountUnderConst, SuggestionType.UnderConstruction));
        pairList.add(new Pair<Integer, SuggestionType>(projectCountUpCompleted, SuggestionType.Completed));

        Collections.sort(pairList, pairComparator);

        for (Pair<Integer, SuggestionType> pair : pairList) {
            if (pair.getFirst() > TypeaheadConstants.suggestionProjectCountTheshold) {
                suggestionList.add(pair.getSecond());
            }
        }

        return UtilityClass.getFirstNElementsOfList(suggestionList, count);
    }

    private Typeahead makeTypeaheadObjectForSuggestionType(SuggestionType st, int localityId, Typeahead topResult) {

        String builderName = topResult.getLabel();
        String builderIdString = TypeaheadUtils.parseEntityIdAsString(topResult);

        Typeahead typeahead = new Typeahead();
        typeahead = new Typeahead();
        typeahead.setId(templateId + "-" + st.typeaheadIdFormat);
        typeahead.setType(typeahead.getId());
        typeahead.setDisplayText(String.format(st.displayTextFormat, builderName));
        typeahead.setRedirectUrl(String.format(st.redirectUrlFormat, builderName, builderIdString).toLowerCase());
        typeahead.setSuggestion(true);
        return typeahead;
    }
}
