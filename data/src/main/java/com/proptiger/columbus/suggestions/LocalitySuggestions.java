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
import com.proptiger.core.enums.DomainObject;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.util.UtilityClass;

@Component
public class LocalitySuggestions {

    private String                                           templateId           = "Typeahead-Suggestion-Locality";

    private static int                                       suggestionEntityType = DomainObject.locality
                                                                                          .getObjectTypeId();

    private static String                                    entityIdFilterFormat = "{\"equal\":{\"localityId\":%s}},";

    @Autowired
    private CustomPairComparatorIntToGeneric<SuggestionInfo> pairComparator;

    @Autowired
    private SuggestionInfoDao                                suggestionInfoDao;

    private SuggestionInfo                                   suggestionInfoAffordable;

    private SuggestionInfo                                   suggestionInfoLuxury;

    private SuggestionInfo                                   suggestionInfoNewLaunch;

    private SuggestionInfo                                   suggestionInfoUnderConstruction;

    private SuggestionInfo                                   suggestionInfoResale;

    @PostConstruct
    private void initialize() {

        suggestionInfoAffordable = suggestionInfoDao.findByEntityTypeIdAndSuggestionType(
                suggestionEntityType,
                "affordable");

        suggestionInfoLuxury = suggestionInfoDao.findByEntityTypeIdAndSuggestionType(suggestionEntityType, "luxury");

        suggestionInfoNewLaunch = suggestionInfoDao.findByEntityTypeIdAndSuggestionType(
                suggestionEntityType,
                "newLaunch");

        suggestionInfoUnderConstruction = suggestionInfoDao.findByEntityTypeIdAndSuggestionType(
                suggestionEntityType,
                "underConst");

        suggestionInfoResale = suggestionInfoDao.findByEntityTypeIdAndSuggestionType(suggestionEntityType, "resale");

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

        int projectCountNewLaunch = UtilityClass.safeUnbox(topResult.getEntityProjectCountNewLaunch(), 0);
        projectCountNewLaunch *= (TypeaheadConstants.SUGGESTION_NEWLAUNCH_MULTIPLIER);

        int projectCountUnderConst = UtilityClass.safeUnbox(topResult.getEntityProjectCountUnderConstruction(), 0);
        int projectCountAffordable = UtilityClass.safeUnbox(topResult.getEntityProjectCountAffordable(), 0);
        int projectCountLuxury = UtilityClass.safeUnbox(topResult.getEntityProjectCountLuxury(), 0);
        int projectCountResale = UtilityClass.safeUnbox(topResult.getEntityProjectCountResale(), 0);

        List<Pair<Integer, SuggestionInfo>> pairList = new ArrayList<Pair<Integer, SuggestionInfo>>();
        pairList.add(new Pair<Integer, SuggestionInfo>(projectCountNewLaunch, suggestionInfoNewLaunch));
        pairList.add(new Pair<Integer, SuggestionInfo>(projectCountUnderConst, suggestionInfoUnderConstruction));
        pairList.add(new Pair<Integer, SuggestionInfo>(projectCountAffordable, suggestionInfoAffordable));
        pairList.add(new Pair<Integer, SuggestionInfo>(projectCountLuxury, suggestionInfoLuxury));
        pairList.add(new Pair<Integer, SuggestionInfo>(projectCountResale, suggestionInfoResale));

        Collections.sort(pairList, pairComparator);

        for (Pair<Integer, SuggestionInfo> pair : pairList) {
            if (pair.getFirst() > TypeaheadConstants.SUGGESTION_PROJECT_COUNT_THESHOLD) {
                suggestionList.add(pair.getSecond());
            }
        }

        return UtilityClass.getFirstNElementsOfList(suggestionList, count);
    }

    private Typeahead makeTypeaheadObjectForSuggestionType(SuggestionInfo st, int localityId, Typeahead topResult) {

        String resultlabel = topResult.getLabel();
        String cityName = topResult.getCity();
        String localityName = topResult.getLocality();

        Typeahead typeahead = new Typeahead();
        String localityNameProcessed = (localityName.replace(' ', '-') + "-" + localityId);
        typeahead.setId(templateId + "-" + st.getTypeaheadIdFormat());
        typeahead.setType(typeahead.getId());
        typeahead.setDisplayText(String.format(st.getDisplayTextFormat(), resultlabel));
        typeahead.setRedirectUrl(String.format(st.getRedirectUrlFormat(), cityName, localityNameProcessed)
                .toLowerCase());

        String entityIdFilter = String.format(entityIdFilterFormat, String.valueOf(localityId));
        typeahead.setRedirectUrlFilters(String.format(st.getRedirectUrlFilters(), entityIdFilter));

        typeahead.setSuggestion(true);
        return typeahead;
    }
}
