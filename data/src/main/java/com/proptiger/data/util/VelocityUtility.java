package com.proptiger.data.util;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This class provides a way to do some sorting in velocity template file
 * 
 * @author Rajeev Pandey
 * 
 */
public class VelocityUtility {

    public void sort(List<String> list) {
        if (list != null) {
            Collections.sort(list);
        }
    }

    public static void main(String args[]) {
        String str = "1.5BHK";
        str.split("\\d");
    }

    public Integer abs(Integer val) {
        return Math.abs(val);
    }

    public Double abs(Double val) {
        return Math.abs(val);
    }

    public long round(double val) {
        return Math.round(val);
    }

    public long round(Double val) {
        return Math.round(val);
    }

    public String roundToTwoDecimal(Double val) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(val);
    }

    public Map<String, String> getMapFromJson(String jsonDump) {
        if (jsonDump == null) {
            return null;
        }

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonDump);
        }
        catch (ParseException e) {
            return null;
        }

        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("comment", (String) jsonObject.get("comment"));
        jsonMap.put("tower", (String) jsonObject.get("tower"));
        return jsonMap;
    }

}
