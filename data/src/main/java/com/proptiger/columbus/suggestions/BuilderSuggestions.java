package com.proptiger.columbus.suggestions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.columbus.model.SuggestionInfo;
import com.proptiger.columbus.model.TypeaheadConstants;
import com.proptiger.columbus.repo.SuggestionInfoDao;
import com.proptiger.columbus.util.Pair;
import com.proptiger.columbus.util.TypeaheadUtils;
import com.proptiger.core.enums.DomainObject;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.util.UtilityClass;

@Component
public class BuilderSuggestions {

    private String                                           templateId           = "Typeahead-Suggestion-Builder";

    private static int                                       suggestionEntityType = DomainObject.builder
                                                                                          .getObjectTypeId();

    private static String                                    entityIdFilterFormat = "{\"equal\":{\"builderId\":%s}},";

    @Autowired
    private CustomPairComparatorIntToGeneric<SuggestionInfo> pairComparator;

    @Autowired
    private SuggestionInfoDao                                suggestionInfoDao;

    private SuggestionInfo                                   suggestionTypeUpcoming;

    private SuggestionInfo                                   suggestionTypeCompleted;

    private SuggestionInfo                                   suggestionTypeUnderConstruction;

    @PostConstruct
    private void initialize() {

        suggestionTypeUpcoming = suggestionInfoDao
                .findByEntityTypeIdAndSuggestionType(suggestionEntityType, "upcoming");

        suggestionTypeCompleted = suggestionInfoDao.findByEntityTypeIdAndSuggestionType(
                suggestionEntityType,
                "completed");

        suggestionTypeUnderConstruction = suggestionInfoDao.findByEntityTypeIdAndSuggestionType(
                suggestionEntityType,
                "underConst");
    }

    public List<Typeahead> getSuggestions(int id, Typeahead topResult, int count) {

        List<Typeahead> suggestions = new ArrayList<Typeahead>();

        List<SuggestionInfo> suggestionTypeList = getRelevantSuggestionTypes(topResult, count);

        for (SuggestionInfo st : suggestionTypeList) {
            suggestions.add(makeTypeaheadObjectForSuggestionType(st, id, topResult));
        }

        return suggestions;
    }

    private List<SuggestionInfo> getRelevantSuggestionTypes(Typeahead topResult, int count) {
        List<SuggestionInfo> suggestionList = new ArrayList<SuggestionInfo>();

        int projectCountUpcoming = UtilityClass.safeUnbox(topResult.getEntityProjectCountNewLaunch(), 0);
        int projectCountUnderConst = UtilityClass.safeUnbox(topResult.getEntityProjectCountUnderConstruction(), 0);
        int projectCountUpCompleted = UtilityClass.safeUnbox(topResult.getEntityProjectCountCompleted(), 0);

        List<Pair<Integer, SuggestionInfo>> pairList = new ArrayList<Pair<Integer, SuggestionInfo>>();
        pairList.add(new Pair<Integer, SuggestionInfo>(projectCountUpcoming, suggestionTypeUpcoming));
        pairList.add(new Pair<Integer, SuggestionInfo>(projectCountUnderConst, suggestionTypeUnderConstruction));
        pairList.add(new Pair<Integer, SuggestionInfo>(projectCountUpCompleted, suggestionTypeCompleted));

        Collections.sort(pairList, pairComparator);

        for (Pair<Integer, SuggestionInfo> pair : pairList) {
            if (pair.getFirst() > TypeaheadConstants.SUGGESTION_PROJECT_COUNT_THESHOLD) {
                suggestionList.add(pair.getSecond());
            }
        }

        return UtilityClass.getFirstNElementsOfList(suggestionList, count);
    }

    private Typeahead makeTypeaheadObjectForSuggestionType(SuggestionInfo st, int builderId, Typeahead topResult) {

        String builderName = topResult.getLabel();
        String builderIdString = TypeaheadUtils.parseEntityIdAsString(topResult);

        Typeahead typeahead = new Typeahead();
        typeahead = new Typeahead();
        typeahead.setId(templateId + "-" + st.getTypeaheadIdFormat());
        typeahead.setType(typeahead.getId());
        typeahead.setDisplayText(String.format(st.getDisplayTextFormat(), builderName));
        typeahead.setRedirectUrl(String.format(st.getRedirectUrlFormat(), builderName, builderIdString).toLowerCase());

        String entityIdFilter = String.format(entityIdFilterFormat, String.valueOf(builderId));
        typeahead.setRedirectUrlFilters(String.format(st.getRedirectUrlFilters(), entityIdFilter));

        typeahead.setSuggestion(true);
        return typeahead;
    }
}
