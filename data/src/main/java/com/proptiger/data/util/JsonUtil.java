package com.proptiger.data.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;
import com.github.fge.jsonschema.report.ProcessingReport;

/**
 * Utility class for json objects
 * 
 * @author Azitabh Ajit
 * 
 */

public class JsonUtil {
    private static final JsonValidator VALIDATOR = JsonSchemaFactory.byDefault().getValidator();
    private Logger                     logger    = LoggerFactory.getLogger(this.getClass());
    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * matches a json for a fiven schema
     * 
     * @param JsonNode
     *            schemajson
     * @param JsonNode
     *            json
     * 
     * @return boolean
     * 
     * @author Azitabh Ajit
     * 
     */
    public boolean isValidSchema(JsonNode schemaJson, JsonNode json) {
        ProcessingReport r1;
        try {
            r1 = VALIDATOR.validate(schemaJson, json);
            return r1.isSuccess();
        }
        catch (ProcessingException e) {
            logger.debug(e.getStackTrace().toString());
        }
        return false;
    }

    /**
     * merges two json objects into one
     * 
     * @param JsonNode
     *            original
     * @param JsonNode
     *            imposed
     * 
     * @return JsonNode
     * 
     * @author Azitabh Ajit
     * 
     */
    public static JsonNode mergeJsonNode(JsonNode original, JsonNode imposed) {
        try {
            JsonMergePatch mergePatch = JsonMergePatch.fromJson(imposed);
            return mergePatch.apply(original);
        }
        catch (JsonPatchException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static boolean isValidJsonString(String str) {
        try {
            objectMapper.readTree(str);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
    public static void main(String args[]){
        System.out.println(JsonUtil.isValidJsonString(""));
    }
}
