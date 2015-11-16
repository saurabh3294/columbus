package com.proptiger.columbus.typeahead;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.proptiger.columbus.model.TypeaheadConstants;
import com.proptiger.columbus.service.AbstractTest;
import com.proptiger.columbus.service.TypeaheadService;
import com.proptiger.core.model.Typeahead;

public class TypeaheadEqualScoreTieBreakTest extends AbstractTest {

    @Autowired
    TypeaheadService      typeaheadService;

    private static Logger logger = LoggerFactory.getLogger(TypeaheadEqualScoreTieBreakTest.class);

    @Test(enabled = true)
    public void testEqualScoringBuilderProjects() {
        testEqualScoring("prestige", 10, 2, 9);
        testEqualScoring("dlf", 10, 1, 9);
    }

    @Test(enabled = true)
    public void testTieBreakBuilderProjects() {
        testTieBreak("prestige", 10, 2, 9);
        testTieBreak("dlf", 10, 1, 9);
    }

    @Test(enabled = true)
    public void testEqualScoringLocality() {
        testEqualScoring("sector", 10, 0, 9);
    }

    @Test(enabled = false)
    public void testTieBreakLocality() {
        testEqualScoring("sector", 10, 0, 9);
    }

    /**
     * 
     * @param query
     * @param startIndex
     *            : index after which results are expected to have equal scores
     * @param endIndex
     *            : index till which results are expected to have equal scores
     */
    private void testEqualScoring(String query, int rows, int startIndex, int endIndex) {

        logger.info("Test : test equal scoring : [" + StringUtils.join(
                new Object[] { query, rows, startIndex, endIndex },
                ", ") + "]");

        List<Typeahead> results = getAndValidateResults(query, rows, startIndex, endIndex);
        float score = results.get(startIndex).getScore();
        for (int i = startIndex; i < endIndex; i++) {
            Assert.assertEquals(results.get(i).getScore(), score);
        }

    }

    /**
     * 
     * @param query
     * @param startIndex
     *            : index after which results are expected to have increasing
     *            scores
     * @param endIndex
     *            : index till which results are expected to have equal scores
     */
    private void testTieBreak(String query, int rows, int startIndex, int endIndex) {

        logger.info("Test : test tie break : [" + StringUtils.join(
                new Object[] { query, rows, startIndex, endIndex },
                ", ") + "]");

        List<Typeahead> results = getAndValidateResults(query, rows, startIndex, endIndex);

        float popularity = results.get(startIndex).getEntityPopularity();
        for (int i = startIndex; i < endIndex; i++) {
            Assert.assertTrue(results.get(i).getEntityPopularity() <= popularity);
            popularity = results.get(i).getEntityPopularity();
        }

    }

    private List<Typeahead> getAndValidateResults(String query, int rows, int startIndex, int endIndex) {

        Assert.assertTrue((endIndex < rows), "Invalid test case : end index >= rows");

        Map<String, String> filterQueries = new HashMap<String, String>();
        List<Typeahead> results = typeaheadService.getTypeaheadsV4(
                query,
                rows,
                filterQueries,
                null,
                null,
                TypeaheadConstants.DOMAIN_PROPTIGER);

        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() > endIndex, "Result-Count should be greater then endIndex(" + endIndex + ")");

        return results;

    }

}
