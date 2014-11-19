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

    @Value("${TYPEAHEAD_API_URL}")
    private String              TYPEAHEAD_API_URL;

    @Value("${testcase.timeout}")
    private long                TestTimeout;

    @Autowired
    private TypeaheadController typeaheadController;

    private static Logger       logger = LoggerFactory.getLogger(TaTestExecuter.class);

    public List<TaTestCase> executeTests(List<TaTestCase> testList, int limit) {
        logger.debug(testList.size() + " tests recieved for execution with limit = " + limit);
        ExecutorService executerService = Executors.newCachedThreadPool();
        List<Future<TaTestCase>> futureList = new ArrayList<Future<TaTestCase>>();
        int ctr = 0;
        for (TaTestCase ttc : testList) {
            if (ctr >= limit) {
                break;
            }
            ttc.setTestUrl(TYPEAHEAD_API_URL + "?query=" + ttc.getQuery());
            futureList.add(executerService.submit(new CustomCallable(ttc)));
            ctr++;
        }

        for (Future<TaTestCase> future : futureList) {
            try {
                future.get(TestTimeout, TimeUnit.MILLISECONDS);
            }
            catch (TimeoutException e) {
                logger.error("Test case execution timed out.");
            }
            catch (InterruptedException | ExecutionException e) {
                logger.error("Test case execution failed or was interrupted.");
            }
        }

        executerService.shutdown();

        return testList;
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
                logger.error("Exception while executing testcase callable : " + taTestCase.getLogString(), ex);
            }
            if(mhsr.getStatus() == 404){
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
