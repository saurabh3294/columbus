package com.proptiger.columbus.model;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.proptiger.core.model.Typeahead;

public class TypeaheadConstants {

    private TypeaheadConstants() {

    }

    public static final String TYPEAHEAD_ID_PATTERN               = "TYPEAHEAD-%s-%s";

    public static final String DEFAULT_CITY_NAME                  = "Noida";
    public static final int    DEFAULT_CITY_ID                    = 20;
    public static final String CITY_COOKIE_LABEL                  = "HOME_CITY";
    public static final String CITY_COOKIE_SEPARATER              = "%2C";

    public static final float  SUGGESTION_SCORE_THRESHOLD         = 20.0f;

    public static final float  QUERY_TIME_BOOST_START             = 10f;
    public static final float  QUERY_TIME_BOOST_MULTIPLIER        = 0.3f;

    public static final float  CITY_BOOST_MIN_SCORE               = 8.0f;
    public static final float  CITY_BOOST                         = 1.25f;
    public static final float  DOCUMENT_FETCH_MULTIPLIER          = 4.0f;
    public static final float  DOCUMENT_FETCH_LIMIT               = 20;

    public static final String EXTERNAL_API_IDENTIFIER_GOOGLE     = "gp";

    public static final String TYPEAHEAD_FIELD_NAME_CITY          = "TYPEAHEAD_CITY";

    public static final String TYPEAHEAD_TYPE_BUILDER             = "BUILDER";

    /** Used while creating suggestions **/

    public static final int    MAX_SUGGESTION_COUNT               = 3;
    public static final int    SUGGESTION_NEWLAUNCH_MULTIPLIER    = 3;
    public static final int    SUGGESTION_PROJECT_COUNT_THESHOLD  = 0;

    public static final int    CITY_RADIUS                        = 50;

    public static final Type   GSON_TOKEN_TYPE_TYPEAHEAD_LIST     = new TypeToken<List<Typeahead>>() {}.getType();
    public static final Type   GSON_TOKEN_TYPE_PROPGUIDE_DOC_LIST = new TypeToken<List<PropguideDocument>>() {}
                                                                          .getType();

    public static final int    PROPGUIDE_POST_TAGS_MULTIPLIER     = 40;
}
