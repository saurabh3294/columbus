package com.proptiger.columbus.thandlers;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.util.HttpRequestUtil;

@Component
@Test(singleThreaded = true)
public class TypeaheadSuggestionObjectTest extends AbstractTest {

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
    public void testObjectCitySuggestion() {
        logger.info("TEST NAME = TYPEAHEAD OBJECT CITY SUGGESTION");
        List<Typeahead> results = typeaheadService.getTypeaheadsV4(
                "gurgaon",
                1,
                new HashMap<String, String>(),
                null,
                null);
        List<Typeahead> suggestions = entitySuggestionHandler.getEntityBasedSuggestions(results, suggCount);
        logger.info("Suggestions recieved = " + suggestions.toString());
        testObjectValidity(suggestions);
    }

    @Test(enabled = true)
    public void testObjectLocalitySuggestion() {

        logger.info("TEST NAME = TYPEAHEAD OBJECT LOCALITY SUGGESTION");
        List<Typeahead> results = typeaheadService.getTypeaheadsV4("50186", 1, null, null, null);
        setDummyProjectCounts(results.get(0));
        List<Typeahead> suggestions = entitySuggestionHandler.getEntityBasedSuggestions(results, suggCount);
        logger.info("Suggestions recieved = " + suggestions.toString());
        Assert.assertEquals(suggestions.size(), 5);
        testObjectValidity(suggestions);
    }

    @Test(enabled = true)
    public void testObjectSuburbSuggestion() {

        logger.info("TEST NAME = TYPEAHEAD OBJECT SUBURB SUGGESTION");
        List<Typeahead> results = typeaheadService.getTypeaheadsV4("10512", 1, null, null, null);
        setDummyProjectCounts(results.get(0));
        List<Typeahead> suggestions = entitySuggestionHandler.getEntityBasedSuggestions(results, suggCount);
        logger.info("Suggestions recieved = " + suggestions.toString());
        Assert.assertEquals(suggestions.size(), 5);
        testObjectValidity(suggestions);
    }

    @Test(enabled = true)
    public void testObjectBuilderSuggestion() {

        logger.info("TEST NAME = TYPEAHEAD OBJECT BUILDER SUGGESTION");
        List<Typeahead> results = typeaheadService.getTypeaheadsV4("100002", 1, null, null, null);
        setDummyProjectCounts(results.get(0));
        List<Typeahead> suggestions = entitySuggestionHandler.getEntityBasedSuggestions(results, suggCount);
        logger.info("Suggestions recieved = " + suggestions.toString());
        Assert.assertEquals(suggestions.size(), 3);
        testObjectValidity(suggestions);
    }

    @Test(enabled = true)
    public void testObjectProjectSuggestion() {

        logger.info("TEST NAME = TYPEAHEAD OBJECT PROJECT SUGGESTION");
        List<Typeahead> results = typeaheadService.getTypeaheadsV4("501421", 1, null, null, null);
        results.get(0).setScore(100f);
        List<Typeahead> suggestions = entitySuggestionHandler.getEntityBasedSuggestions(results, suggCount);
        logger.info("Suggestions recieved = " + suggestions.toString());
        Assert.assertEquals(suggestions.size(), 3);
        testObjectValidity(suggestions);
    }

    /********** Internal Methods **********/

    private void testObjectValidity(List<Typeahead> suggestions) {
        for (Typeahead suggestion : suggestions) {
            testObjectValidity(suggestion);
        }
    }

    private void testObjectValidity(Typeahead typeahead) {
        Assert.assertNotNull(typeahead, "Typeahead object is null");

        String typeaheadId = typeahead.getId();
        Assert.assertNotNull(typeaheadId, "Typeahead ID is null");
        Assert.assertFalse(typeaheadId.isEmpty(), "Typeahead ID is empty");
        Assert.assertTrue(StringUtils.contains(typeaheadId, "Typeahead-Suggestion"));

        String typeaheadType = typeahead.getType();
        Assert.assertNotNull(typeaheadType, "Typeahead Type is null");
        Assert.assertFalse(typeaheadType.isEmpty(), "Typeahead Type is empty");
        Assert.assertTrue(typeaheadType.equalsIgnoreCase(typeaheadId), "Typeahead ID and type should be same");
        
        String typeaheadDisplayText = typeahead.getDisplayText();
        Assert.assertNotNull(typeaheadDisplayText, "Typeahead ID is null");
        Assert.assertFalse(typeaheadDisplayText.isEmpty(), "Typeahead ID is empty");
        
        Assert.assertTrue(typeahead.isSuggestion(), "isSuggestion flag must be on");
        Assert.assertFalse(typeahead.isGooglePlace(),"isGooglePlace flag must be off");
        
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