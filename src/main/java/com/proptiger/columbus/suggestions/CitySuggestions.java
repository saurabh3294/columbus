package com.proptiger.columbus.suggestions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.columbus.model.SuggestionInfo;
import com.proptiger.columbus.repo.SuggestionInfoDao;
import com.proptiger.core.enums.DomainObject;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.util.UtilityClass;

@Component
public class CitySuggestions {

    private String               templateId           = "Typeahead-Suggestion-City";

    private static int           suggestionEntityType = DomainObject.city.getObjectTypeId();

    private static String        entityIdFilterFormat = "{\"equal\":{\"cityId\":%s}},";

    @Autowired
    private SuggestionInfoDao    suggestionInfoDao;

    private SuggestionInfo       suggestionInfoAffordable;

    private SuggestionInfo       suggestionInfoLuxury;

    private SuggestionInfo       suggestionInfoUnderConstruction;

    private SuggestionInfo       suggestionInfoResale;

    private List<SuggestionInfo> suggestionInfoList;

    @PostConstruct
    private void initialize() {

        suggestionInfoList = new ArrayList<SuggestionInfo>();

        suggestionInfoAffordable = suggestionInfoDao.findByEntityTypeIdAndSuggestionType(
                suggestionEntityType,
                "affordable");
        suggestionInfoList.add(suggestionInfoAffordable);

        suggestionInfoLuxury = suggestionInfoDao.findByEntityTypeIdAndSuggestionType(suggestionEntityType, "luxury");
        suggestionInfoList.add(suggestionInfoLuxury);

        suggestionInfoUnderConstruction = suggestionInfoDao.findByEntityTypeIdAndSuggestionType(
                suggestionEntityType,
                "underConst");
        suggestionInfoList.add(suggestionInfoUnderConstruction);

        suggestionInfoResale = suggestionInfoDao.findByEntityTypeIdAndSuggestionType(suggestionEntityType, "resale");
        suggestionInfoList.add(suggestionInfoResale);

    }

    public List<Typeahead> getSuggestions(int id, Typeahead topResult, int count) {

        String name = topResult.getLabel();

        List<Typeahead> suggestions = new ArrayList<Typeahead>();
        Typeahead typeahead;
        for (SuggestionInfo st : suggestionInfoList) {
            typeahead = new Typeahead();
            typeahead.setDisplayText(String.format(st.getDisplayTextFormat(), name));
            typeahead.setRedirectUrl(name.toLowerCase() + "/" + st.getRedirectUrlFormat());

            String entityIdFilter = String.format(entityIdFilterFormat, String.valueOf(id));
            typeahead.setRedirectUrlFilters(String.format(st.getRedirectUrlFilters(), entityIdFilter));

            typeahead.setId(templateId + "-" + st.getTypeaheadIdFormat());
            typeahead.setType(typeahead.getId());
            typeahead.setSuggestion(true);
            suggestions.add(typeahead);
        }

        return filterByCustomRules(suggestions, count);
    }

    private List<Typeahead> filterByCustomRules(List<Typeahead> suggestions, int count) {
        Collections.shuffle(suggestions);
        String temp = (suggestions.get(0).getDisplayText() + " " + suggestions.get(1).getDisplayText());
        if (StringUtils.containsIgnoreCase(temp, "Resale property") && StringUtils.containsIgnoreCase(
                temp,
                "Ready to move")) {
            suggestions.remove(0);
        }
        return UtilityClass.getFirstNElementsOfList(suggestions, count);
    }

}
