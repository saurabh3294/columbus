package com.proptiger.columbus.typeahead;

import java.net.URI;
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
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.proptiger.columbus.model.Typeahead;
import com.proptiger.core.util.HttpRequestUtil;

@Component
public class TaTestExecuter {

    @Value("${BASE_URL}")
    private String          BASE_URL;

    @Value("${TYPEAHEAD_API_URL}")
    private String          TYPEAHEAD_API_URL;
    
    @Value("${testcase.timeout}")
    private long            TestTimeout;
    
    private static Logger   logger            = LoggerFactory.getLogger(TaTestExecuter.class);

    @Autowired
    private HttpRequestUtil httpRequestUtil;

    public List<TaTestCase> executeTests(List<TaTestCase> testList, int limit) {
        ExecutorService executerService = Executors.newCachedThreadPool();
        List<Future<TaTestCase>> futureList = new ArrayList<Future<TaTestCase>>();
        int ctr=0;
        for (TaTestCase ttc : testList) {
            if(ctr >= limit){
                break;
            }
            ttc.setTestUrl(BASE_URL + TYPEAHEAD_API_URL + "?query=" + ttc.getQuery());
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
        public TaTestCase call() throws Exception {
            URI uri = URI.create(UriComponentsBuilder.fromUriString(taTestCase.getTestUrl()).build().encode().toString());
            List<Typeahead> resultList = httpRequestUtil.getInternalApiResultAsTypeListFromCache(uri, Typeahead.class);
            taTestCase.setResults(resultList);
            return taTestCase;
        }
    }

}
