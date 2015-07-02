package com.proptiger.columbus.util;

import java.util.Comparator;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.proptiger.columbus.util.TypeaheadUtils;
import com.proptiger.core.model.AbstractTypeahead;
import com.proptiger.core.model.Typeahead;

public class TypeaheadUtils {

    /* Assumes Typeahead id format is : TYPEAHEAD-<entity_name>-<entityId */

    /**
     * @param t
     *            Typeahead object
     * @return entity id extracted from typeaheadId
     */
    private static Logger logger = LoggerFactory.getLogger(TypeaheadUtils.class);

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
            logger.warn("Exception while parsing idAsString", ex);
            return 0;
        }
    }

    public static class AbstractTypeaheadComparatorScore implements Comparator<AbstractTypeahead> {
        @Override
        public int compare(AbstractTypeahead o1, AbstractTypeahead o2) {
            return o2.getScore().compareTo(o1.getScore());
        }
    }

    /** Inner classes : Comparators for Typeahead objects **/

    public static class TypeaheadComparatorTypeaheadType implements Comparator<Typeahead> {
        @Override
        public int compare(Typeahead o1, Typeahead o2) {
            return o2.getType().compareTo(o1.getType());
        }
    }

    public static class AbstractTypeaheadComparatorId implements Comparator<AbstractTypeahead> {
        @Override
        public int compare(AbstractTypeahead o1, AbstractTypeahead o2) {
            return o1.getId().compareTo(o2.getId());
        }
    }

}
