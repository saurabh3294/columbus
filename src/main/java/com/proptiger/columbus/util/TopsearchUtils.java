package com.proptiger.columbus.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.proptiger.core.enums.DomainObject;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.util.UtilityClass;

public class TopsearchUtils {

    private static Type type = new TypeToken<List<Typeahead>>() {}.getType();

    public static List<Topsearch> typeaheadToTopsearchConverter(List<Typeahead> thList, String requiredEntities) {
        List<Topsearch> tsList = new ArrayList<Topsearch>();
        int rows = PropertyKeys.TOPSEARCH_ROW_COUNTS;
        Topsearch topsearch;
        requiredEntities = requiredEntities.toLowerCase();
        for (Typeahead th : thList) {
            topsearch = new Topsearch();
            topsearch.setEntityId(TypeaheadUtils.parseEntityIdAsString(th));
            topsearch.setEntityType(th.getType());

            if (StringUtils.contains(requiredEntities, DomainObject.suburb.getText())) {
                topsearch.setSuburb(getTopsearchObjectFieldFromString(th.getTopSearchedSuburb(), rows));
            }
            if (StringUtils.contains(requiredEntities, DomainObject.locality.getText())) {
                topsearch.setLocality(getTopsearchObjectFieldFromString(th.getTopSearchedLocality(), rows));
            }
            if (StringUtils.contains(requiredEntities, DomainObject.builder.getText())) {
                topsearch.setBuilder(getTopsearchObjectFieldFromString(th.getTopSearchedBuilder(), rows));
            }
            if (StringUtils.contains(requiredEntities, DomainObject.project.getText())) {
                topsearch.setProject(getTopsearchObjectFieldFromString(th.getTopSearchedProject(), rows));
            }

            tsList.add(topsearch);
        }
        return tsList;
    }

    public static List<Typeahead> getTopsearchObjectFieldFromString(String line, int rows) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        List<Typeahead> objList = new Gson().fromJson(line, type);
        objList = UtilityClass.getFirstNElementsOfList(objList, rows);
        return objList;
    }

}
