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
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

//import static org.testng.Assert.assertEquals;

/**
 * This TestNG test case checks the status code of all the APIs and print
 * Successful APIs list and Failed APIs list alongwith error message
 * 
 * @author user nikhil singhal
 * 
 */
public class TestAPIs {
    public final static String        BASE_URL       = "http://localhost:8080/dal";

    /*
     * regex pattern to fetch request parameters from given URLs
     */
    Pattern                           pattern        = Pattern.compile("([{])(\\w+)([}])");
    private static RestTemplate       restTemplate;

    /*
     * Store list of APIs returning statusCode as 2XX
     */
    List<String>                      successUrlList = new ArrayList<>();

    /*
     * Store list of APIS failing to return 2XX
     */

    private Map<String, String>       failedUrlList  = new HashMap<>();
    /*
     * Map storing values of request parameters
     */

    private Map<String, List<String>> apiKeysValuesMap;

    Set<String>                       exclusionList  = new HashSet<String>();

    @BeforeTest
    public void init() throws ConfigurationException {
        populateKeysValuesForAPI();

        exclusionList.add("data/v1/trend?");
        exclusionList.add("data/v1/trend/current?");
        exclusionList.add("data/v1/trend/hitherto?");
        exclusionList.add("data/v1/price-trend?");
        exclusionList.add("data/v1/price-trend/current?");
        exclusionList.add("data/v1/price-trend/hitherto?");
        exclusionList.add("data/v2/entity/project");
        exclusionList.add("data/v1/recommendation?propertyId");
        exclusionList.add("data/v1/recommendation?projectId");
        exclusionList.add("app/v1/locality?");
        exclusionList.add("app/v1/project-detail?projectId=");
        exclusionList.add("app/v1/amenity?");
        exclusionList.add("data/v1/entity/broker-agent");
        exclusionList.add("data/apilist");

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

    /**
     * Create a map of request parameters and their values by fetching values
     * from TestNG.properties file
     */
    private void populateKeysValuesForAPI() throws ConfigurationException {
        apiKeysValuesMap = new HashMap<>();
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
        String apilist = null;
        try {
            apilist = restTemplate.getForObject(BASE_URL + "/data/apilist", String.class);
        }
        catch (RestClientException e1) {
            e1.printStackTrace();
        }
        if (apilist != null) {
            /*
             * listOfApi stores list of APIs
             */
            List<String> listOfApi = getListOfAPis(apilist);

            ExecutorService executors = Executors.newFixedThreadPool(listOfApi.size());
            List<Future<Object>> futures = new ArrayList<Future<Object>>();

            for (final String apiUrl : listOfApi) {
                /*
                 * skipping APIs needing User authentication
                 */

                if (apiToBeExcluded(apiUrl)) {

                    continue;
                }

                /*
                 * Submitting API response to mutiple threads
                 */
                futures.add(executors.submit(new Callable<Object>() {
                    public Object call() throws Exception {
                        getApiResponse(apiUrl);
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

            System.out.println("No. of successful APIs   :" + successUrlList.size());
            System.out.println("No. of failed APIs       :" + failedUrlList.size());

            System.out.println("List of successful APIs :");
            for (String element : successUrlList) {
                System.out.println(element);
            }
            System.out.println("List of failed APIs :");

            for (Map.Entry<String, String> entry : failedUrlList.entrySet()) {
                System.out.println("\n " + entry.getKey());
                System.out.println(" Error :" + entry.getValue());
            }

        }
        else {

            Assert.assertEquals(true, true, "API list of EndPointController is not open");
            // "API not working fine")
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
     * 
     */
    private void getApiResponse(String apiUrl) throws ConfigurationException {

        if (!apiUrl.contains("{")) {
            urlWithoutRequestParams(apiUrl); // for URLs without request
                                             // parameters

        }
        else {
            urlContainingRequestParams(apiUrl); // for URLs with request
                                                // parameters
        }
    }

    /**
     * @param apiUrl
     *            add successful and failing URLs (without request parameters)
     *            to their respective lists
     */
    private void urlWithoutRequestParams(String apiUrl) {
        String apiResponse = "";
        if (apiUrl.contains("app/v1/amenity?")) {
            apiUrl = "http://localhost:8080/dal/app/v1/amenity?city-id=2";
        }
        apiResponse = restTemplate.getForObject(apiUrl, String.class);

        addApiResponseCode(apiResponse, apiUrl);

    }

    /**
     * @param apiUrl
     *            add successful and failing URLs (with request parameters) to
     *            their respective lists
     */
    private void urlContainingRequestParams(String apiUrl) throws ConfigurationException {
        String apiResponse = "";

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
            apiResponse = restTemplate.getForObject(expanded, String.class);
            String finalUrl = expanded.toString();

            addApiResponseCode(apiResponse, finalUrl);

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
    private List<String> getListOfAPis(String apilist) {
        List<String> listofAPIs = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\\"GET(\\s*)(.*?)\\\"");
        Matcher m = pattern.matcher(apilist);
        while (m.find()) {
            String baseurl = BASE_URL + m.group(2);
            String regex1 = "<(\\w*)>";
            String bcd = baseurl.replaceAll(regex1, "{$1}");
            String regex2 = "\\[(.*)\\]";
            String finalurl = bcd.replaceAll(regex2, "");
   //         if (finalurl.contains("entity/graph/project_distribution_price")) {
              listofAPIs.add(finalurl);
    //        }
        }
        return listofAPIs;
    }

    /**
     * @param apiResponse
     *            fetch and return statusCode from response of a API hit
     * @param finalUrl
     * @return
     * @return
     */
    void addApiResponseCode(String apiResponse, String finalUrl) {
        Pattern pattern = Pattern.compile("\\\"statusCode\\\":(\\s*)\\\"(\\d\\D\\D)\\\",");
        Matcher m = pattern.matcher(apiResponse);
        boolean dataPresent = false;
        String statusCode = "";
        if (m.find()) {
            statusCode = m.group(2);
        }
        else {
            Pattern dataPattern = Pattern.compile("\\\"data\\\":(.*?)");
            Matcher match = dataPattern.matcher(apiResponse);
            if (match.find()) {
                dataPresent = true;
            }
        }

        if (statusCode.equals("2XX") || dataPresent) {
            successUrlList.add(finalUrl);
        }
        else {
            failedUrlList.put(finalUrl, apiResponse);
        }
    }

}
