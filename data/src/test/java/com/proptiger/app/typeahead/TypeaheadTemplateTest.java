package com.proptiger.app.typeahead;

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

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.proptiger.app.typeahead.suggestions.NLPSuggestionHandler;
import com.proptiger.app.typeahead.thandlers.TemplateTypes;
import com.proptiger.data.model.Typeahead;
import com.proptiger.data.service.AbstractTest;
import com.proptiger.data.util.URLUtil;

public class TypeaheadTemplateTest extends AbstractTest {

    @Autowired
    private NLPSuggestionHandler nlpSuggestionHandler;

    private RestTemplate         restTemplate       = new RestTemplate();

    private static Logger        logger             = LoggerFactory.getLogger(TypeaheadTemplateTest.class);

    private long                 UrlResponseTimeout = 30000l;

    /* TODO :: Pick this up from config file */
    private String               BASE_URL           = "http://beta.proptiger-ws.com/";
    private String               testCity           = "Noida";

    @Test (enabled = false)
    public void TypeaheadTemplateValidURLTest() {

        List<Typeahead> taResults = new ArrayList<Typeahead>();
        for (TemplateTypes ttype : TemplateTypes.values()) {
            taResults.addAll(nlpSuggestionHandler.getNlpTemplateBasedResults(ttype.getText(), testCity, 5));
        }

        UrlValidator urlValidator = new UrlValidator();
        ExecutorService executors = Executors.newFixedThreadPool(taResults.size());
        List<Future<Object>> futures = new ArrayList<Future<Object>>();
        for (final Typeahead t : taResults) {

            /* Check if URL has a valid syntax */
            Assert.assertTrue(
                    urlValidator.isValid(BASE_URL + t.getRedirectUrl()),
                    "Typeahead Template Suggestion generated an invalid URL.");

            /* Check if URL returns a valid HTTP status */
            futures.add(executors.submit(new Callable<Object>() {
                public Object call() throws Exception {
                    HttpStatus httpStatus = getURLResponseStatus(t.getRedirectUrl());
                    TypeaheadUrlValidityResult tuvResult = new TypeaheadUrlValidityResult(t, httpStatus);
                    logger.debug(tuvResult.getLogString() + " ::: Status = " + httpStatus.name());
                    return tuvResult;
                }
            }));
        }

        TypeaheadUrlValidityResult tuvResult;
        for (Future<Object> future : futures) {
            try {
                tuvResult = (TypeaheadUrlValidityResult) (future.get(UrlResponseTimeout, TimeUnit.MILLISECONDS));
                Assert.assertEquals(
                        tuvResult.httpStatus,
                        HttpStatus.OK,
                        "Typeahead Template Suggestion generated an invalid URL." + tuvResult.getLogString());
            }
            catch (TimeoutException e) {
                Assert.assertTrue(false, "URL Execution timed out.");
            }
            catch (InterruptedException | ExecutionException e) {
                Assert.assertTrue(false, "URL Execution failed or was interrupted.");
            }
        }
    }

    private HttpStatus getURLResponseStatus(String url) {
        try {
            URI uri = URLUtil.getEncodedURIObject(url, BASE_URL);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
            return (response.getStatusCode());
        }
        catch (Exception ex) {
            return null;
        }
    }

    private class TypeaheadUrlValidityResult {
        public Typeahead  typeahead;
        public HttpStatus httpStatus;

        public TypeaheadUrlValidityResult(Typeahead typeahead, HttpStatus httpStatus) {
            this.typeahead = typeahead;
            this.httpStatus = httpStatus;
        }

        public String getLogString() {
            String msg = "Template Details = [" + this.typeahead.getId()
                    + ", "
                    + this.typeahead.getDisplayText()
                    + ", "
                    + this.typeahead.getRedirectUrl()
                    + "]";
            return msg;
        }
    }

}
