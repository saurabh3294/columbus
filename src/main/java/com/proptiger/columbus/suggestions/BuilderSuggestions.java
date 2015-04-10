package com.proptiger.columbus.suggestions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.springframework.stereotype.Component;

import com.proptiger.columbus.model.TypeaheadConstants;
import com.proptiger.columbus.util.TypeaheadUtils;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.util.UtilityClass;

@Component
public class BuilderSuggestions {

    private String templateId = "Typeahead-Suggestion-Builder";

    private enum SuggestionType {

        Upcoming("Upcoming projects by %s", "upcoming-project-by-%s-%s", "upcoming-project"), Completed(
                "Completed properties by %s", "completed-property-by-%s-%s", "completed-property"), UnderConstruction(
                "Under Construction projects by %s", "under-construction-property-by-%s-%s", "under-construction-property");

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

        Map<Integer, SuggestionType> map = new TreeMap<Integer, SuggestionType>(Collections.reverseOrder());
        map.put(projectCountUpcoming, SuggestionType.Upcoming);
        map.put(projectCountUnderConst, SuggestionType.UnderConstruction);
        map.put(projectCountUpCompleted, SuggestionType.Completed);

        for (Entry<Integer, SuggestionType> entry : map.entrySet()) {
            if (entry.getKey() > TypeaheadConstants.suggestionProjectCountTheshold) {
                suggestionList.add(entry.getValue());
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
        typeahead.setDisplayText(String.format(st.displayTextFormat, builderName));
        typeahead.setRedirectUrl(String.format(st.redirectUrlFormat, builderName, builderIdString).toLowerCase());
        typeahead.setSuggestion(true);
        return typeahead;
    }
}
