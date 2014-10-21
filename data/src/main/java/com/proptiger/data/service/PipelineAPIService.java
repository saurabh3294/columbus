package com.proptiger.data.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.proptiger.core.constants.ResponseCodes;
import com.proptiger.core.constants.ResponseErrorMessages;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.util.URLUtil;

@Service
public class PipelineAPIService {

    private static Logger       logger            = LoggerFactory.getLogger(PipelineAPIService.class);

    private static String       delimIncludeParam = ",";
    private static String       includeParamAll   = "all";
    private static String       includeParamNone  = "last";

    @Value("${composite.api.base.url}")
    private String              BASE_URL;

    private RestTemplate        restTemplate      = new RestTemplate();

    /* kwe = keyword-expression */
    private String              kweOuterDlim      = ".";
    private String              kweInnerDlimStart = "[";
    private String              kweInnerDlimEnd   = "]";
    private String              kweStartKeyword   = "api";

    /* utp = url-template-param */
    private String              utpDelimStart     = "<";
    private String              utpDelimEnd       = ">";

    public Map<String, Object> getResponseForApis(List<String> urlList, String include) {
        List<Object> apiResultList = new ArrayList<Object>();
        String parsedUrl = null;
        for (String url : urlList) {
            parsedUrl = buildApiUrl(url, apiResultList);
            apiResultList.add(getApiResult(parsedUrl));
        }
        return (consilidateResultsToMap(urlList, apiResultList, include));
    }

    private Map<String, Object> consilidateResultsToMap(List<String> urlList, List<Object> apiResultList, String include) {
        Map<String, Object> resultMap;
        if (include == null || include.equalsIgnoreCase(includeParamNone)) {
            resultMap = new HashMap<String, Object>();
            resultMap.put(urlList.get(urlList.size() - 1), apiResultList.get(apiResultList.size() - 1));
        }
        else if (include.equalsIgnoreCase(includeParamAll)) {
            resultMap = makeMapFromKeyValueLists(urlList, apiResultList, null);
        }
        else {
            List<Integer> resultIncludeList = getIntegerListFromDelimSeparatedString(include, delimIncludeParam);
            resultMap = makeMapFromKeyValueLists(urlList, apiResultList, resultIncludeList);
        }
        return resultMap;
    }

    private Object getApiResult(String apiUrl) {
        try {
            URI uri = URLUtil.getEncodedURIObject(apiUrl, BASE_URL);
            Object apiResult = restTemplate.getForObject(uri, Object.class);
            return apiResult;
        }
        catch (Exception ex) {
            return (new APIResponse(ResponseCodes.INTERNAL_SERVER_ERROR, ResponseErrorMessages.SOME_ERROR_OCCURED));
        }
    }

    /**
     * @param templateUrl
     *            e.g. /data/v1/trend/hitherto?filters=(cityId=ge=<expression1>;
     *            cityId=le=<expression2>)&fields=.....
     * @param apiResultList
     * @return
     */
    private String buildApiUrl(String templateUrl, List<Object> apiResultList) {

        String finalUrl = templateUrl;
        String[] expressions = StringUtils.substringsBetween(templateUrl, utpDelimStart, utpDelimEnd);
        if (expressions == null) {
            return templateUrl;
        }
        String value = null;
        for (String exp : expressions) {
            value = evaluateParamExpression(exp, apiResultList);
            finalUrl = StringUtils.replace(finalUrl, (utpDelimStart + exp + utpDelimEnd), value);
        }
        return finalUrl;
    }

    /**
     * @param expression
     *            api1.data[i1].fieldName1[i2].fieldName3.fieldName4[i3]....
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
        try {
            for (String key : outerKeys) {
                index = extractArrayIndexFromExpression(key);

                if (index == -1) { // is a Map
                    value = map.get(key);
                }
                else { // is an array of Maps
                    key = StringUtils.split(key, kweInnerDlimStart)[0];
                    value = ((List<Map<String, Object>>) (map.get(key))).get(index);
                }
                map = (Map<String, Object>) value;
            }
        }
        catch (Exception ex) {
            logger.error("Invalid index at pipeline expression : " + expression);
        }

        return String.valueOf(value);
    }

    /** Utility methods **/

    private List<Integer> getIntegerListFromDelimSeparatedString(String line, String dlim) {
        List<Integer> paramListInteger = new ArrayList<Integer>();

        if (line == null || dlim == null) {
            return null;
        }

        String[] paramList = StringUtils.split(line, dlim);
        for (String param : paramList) {
            paramListInteger.add(Integer.parseInt(param));
        }
        return paramListInteger;
    }

    /**
     * @param keyList
     *            list-of-strings to be used as keys
     * @param valueList
     *            list-of-objects to be used as values
     * @param selectiveInclusionList
     *            list-of-indices which need to be included in the map
     * @return map<String, Object> containing keys from keyList and values form
     *         valueList only includes the keys at indices in includeList
     */
    private Map<String, Object> makeMapFromKeyValueLists(
            List<String> keyList,
            List<Object> valueList,
            List<Integer> selectiveInclusionList) {
        if (keyList == null || valueList == null) {
            return null;
        }

        boolean includeAll = ((selectiveInclusionList == null) ? true : false);

        if (keyList.size() != valueList.size()) {
            logger.error("Count mismatch between keyList and valueList : " + keyList.size() + ", " + valueList.size());
            return null;
        }

        Map<String, Object> resultMap = new HashMap<String, Object>();
        int size = keyList.size();
        for (int i = 0; i < size; i++) {
            if (includeAll || selectiveInclusionList.contains(i)) {
                resultMap.put(keyList.get(i), valueList.get(i));
            }
        }

        return resultMap;
    }

    /**
     * @param expression
     *            "<keyword>[<index>]" eg. api[0]
     * @return index or '-1' if something goes wrong. eg.
     *         extractArrayIndexFromExpression("data[5]") = 5
     *         extractArrayIndexFromExpression("data") = -1
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
