package com.proptiger.columbus.thandlers;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.proptiger.columbus.model.TypeaheadConstants;
import com.proptiger.columbus.service.AbstractTest;
import com.proptiger.columbus.service.TypeaheadService;
import com.proptiger.columbus.suggestions.EntitySuggestionHandler;
import com.proptiger.core.enums.seo.ValidURLResponse;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.URLUtil;

@Component
@Test(singleThreaded = true)
public class TypeaheadSuggestionURLTest extends AbstractTest {

    private static Logger   logger    = LoggerFactory.getLogger(TypeaheadTemplateTest.class);

    @Autowired
    TypeaheadService        typeaheadService;

    @Autowired
    EntitySuggestionHandler entitySuggestionHandler;

    @Autowired
    private HttpRequestUtil httpRequestUtil;

    @Value("${proptiger.url}")
    private String          BASE_URL;

    @Value("${url.validation.api.url}")
    private String          urlValidationApiURL;

    private static int      suggCount = 10;

    @Test(enabled = true)
    public void testURLCitySuggestion() {

        logger.info("TEST NAME = URL CITY SUGGESTION");
        List<Typeahead> results = typeaheadService.getTypeaheadsV4(
                "gurgaon",
                1,
                new HashMap<String, String>(),
                null,
                null);
        List<Typeahead> suggestions = entitySuggestionHandler.getEntityBasedSuggestions(results, suggCount);
        logger.info("Suggestions recieved = " + suggestions.toString());
        testRedirectUrlsValidity(suggestions);
    }

    @Test(enabled = true)
    public void testURLLocalitySuggestion() {

        logger.info("TEST NAME = URL LOCALITY SUGGESTION");
        List<Typeahead> results = typeaheadService.getTypeaheadsV4("50186", 1, null, null, null);
        setDummyProjectCounts(results.get(0));
        List<Typeahead> suggestions = entitySuggestionHandler.getEntityBasedSuggestions(results, suggCount);
        logger.info("Suggestions recieved = " + suggestions.toString());
        Assert.assertEquals(suggestions.size(), 5);
        testRedirectUrlsValidity(suggestions);
    }

    @Test(enabled = true)
    public void testURLSuburbSuggestion() {

        logger.info("TEST NAME = URL SUBURB SUGGESTION");
        List<Typeahead> results = typeaheadService.getTypeaheadsV4("10512", 1, null, null, null);
        setDummyProjectCounts(results.get(0));
        List<Typeahead> suggestions = entitySuggestionHandler.getEntityBasedSuggestions(results, suggCount);
        logger.info("Suggestions recieved = " + suggestions.toString());
        Assert.assertEquals(suggestions.size(), 5);
        testRedirectUrlsValidity(suggestions);
    }

    @Test(enabled = true)
    public void testURLBuilderSuggestion() {

        logger.info("TEST NAME = URL BUILDER SUGGESTION");
        List<Typeahead> results = typeaheadService.getTypeaheadsV4("100002", 1, null, null, null);
        setDummyProjectCounts(results.get(0));
        List<Typeahead> suggestions = entitySuggestionHandler.getEntityBasedSuggestions(results, suggCount);
        logger.info("Suggestions recieved = " + suggestions.toString());
        Assert.assertEquals(suggestions.size(), 3);
        testRedirectUrlsValidity(suggestions);
    }

    @Test(enabled = true)
    public void testURLProjectSuggestion() {

        logger.info("TEST NAME = URL PROJECT SUGGESTION");
        List<Typeahead> results = typeaheadService.getTypeaheadsV4("501421", 1, null, null, null);
        results.get(0).setScore(100f);
        List<Typeahead> suggestions = entitySuggestionHandler.getEntityBasedSuggestions(results, suggCount);
        logger.info("Suggestions recieved = " + suggestions.toString());
        Assert.assertEquals(suggestions.size(), 3);
        testRedirectUrlsValidity(suggestions);
    }

    /********** Internal Methods **********/

    private void testRedirectUrlsValidity(List<Typeahead> suggestions) {
        String url;
        for (Typeahead suggestion : suggestions) {
            url = suggestion.getRedirectUrl();
            Assert.assertTrue(isURLValid(url), ("test failed for " + suggestion.toString()));
        }
    }

    private boolean isURLValid(String urlToTest) {
        String url = urlValidationApiURL + "?url=" + urlToTest;
        URI uri = URLUtil.getEncodedURIObject(url, BASE_URL);
        logger.info("Testing url : " + uri.toString());
        ValidURLResponse validURLResponse = httpRequestUtil.getInternalApiResultAsTypeFromCache(
                uri,
                ValidURLResponse.class);
        logger.info("Validator response : " + validURLResponse.toString());
        return (validURLResponse.getHttpStatus() == 200);
    }

    private void setDummyProjectCounts(Typeahead topResult) {
        topResult.setScore(TypeaheadConstants.suggestionScoreThreshold);
        topResult.setEntityProjectCountTotal(500);
        topResult.setEntityProjectCountAffordable(100);
        topResult.setEntityProjectCountLuxury(100);
        topResult.setEntityProjectCountNewLaunch(100);
        topResult.setEntityProjectCountUnderConstruction(100);
        topResult.setEntityProjectCountResale(100);
        topResult.setEntityProjectCountCompleted(100);
    }

}