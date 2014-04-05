package com.proptiger.data.util.b2b;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.util.JsonLoader;
import com.proptiger.data.util.JsonUtil;

/**
 * Class for various json processing on b2b user preference
 * 
 * @author Azitabh Ajit
 * 
 */

public class B2bUserPreferenceProcessor {
    private static final JsonNode preferenceSahema;
    private static final JsonNode defaultPreference;
    private static final JsonUtil jsonUtil = new JsonUtil();

    static {
        try {
            preferenceSahema = JsonLoader.fromPath("src/main/resources/b2b/userPreferenceSchema.json");
            defaultPreference = JsonLoader.fromPath("src/main/resources/b2b/userPreferenceDefault.json");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * checks if a given json is a valid user preference or not
     * 
     * @param JsonNode
     *            preference
     * 
     * @return boolean
     * 
     * @author Azitabh Ajit
     * 
     */
    public static boolean isValidPreference(JsonNode preference) {
        return jsonUtil.isValidSchema(preferenceSahema, preference);
    }

    /**
     * imposes a user preference over default user preference
     * 
     * @param JsonNode
     *            preference
     * 
     * @return JsonNode
     * 
     * @author Azitabh Ajit
     * 
     */
    public static JsonNode mergeDefaultPreference(JsonNode preference) {
        return JsonUtil.mergeJsonNode(defaultPreference, preference);
    }
}