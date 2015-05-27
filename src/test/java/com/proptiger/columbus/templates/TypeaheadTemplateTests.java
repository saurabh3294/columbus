package com.proptiger.columbus.templates;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.solr.client.solrj.SolrQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.proptiger.columbus.model.TypeaheadConstants;
import com.proptiger.columbus.service.AbstractTest;
import com.proptiger.columbus.service.SuggestionTest;
import com.proptiger.columbus.suggestions.NLPSuggestionHandler;
import com.proptiger.columbus.suggestions.SuggestionTestFilter;
import com.proptiger.columbus.suggestions.SuggestionTestStructure;
import com.proptiger.columbus.suggestions.SuggestionTestURL;
import com.proptiger.columbus.thandlers.RootTHandler;
import com.proptiger.columbus.thandlers.TemplateMap;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.repo.SolrDao;

public class TypeaheadTemplateTests extends AbstractTest {

    @Autowired
    private NLPSuggestionHandler nlpSuggestionHandler;

    private static Logger        logger             = LoggerFactory.getLogger(TypeaheadTemplateTests.class);

    private String               assertMsgNoResults = "No results recieved for temptate = %s, city = %s";

    @Value("${proptiger.url}")
    private String               BASE_URL;

    private String               testCityName       = TypeaheadConstants.defaultCityName;

    private int                  testCityId         = TypeaheadConstants.defaultCityId;

    private List<SuggestionTest> testList;

    private TemplateMap          templateMap        = new TemplateMap();

    @Autowired
    private SolrDao              solrDao;

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
    public void testAllTemplates() {

        logger.info("TEST NAME = ALL TEMPLATES");

        List<Typeahead> allTemplates = getAllTemplates();
        Assert.assertNotNull(allTemplates, "Could not retrieve exhaustive list of templates.");
        Assert.assertTrue(allTemplates.size() > 0, "0 templates recieved.");

        logger.info("Total Templates recieved = " + allTemplates.size());
        
        RootTHandler thandler;
        List<Typeahead> results;
        String message;
        for (Typeahead t : allTemplates) {
            logger.info("Testing template : " + t.getId());
            thandler = templateMap.getTemplateHandler(t.getTemplateText().trim());
            results = thandler.getResults("", t, testCityName, testCityId, 5);
            message = String.format(assertMsgNoResults, t.getId(), testCityName);
            Assert.assertEquals(results.size(), 5, message);
            executeAllTestTypes(results);
        }
    }
  
    /********** Internal Methods **********/

    private List<Typeahead> getAllTemplates() {

        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setFilterQueries("DOCUMENT_TYPE:TYPEAHEAD" + " AND " + "TYPEAHEAD_TYPE:TEMPLATE");
        List<Typeahead> results = solrDao.executeQuery(solrQuery).getBeans(Typeahead.class);
        return results;
    }

    private void executeAllTestTypes(List<Typeahead> suggestions) {
        for (SuggestionTest test : testList) {
            logger.info("Executing tests from " + test.getClass().getSimpleName() + " on all templates");
            test.test(suggestions);
        }
    }

}
