package com.proptiger.columbus.typeahead;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
import com.proptiger.columbus.model.Typeahead;
import com.proptiger.columbus.mvc.TypeaheadController;
import com.proptiger.core.pojo.response.APIResponse;

@Component
public class TaTestExecuter {

    @Value("${typeahead.api.url}")
    private String              TYPEAHEAD_API_URL;

    @Value("${test.testcase.timeout}")
    private long                TestTimeout;

    @Autowired
    private TypeaheadController typeaheadController;

    private static Logger       logger = LoggerFactory.getLogger(TaTestExecuter.class);

    /**
     * Runs top 'limit' number of test-cases from 'testList'.
     * 
     * @param testList
     *            list of test cases to run
     * @param limit
     *            number of test cases to run
     * @return A sublist of 'testList' with results field populated
     */
    public List<TaTestCase> executeTests(List<TaTestCase> testList, int limit) {
        logger.info(testList.size() + " tests recieved for execution with limit = " + limit);
        limit = Math.min(limit, testList.size());
        List<TaTestCase> testListLimited = testList.subList(0, limit);

        ExecutorService executerService = Executors.newFixedThreadPool(20);
        List<Future<TaTestCase>> futureList = new ArrayList<Future<TaTestCase>>();
        for (TaTestCase ttc : testListLimited) {
            ttc.setTestUrl(TYPEAHEAD_API_URL + "?query=" + ttc.getQuery());
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

    class CustomCallable implements Callable<TaTestCase> {

        private TaTestCase taTestCase;

        public CustomCallable(TaTestCase taTestCase) {
            super();
            this.taTestCase = taTestCase;
        }

        @Override
        public TaTestCase call() {
            List<Typeahead> resultList = null;
            MockHttpServletResponse mhsr = null;
            String response = null;
            String url = taTestCase.getTestUrl();
            try {
                MockMvc mockMvc = MockMvcBuilders.standaloneSetup(typeaheadController).build();
                mhsr = mockMvc.perform(MockMvcRequestBuilders.get(url)).andReturn().getResponse();
                response = mhsr.getContentAsString();
            }
            catch (Exception ex) {
                logger.error("Exception while executing testcase callable : " + taTestCase.getLogString() + " Moving On." , ex);
            }
            if (mhsr.getStatus() == 404) {
                logger.error("Problem executing testcase : " + taTestCase.getLogString(), "Invalid Url : Status = 404");
            }
            if (response == null || response.isEmpty()) {
                return taTestCase;
            }
            /* Parsing Json Response */
            Gson gson = new Gson();
            APIResponse apiResponse = gson.fromJson(response, APIResponse.class);
            Object data = apiResponse.getData();
            resultList = gson.fromJson(gson.toJson(data), new TypeToken<List<Typeahead>>() {}.getType());
            taTestCase.setResults(resultList);
            return taTestCase;
        }
    }

}
