package com.proptiger.columbus.suggestions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.proptiger.columbus.model.TypeaheadConstants;
import com.proptiger.columbus.service.AbstractTest;
import com.proptiger.columbus.service.SuggestionTest;
import com.proptiger.columbus.service.TypeaheadService;
import com.proptiger.core.model.Typeahead;

@Component
@Test(singleThreaded = true)
public class TypeaheadSuggestionTest extends AbstractTest {

    private static Logger        logger                     = LoggerFactory.getLogger(TypeaheadSuggestionTest.class);

    private String               assertMsgTemplateSuggCount = "Suggestion count mismatch for %s suggestions. ID = %s";

    private static int           suggCount                  = 10;

    @Autowired
    TypeaheadService             typeaheadService;

    @Autowired
    EntitySuggestionHandler      entitySuggestionHandler;

    private List<SuggestionTest> testList;

    @Autowired
    SuggestionTestStructure      suggestionTestStructure;

    @Autowired
    SuggestionTestURL            suggestionTestURL;

    @Autowired
    SuggestionTestFilter         suggestionTestFilter;

    @PostConstruct
    public void initialize() {
        this.testList = new ArrayList<SuggestionTest>();
        testList.add(suggestionTestStructure);
        testList.add(suggestionTestURL);
        testList.add(suggestionTestFilter);
    }

    @Test(enabled = true)
    public void testCitySuggestion() {

        logger.info("TEST NAME = CITY SUGGESTION");
        List<Typeahead> results = typeaheadService.getTypeaheadsV4(
                "gurgaon",
                1,
                new HashMap<String, String>(),
                null,
                null);
        List<Typeahead> suggestions = entitySuggestionHandler.getEntityBasedSuggestions(results, suggCount);
        logger.info("Suggestions recieved = " + suggestions.toString());
        executeAllTests(suggestions);
    }

    @Test(enabled = true)
    public void testLocalitySuggestion() {

        String entityId = "50186";
        logger.info("TEST NAME = LOCALITY SUGGESTION");
        List<Typeahead> results = typeaheadService.getTypeaheadsV4(entityId, 1, null, null, null);
        setDummyProjectCounts(results.get(0));
        List<Typeahead> suggestions = entitySuggestionHandler.getEntityBasedSuggestions(results, suggCount);
        logger.info("Suggestions recieved = " + suggestions.toString());
        String message = String.format(assertMsgTemplateSuggCount, "locality", entityId);
        Assert.assertEquals(suggestions.size(), 5, message);
        executeAllTests(suggestions);
    }

    @Test(enabled = true)
    public void testSuburbSuggestion() {

        String entityId = "10512";
        logger.info("TEST NAME = SUBURB SUGGESTION");
        List<Typeahead> results = typeaheadService.getTypeaheadsV4(entityId, 1, null, null, null);
        setDummyProjectCounts(results.get(0));
        List<Typeahead> suggestions = entitySuggestionHandler.getEntityBasedSuggestions(results, suggCount);
        logger.info("Suggestions recieved = " + suggestions.toString());
        String message = String.format(assertMsgTemplateSuggCount, "suburb", entityId);
        Assert.assertEquals(suggestions.size(), 5, message);
        executeAllTests(suggestions);
    }

    @Test(enabled = true)
    public void testBuilderSuggestion() {

        String entityId = "100002";
        logger.info("TEST NAME = BUILDER SUGGESTION");
        List<Typeahead> results = typeaheadService.getTypeaheadsV4(entityId, 1, null, null, null);
        setDummyProjectCounts(results.get(0));
        List<Typeahead> suggestions = entitySuggestionHandler.getEntityBasedSuggestions(results, suggCount);
        logger.info("Suggestions recieved = " + suggestions.toString());
        String message = String.format(assertMsgTemplateSuggCount, "builder", entityId);
        Assert.assertEquals(suggestions.size(), 3, message);
        executeAllTests(suggestions);
    }

    @Test(enabled = true)
    public void testProjectSuggestion() {

        String entityId = "501421";
        logger.info("TEST NAME = PROJECT SUGGESTION");
        List<Typeahead> results = typeaheadService.getTypeaheadsV4(entityId, 1, null, null, null);
        results.get(0).setScore(100f);
        List<Typeahead> suggestions = entitySuggestionHandler.getEntityBasedSuggestions(results, suggCount);
        logger.info("Suggestions recieved = " + suggestions.toString());
        String message = String.format(assertMsgTemplateSuggCount, "project", entityId);
        Assert.assertEquals(suggestions.size(), 3, message);
        executeAllTests(suggestions);
    }

    /********** Internal Methods **********/

    private void executeAllTests(List<Typeahead> suggestions) {
        for (SuggestionTest test : testList) {
            logger.info("Executing tests from " + test.getClass().getSimpleName() + " on all templates.");
            test.test(suggestions);
        }
    }

    private void setDummyProjectCounts(Typeahead topResult) {
        topResult.setScore(TypeaheadConstants.SUGGESTION_SCORE_THRESHOLD);
        topResult.setEntityProjectCountTotal(500);
        topResult.setEntityProjectCountAffordable(100);
        topResult.setEntityProjectCountLuxury(100);
        topResult.setEntityProjectCountNewLaunch(100);
        topResult.setEntityProjectCountUnderConstruction(100);
        topResult.setEntityProjectCountResale(100);
        topResult.setEntityProjectCountCompleted(100);
    }

}