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

    /**
     * takes typeahead object list as input and converts topsearch string fields
     * (topsearchedSuburb,topsearchedLocality...) into typeahead object
     * 
     * @param thList
     * @param requiredEntities
     *            (topsearchEntity string fields in typeahead object which
     *            require to be converted to typeahead object)
     * @param isGroup
     * @param inputRows
     * @return
     */
    public static List<Typeahead> typeaheadToTopsearchConverter(
            List<Typeahead> typeaheadList,
            String requiredEntities,
            Boolean isGroup,
            int inputRows) {
        List<Typeahead> topsearchList = new ArrayList<Typeahead>();

        requiredEntities = requiredEntities.toLowerCase();

        for (Typeahead th : typeaheadList) {
            if (StringUtils.contains(requiredEntities, DomainObject.suburb.getText())) {

                topsearchList.addAll(getTopsearchObjectFieldFromString(th.getTopSearchedSuburb(), inputRows));
            }
            if (StringUtils.contains(requiredEntities, DomainObject.locality.getText())) {

                topsearchList.addAll(getTopsearchObjectFieldFromString(th.getTopSearchedLocality(), inputRows));
            }
            if (StringUtils.contains(requiredEntities, DomainObject.builder.getText())) {

                topsearchList.addAll(getTopsearchObjectFieldFromString(th.getTopSearchedBuilder(), inputRows));
            }
            if (StringUtils.contains(requiredEntities, DomainObject.project.getText())) {

                topsearchList.addAll(getTopsearchObjectFieldFromString(th.getTopSearchedProject(), inputRows));
            }
        }

        return topsearchList;
    }

    private static List<Typeahead> getTopsearchObjectFieldFromString(String line, int rows) {
        List<Typeahead> objList = new ArrayList<Typeahead>();
        if (line == null || line.trim().isEmpty()) {
            return objList;
        }
        objList = new Gson().fromJson(line, type);
        objList = UtilityClass.getFirstNElementsOfList(objList, rows);
        return objList;
    }

}
