package com.proptiger.columbus.util;

import java.util.Comparator;

import org.apache.commons.lang.StringUtils;

import com.proptiger.core.model.AbstractTypeahead;
import com.proptiger.core.model.Typeahead;

public class TypeaheadUtils {

    /* Assumes Typeahead id format is : TYPEAHEAD-<entity_name>-<entityId */

    /**
     * @param t
     *            Typeahead object
     * @return entity id extracted from typeaheadId
     */
    public static String parseEntityIdAsString(Typeahead t) {
        String[] tokens = StringUtils.split(t.getId(), '-');
        if (tokens.length > 2) {
            return tokens[2];
        }
        else {
            return null;
        }
    }

    /**
     * @param t
     *            Typeahead object
     * @return entityId (int) if entityId is integer, null otherwise.
     */
    public static int parseEntityIdAsInt(Typeahead t) {
        String idAsString = parseEntityIdAsString(t);
        try {
            return (Integer.parseInt(idAsString));
        }
        catch (Exception ex) {
            return 0;
        }
    }

    public static class AbstractTypeaheadComparatorScore implements Comparator<AbstractTypeahead> {
        @Override
        public int compare(AbstractTypeahead o1, AbstractTypeahead o2) {
            return o2.getScore().compareTo(o1.getScore());
        }
    }

    public static class AbstractTypeaheadComparatorId implements Comparator<AbstractTypeahead> {
        @Override
        public int compare(AbstractTypeahead o1, AbstractTypeahead o2) {
            return o1.getId().compareTo(o2.getId());
        }
    }
}
