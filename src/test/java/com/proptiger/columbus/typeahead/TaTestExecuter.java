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

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.proptiger.columbus.model.Typeahead;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;

@Component
public class TaTestExecuter {

    private String          BASE_URL          = "";
    private String          TYPEAHEAD_API_URL = "";
    private long            TestTimeout       = 1000;

    private static Logger   logger            = LoggerFactory.getLogger(TaTestExecuter.class);

    @Autowired
    private HttpRequestUtil httpRequestUtil;

    @PostConstruct
    public void initialize() {
        BASE_URL = PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL);
        TYPEAHEAD_API_URL = PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL);
    }

    public List<TaTestCase> executeTests(List<TaTestCase> testList) {
        ExecutorService executerService = Executors.newCachedThreadPool();
        List<Future<TaTestCase>> futureList = new ArrayList<Future<TaTestCase>>();
        for (TaTestCase ttc : testList) {
            ttc.setTestUrl(BASE_URL + TYPEAHEAD_API_URL + "?q=" + ttc.getQuery());
            futureList.add(executerService.submit(new CustomCallable(ttc)));
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
