package com.proptiger.columbus.typeahead;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import junit.framework.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.proptiger.columbus.mvc.TypeaheadController;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.pojo.response.APIResponse;

@Component
public class TaTestExecuter {

    @Value("${typeahead.api.url.pattern}")
    private String              TYPEAHEAD_API_URL_PATTERN;

    @Value("${test.testcase.timeout}")
    private long                TestTimeout;

    @Autowired
    private TypeaheadController typeaheadController;

    private static Logger       logger           = LoggerFactory.getLogger(TaTestExecuter.class);

    private final String        URL_PARAM_FORMAT = "&%s=%s";

    @Value("${test.default.typeahead.version}")
    private String              defaultVersion;

    /**
     * Runs top 'limit' number of test-cases from 'testList'.
     * 
     * @param testList
     *            list of test cases to run
     * @param limit
     *            number of test cases to run
     * @param apiVersion
     * @return A sublist of 'testList' with results field populated
     */
    public List<TaTestCase> executeTests(List<TaTestCase> testList, int limit, String apiVersion) {
        logger.info(testList.size() + " tests recieved for execution with limit = " + limit);
        limit = Math.min(limit, testList.size());
        List<TaTestCase> testListLimited = testList.subList(0, limit);

        ExecutorService executerService = Executors.newFixedThreadPool(20);
        List<Future<TaTestCase>> futureList = new ArrayList<Future<TaTestCase>>();
        for (TaTestCase ttc : testListLimited) {
            ttc.setTestUrl(getTypeaheadTestUrl(ttc, apiVersion));
            futureList.add(executerService.submit(new CustomCallable(ttc)));
        }

        for (Future<TaTestCase> future : futureList) {
            try {
                future.get(TestTimeout, TimeUnit.MILLISECONDS);
            }
            catch (TimeoutException e1) {
                logger.error("Some test case execution timed out. Moving On.", e1);
            }
            catch (InterruptedException | ExecutionException e2) {
                logger.error("Some test case execution failed or was interrupted. Moving On.", e2);
            }
        }
        executerService.shutdown();
        return testListLimited;
    }

    public void assertNonNullResponse(String query) {
        List<Typeahead> typeaheads = getTestResult(getTypeAheadTestUrl(query, defaultVersion));
        Assert.assertNotNull(typeaheads);
    }

    public String getTypeaheadTestUrl(TaTestCase ttc, String apiVersion) {
        String testCaseUrl = getTypeAheadTestUrl(ttc.getQuery(), apiVersion);
        Map<String, String> urlParams = ttc.getUrlParams();
        if (urlParams == null || urlParams.size() == 0) {
            return testCaseUrl;
        }
        for (Entry<String, String> entry : urlParams.entrySet()) {
            testCaseUrl += String.format(URL_PARAM_FORMAT, entry.getKey(), entry.getValue());
        }
        return testCaseUrl;
    }

    public String getTypeAheadTestUrl(String testQuery, String apiVersion) {
        return String.format(TYPEAHEAD_API_URL_PATTERN, apiVersion) + "?query=" + testQuery;
    }

    class CustomCallable implements Callable<TaTestCase> {

        private TaTestCase taTestCase;

        public CustomCallable(TaTestCase taTestCase) {
            super();
            this.taTestCase = taTestCase;
        }

        @Override
        public TaTestCase call() {
            taTestCase.setResults(getTestResult(taTestCase.getTestUrl()));
            return taTestCase;
        }
    }

    public List<Typeahead> getTestResult(String url) {
        List<Typeahead> resultList = null;
        MockHttpServletResponse mhsr = null;
        String response = null;
        try {
            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(typeaheadController).build();
            mhsr = mockMvc.perform(MockMvcRequestBuilders.get(url)).andReturn().getResponse();
            response = mhsr.getContentAsString();
        }
        catch (Exception ex) {
            logger.error("Exception while executing testcase callable : " + " Moving On.", ex);
        }
        if (mhsr.getStatus() == 404) {
            logger.error("Problem executing testcase : ", "Invalid Url : Status = 404");
        }
        if (response != null && !response.isEmpty()) {
            /* Parsing Json Response */
            Gson gson = new Gson();
            APIResponse apiResponse = gson.fromJson(response, APIResponse.class);
            Object data = apiResponse.getData();
            resultList = gson.fromJson(gson.toJson(data), new TypeToken<List<Typeahead>>() {}.getType());
        }
        return resultList;
    }
}