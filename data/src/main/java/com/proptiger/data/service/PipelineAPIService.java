package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipelineAPIService {

    private static Logger logger = LoggerFactory.getLogger(PipelineAPIService.class);

    /* kwe = keyword-expression */
    private String kweOuterDlim    = ".";
    private String kweInnerDlimStart   = "[";
    private String kweInnerDlimEnd   = "]";
    private String kweStartKeyword = "api";

    /* utp = url-template-param */
    private String utpDelimStart = "<";
    private String utpDelimEnd = ">";
    
    public Map<String, Object> getResponseForApis(List<String> urlList, HttpServletRequest request) {
        List<Object> apiResultList = new ArrayList<Object>();
        
        String parsedUrl = null;
        for(String url : urlList)
        {
            parsedUrl = buildApiUrl(url, apiResultList);
            apiResultList.add(getApiResult(parsedUrl));
        }
        
        return null;
    }

    private Object getApiResult(String url) 
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param templateUrl : e.g. /data/v1/trend/hitherto?filters=(cityId=ge=<expression1>;cityId=le=<expression2>)&fields=.....
     * @param apiResultList
     * @return
     */
    private String buildApiUrl(String templateUrl, List<Object> apiResultList) {
        
        String finalUrl = templateUrl;
        String[] expressions = StringUtils.substringsBetween(templateUrl, utpDelimStart, utpDelimEnd);
        String value = null;
        for(String exp : expressions)
        {
            value = evaluateParamExpression(exp, apiResultList);
            finalUrl = StringUtils.replace(finalUrl, (utpDelimStart + exp + utpDelimEnd), value);
        }
        return finalUrl;
    }

    /**
     * @param expression
     *            :: api1.data[i1].fieldName1[i2].fieldName3.fieldName4[i3]....
     * @return extracted value OR "null" if something goes wrong
     */
    @SuppressWarnings("unchecked")
    private String evaluateParamExpression(String expression, List<Object> apiResultList) {

        Object value = null;

        if (expression == null || expression.isEmpty() || apiResultList == null) {
            return String.valueOf(value);
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(kweStartKeyword, apiResultList);

        int index = -1;
        String[] outerKeys = StringUtils.split(expression, kweOuterDlim);
        try{
            for (String key : outerKeys) {
                map = (Map<String, Object>) value;
                index = extractArrayIndexFromExpression(key);
    
                if (index == -1) {              // is a Map
                    value = map.get(key);
                }
                else {                          // is an array of Maps
                    value = ((List<Map<String, Object>>) (map.get(key))).get(index);
                }
            }
        }
        catch(Exception ex){
            logger.error("Invalid index at pipeline expression : " + expression);
        }
        
        return String.valueOf(value);
    }

    /**
     * @param expression
     *            : "<keyword>[<index>]"
     * @return index of '-1' if something goes wrong.
     */
    private int extractArrayIndexFromExpression(String expression) {
        try {
            return (Integer.parseInt(StringUtils.substringsBetween(expression, kweInnerDlimStart, kweInnerDlimEnd)[0]));
        }
        catch (Exception e) {
            logger.error("Invalid index at pipeline expression : " + expression);
            return -1;
        }
    }
}
