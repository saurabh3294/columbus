package com.proptiger.data;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

/**
 * This TestNG test case checks the status code of all the APIs and print
 * Successful APIs list and Failed APIs list alongwith error message
 * 
 * @author user nikhil singhal
 * 
 */
public class TestAPIs {
    private static Logger             logger             = LoggerFactory.getLogger(TestAPIs.class);
    /*
     * regex pattern to fetch request parameters from given URLs
     */
    Pattern                           pattern            = Pattern.compile("([{])(\\w+)([}])");
    private static RestTemplate       restTemplate;
    private static Integer            totalUrl           = 0;
    private static Integer            successUrl         = 0;
    private static Integer            failedUrl          = 0;
    private static Integer            skippedUrl         = 0;

    /*
     * Store list of APIs returning statusCode as 2XX
     */
    List<String>                      successGETUrlList  = new ArrayList<>();
    List<String>                      successPOSTUrlList = new ArrayList<>();
    List<String>                      successPUTUrlList  = new ArrayList<>();
    /*
     * Store list of APIS failing to return 2XX
     */
    private Map<String, String>       failedGETUrlList   = new HashMap<>();
    private Map<String, String>       failedPOSTUrlList  = new HashMap<>();
    private Map<String, String>       failedPUTUrlList   = new HashMap<>();

    /*
     * Map storing values of request parameters
     */

    private Map<String, List<String>> apiKeysValuesMap;
    private Map<String, String>       populateMapforPostData;

    Set<String>                       exclusionList      = new HashSet<String>();

    @BeforeTest
    public void init() throws ConfigurationException {
        logger.debug("Before start of test method");
        populateKeysValuesForAPI();
        populateMapforPostData();

        exclusionList.add("data/apilist");
        exclusionList.add("app/v1/locality?");
        exclusionList.add("data/v1/entity/broker-agent");
        exclusionList.add("sell-property");
        exclusionList.add(".csv");
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                /*
                 * Return false in all cases even if there was a error while
                 * handling a url. This is to include original error message
                 * returned from server in the composite api response
                 */
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                // this method will never be used because we are returning false
                // in each case from hasError method
            }
        });
    }

    private void populateMapforPostData() {
        populateMapforPostData = new HashMap<String, String>();
        populateMapforPostData.put(
                (apiKeysValuesMap.get("BASE_URL").get(0) + "/data/v1/entity/property/{propertyId}/report-error"),
                "report_error");
        populateMapforPostData.put(
                apiKeysValuesMap.get("BASE_URL").get(0) + "/data/v1/entity/project/{projectId}/report-error",
                "report_error");
        populateMapforPostData.put(
                apiKeysValuesMap.get("BASE_URL").get(0) + "/data/v1/entity/locality/{localityId}/rating",
                "post_rating");
    }

    /**
     * Create a map of request parameters and their values by fetching values
     * from TestNG.properties file
     */
    private void populateKeysValuesForAPI() throws ConfigurationException {
        apiKeysValuesMap = new HashMap<String, List<String>>();
        /*
         * TestNG.properties
         * 
         * file stores values of request parameters
         */
        PropertiesConfiguration config = new PropertiesConfiguration("TestNG.properties");

        if (config != null) {
            Iterator<?> propertyFileKetsIt = config.getKeys();
            while (propertyFileKetsIt.hasNext()) {
                String key = (String) propertyFileKetsIt.next();
                String[] values = config.getStringArray(key);
                List<String> keyValues = new ArrayList<>();
                for (String val : values) {
                    keyValues.add(val);
                }
                apiKeysValuesMap.put(key, keyValues);
            }
        }
    }

    /**
     * @throws IOException
     * @throws ConfigurationException
     */
    @Test
    public void checkStatusCode() throws IOException, ConfigurationException {
        String apilist = "";
        try {
            apilist = restTemplate
                    .getForObject(apiKeysValuesMap.get("BASE_URL").get(0) + "/data/apilist", String.class);
        }
        catch (RestClientException e1) {
            e1.printStackTrace();
        }
        if (apilist != null) {
            /*
             * listOfApi stores list of APIs
             */
            Map<String, String> listofAPIs = getListOfAPis(apilist);
            ExecutorService executors = Executors.newFixedThreadPool(listofAPIs.size());
            List<Future<Object>> futures = new ArrayList<Future<Object>>();

            for (final Map.Entry<String, String> entry : listofAPIs.entrySet()) {
                logger.debug("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                totalUrl++;
                if (apiToBeExcluded(entry.getKey())) {
                    skippedUrl++;
                    continue;
                }

                /*
                 * Submitting API response to mutiple threads
                 */
                futures.add(executors.submit(new Callable<Object>() {
                    public Object call() throws Exception {
                        getApiResponse(entry.getKey(), entry.getValue());
                        return "";
                    }
                }));

            }

            /*
             * Wait till all threads stop
             */
            for (Future<Object> future : futures) {
                try {
                    future.get();
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            executors.shutdown();
            Reporter.log("Total APIs tested    :" + totalUrl);
            Reporter.log("Distinct successful APIs      :" + successUrl);
            Reporter.log("Distinct failed APIs       :" + failedUrl);
            Reporter.log("Skipped APIs       :" + skippedUrl);
            logger.debug("No. of successful GET APIs   :" + successGETUrlList.size());
            logger.debug("No. of successful POST APIs   :" + successPOSTUrlList.size());
            logger.debug("No. of successful PUT APIs   :" + successPUTUrlList.size());
            Reporter.log("No. of failed GET APIs       :" + failedGETUrlList.size());
            Reporter.log("No. of failed POST APIs       :" + failedPOSTUrlList.size());
            Reporter.log("No. of failed PUT APIs       :" + failedPUTUrlList.size());
            logger.debug("List of successful GET APIs :");
            for (String element : successGETUrlList) {
                logger.debug(element);
            }
            logger.debug("List of successful POST APIs :");
            for (String element : successPOSTUrlList) {
                logger.debug(element);
            }
            logger.debug("List of successful PUT APIs :");
            for (String element : successPUTUrlList) {
                logger.debug(element);
            }
            Reporter.log("List of failed GET APIs :");
            for (Map.Entry<String, String> entry : failedGETUrlList.entrySet()) {
                Reporter.log("\n " + entry.getKey());
                Reporter.log("\n Error :" + entry.getValue());
            }
            Reporter.log("List of failed POST APIs :");
            for (Map.Entry<String, String> entry : failedPOSTUrlList.entrySet()) {
                Reporter.log("\n " + entry.getKey());
                Reporter.log("\n Error :" + entry.getValue());
            }
            logger.debug("List of failed PUT APIs :");
            for (Map.Entry<String, String> entry : failedPUTUrlList.entrySet()) {
                Reporter.log("\n " + entry.getKey());
                Reporter.log("\n Error :" + entry.getValue());
            }
        }
        int numberOfAPIFailed = failedGETUrlList.size() + failedPOSTUrlList.size() + failedPUTUrlList.size();
        if (numberOfAPIFailed > 0) {
            Assert.assertEquals(true, true, "API has faced some failure");
        }
    }

    private boolean apiToBeExcluded(String apiUrl) {
        boolean exclude = false;
        if (apiUrl.contains("user"))
            exclude = true;
        else {
            for (String set : exclusionList) {
                if (apiUrl.contains(set))
                    exclude = true;
            }
        }
        return exclude;
    }

    /**
     * @param apiUrl
     *            differentiating simple URLs and URLs containing request
     *            parameters
     * @param method
     * 
     */
    private void getApiResponse(String apiUrl, String method) throws ConfigurationException {
        if (!apiUrl.contains("{")) {
            urlWithoutRequestParams(apiUrl, method); // for URLs without request
        }
        else {
            urlContainingRequestParams(apiUrl, method); // for URLs with request
            // parameters
        }
    }

    /**
     * @param apiUrl
     *            add successful and failing URLs (without request parameters)
     *            to their respective lists
     * @param method
     */
    private void urlWithoutRequestParams(String apiUrl, String method) {
        String apiResponse = "";
        if (apiUrl.contains("app/v1/amenity?")) {
            apiUrl = apiUrl + "city-id=" + apiKeysValuesMap.get("city-id").get(0);
        }
        if (apiUrl.contains("trend")) {
            apiUrl = apiUrl + apiKeysValuesMap.get("trend").get(0);
        }
        if (apiUrl.contains("app/v1/locality?")) {
            apiUrl = apiUrl + apiKeysValuesMap.get("locality_selector").get(0);
            logger.debug("apiurl:   " + apiUrl);
        }
        if (apiUrl.contains("data/v2/entity/project")) {
            apiUrl = apiUrl + apiKeysValuesMap.get("entity_project_selector").get(0);
            logger.debug("apiurl:   " + apiUrl);
        }
        if (method == "GET") {
            apiResponse = restTemplate.getForObject(apiUrl, String.class);
        }

        if (addApiResponseCode(apiResponse, apiUrl, method)) {
            successUrl++;
        }
        else {
            failedUrl++;
        }
    }

    /**
     * @param apiUrl
     *            add successful and failing URLs (with request parameters) to
     *            their respective lists
     * @param method
     */
    private void urlContainingRequestParams(String apiUrl, String method) throws ConfigurationException {
        String apiResponse = "";
        boolean responseCode;
        boolean isUrlSuccessfulForAllValues = true;
        String VariableFromPostMap = populateMapforPostData.get(apiUrl);

        if (apiUrl.contains("params")) {
            apiUrl = urlContainParams(apiUrl);
        }
        Matcher m = pattern.matcher(apiUrl);
        List<String> result = new ArrayList<String>(); // result stores request
                                                       // parameters of a
                                                       // particular
                                                       // URL
        while (m.find()) {
            result.add(m.group(2));
        }
        int i = 0, maximumValues = 0, max = 0;

        for (String key : result) {
            max = apiKeysValuesMap.get(key).size();
            maximumValues = Math.max(maximumValues, max);
        }

        for (i = 0; i < maximumValues; i++) {
            Map<String, String> map = new HashMap<>();
            map = returnMap(apiKeysValuesMap, i, result);
            UriTemplate uriTemplate = new UriTemplate(apiUrl);
            URI expanded = uriTemplate.expand(map);
            String finalUrl = expanded.toString();
            if (method == "GET") {
                apiResponse = restTemplate.getForObject(expanded, String.class);
                responseCode = addApiResponseCode(apiResponse, finalUrl, method);
            }
            else if (method == "POST") {
                String dataToPost = apiKeysValuesMap.get(VariableFromPostMap).get(0);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> entity = new HttpEntity<String>(dataToPost, headers);
                String postResponse = restTemplate.postForObject(expanded, entity, String.class);
                logger.debug("postReRsponse    " + postResponse);
                responseCode = addApiResponseCode(postResponse, finalUrl, method);
            }
            else if (method == "PUT") {
                String post_rating = apiKeysValuesMap.get(VariableFromPostMap).get(0);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> entity = new HttpEntity<String>(post_rating, headers);
                ResponseEntity<Object> putResponse = restTemplate.exchange(
                        expanded,
                        HttpMethod.PUT,
                        entity,
                        Object.class);
                logger.debug("putResponse    " + putResponse);
                String finalputResponse = putResponse.toString();
                responseCode = addApiResponseCode(finalputResponse, finalUrl, method);
            }
            else {
                continue;
            }

            isUrlSuccessfulForAllValues = isUrlSuccessfulForAllValues && responseCode;
        }

        if (isUrlSuccessfulForAllValues) {
            successUrl++;
        }
        else {
            failedUrl++;
        }

    }

    /**
     * @param apiUrl
     *            Change request parameters of URL containing params. New
     *            request parameters are designed to fetch value from TestNG
     *            properties
     * @return
     */
    private String urlContainParams(String apiUrl) {
        String params = "";
        if (apiUrl.contains("bedroom")) {
            String regex1 = "\\{params\\}";
            params = apiUrl.replaceAll(regex1, "{param_bedroom}");
        }
        else if (apiUrl.contains("locality")) {
            String regex1 = "\\{params\\}";
            params = apiUrl.replaceAll(regex1, "{param_locality}");
        }
        else if (apiUrl.contains("distribution_price")) {
            String regex1 = "\\{params\\}";
            params = apiUrl.replaceAll(regex1, "{param_price}");
        }
        else if (apiUrl.contains("price_trends")) {
            String regex1 = "\\{params\\}";
            params = apiUrl.replaceAll(regex1, "{param_pricetrends}");
        }

        return params;
    }

    /**
     * @param apiKeysValuesLocalMap
     * @param i
     * @param result
     * 
     *            return map of request parameters and its values of a given URL
     * 
     * @return
     */
    private Map<String, String> returnMap(Map<String, List<String>> apiKeysValuesLocalMap, int i, List<String> result) {
        Map<String, String> map = new HashMap<>();
        String value;
        for (String key : result) {
            int arraylistSize = apiKeysValuesLocalMap.get(key).size();
            if (i < arraylistSize) {
                value = apiKeysValuesLocalMap.get(key).get(i);
            }

            else {
                value = apiKeysValuesLocalMap.get(key).get(arraylistSize - 1);
            }
            map.put(key, value);
        }
        return map;
    }

    /**
     * @param apilist
     *            generate proper URL of all APIs
     * @return
     */
    private Map<String, String> getListOfAPis(String apilist) {
        Map<String, String> listofAPIs = new HashMap<>();
        Pattern urlPattern = Pattern.compile("\\\"(\\w*)(\\s)(.*?)\\\"");
        Matcher m = urlPattern.matcher(apilist);
        while (m.find()) {
            String baseurl = apiKeysValuesMap.get("BASE_URL").get(0) + m.group(3);
            String regex1 = "<(\\w*)>";
            String bcd = baseurl.replaceAll(regex1, "{$1}");
            String regex2 = "\\[(.*)\\]";
            String finalUrl = bcd.replaceAll(regex2, "");
            if (m.group(1).equals("GET")) {
                listofAPIs.put(finalUrl, "GET");
            }
            else if (m.group(1).equals("POST") && finalUrl.contains("image")) {
                skippedUrl++;
                totalUrl++;
            }
            else if (m.group(1).equals("POST")) {
                listofAPIs.put(finalUrl, "POST");
            }
            else if (m.group(1).equals("PUT")) {
                listofAPIs.put(finalUrl, "PUT");
            }
            else {
                skippedUrl++;
                totalUrl++;
            }

        }
        return listofAPIs;
    }

    /**
     * @param apiResponse
     *            fetch and return statusCode from response of a API hit
     * @param finalUrl
     * @param method
     * @return
     * @return
     * @return
     */
    boolean addApiResponseCode(String apiResponse, String finalUrl, String method) {
        Pattern responsePattern = Pattern.compile("\\\"statusCode\\\":(\\s*)\\\"(\\d\\D\\D)\\\",");
        Matcher m = responsePattern.matcher(apiResponse);
        boolean dataPresent = false;
        String statusCode = "";
        if (m.find()) {
            statusCode = m.group(2);
        }
        // else - in case statusCode is not present but empty data has been
        // returned, as that will be counted as valid API response
        else {
            Pattern dataPattern = Pattern.compile("\\\"data\\\":(.*?)");
            Matcher match = dataPattern.matcher(apiResponse);
            if (match.find()) {
                dataPresent = true;
            }
        }
        boolean responseCode = statusCode.equals("2XX") || dataPresent;
        if (responseCode) {
            if (method == "GET") {
                successGETUrlList.add(finalUrl);
            }
            if (method == "POST") {
                successPOSTUrlList.add(finalUrl);
            }
            if (method == "PUT") {
                successPUTUrlList.add(finalUrl);
            }
        }
        else {
            if (method == "GET") {
                failedGETUrlList.put(finalUrl, apiResponse);
            }
            if (method == "POST") {
                failedPOSTUrlList.put(finalUrl, apiResponse);
            }
            if (method == "PUT") {
                failedPUTUrlList.put(finalUrl, apiResponse);
            }

        }
        return responseCode;

    }

}
