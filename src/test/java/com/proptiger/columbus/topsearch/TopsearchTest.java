package com.proptiger.columbus.topsearch;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Component;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.Ordering;
import com.proptiger.columbus.model.TypeaheadConstants;
import com.proptiger.columbus.mvc.TopsearchController;
import com.proptiger.columbus.service.AbstractTest;
import com.proptiger.columbus.util.TypeaheadUtils;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.pojo.response.APIResponse;

@Component
@Test(singleThreaded = true)
public class TopsearchTest extends AbstractTest {

    private static Logger logger                       = LoggerFactory.getLogger(TopsearchTest.class);

    private String        URL_PARAM_TEMPLATE_TOPSEARCH = "entityId=%s&entityType=%s&requiredEntities=%s&group=%s&rows=%s";

    private int           defaultEntityId              = 2;
    private String        defaultEntityName            = "bangalore";
    private String        defaultEntityType            = "city";
    private String        defaultRequiredEntities      = "locality,project";

    @Autowired
    TopsearchController   topsearchController;

    @Value("${topsearch.api.url}")
    private String        TOP_SEARCH_URL;

    @Test(enabled = true)
    public void testControllerResponseValidity() {
        String url;
        MockHttpServletResponse mhsr = null;

        /* Basic api test */
        url = TOP_SEARCH_URL + "?" + "entityId=2&entityType=city&requiredEntities=locality";
        logger.info("RUNNING TEST (basic-api-response). Url = " + url);
        mhsr = mockRequestAndGetResponse(topsearchController, url);
        Assert.assertTrue(mhsr.getStatus() == 200, "Non Ok response. Url = " + url);

        /* No entity Id given */
        url = TOP_SEARCH_URL + "?" + "entityType=city&requiredEntities=locality";
        logger.info("RUNNING TEST (absent-entityId). Url = " + url);
        mhsr = mockRequestAndGetResponse(topsearchController, url);
        Assert.assertEquals(mhsr.getStatus(), 400, "Invalid status code in response.");

        /* No entity Type given */
        url = TOP_SEARCH_URL + "?" + "entityId=2&requiredEntities=locality";
        logger.info("RUNNING TEST (absent-entityId). Url = " + url);
        mhsr = mockRequestAndGetResponse(topsearchController, url);
        Assert.assertEquals(mhsr.getStatus(), 400, "Invalid status code in response.");

        /* No required Entity given */
        url = TOP_SEARCH_URL + "?" + "entityId=2&entityType=city";
        logger.info("RUNNING TEST (absent-requiredEntities). Url = " + url);
        mhsr = mockRequestAndGetResponse(topsearchController, url);
        Assert.assertEquals(mhsr.getStatus(), 400, "Invalid status code in response.");

    }

    @Test(enabled = true)
    public void testRowLimiting() {

        String urlUngrouped;
        String urlGrouped;
        APIResponse apiResponse = null;
        int rows = 2;
        urlUngrouped = TOP_SEARCH_URL + "?"
                + String.format(
                        URL_PARAM_TEMPLATE_TOPSEARCH,
                        defaultEntityId,
                        defaultEntityType,
                        defaultRequiredEntities,
                        false,
                        rows);
        urlGrouped = TOP_SEARCH_URL + "?"
                + String.format(
                        URL_PARAM_TEMPLATE_TOPSEARCH,
                        defaultEntityId,
                        defaultEntityType,
                        "locality,project",
                        true,
                        rows);
        logger.info("RUNNING TEST (row-limiting-ungrouped). Url = " + urlUngrouped);
        apiResponse = mockRequestAndGetAPIResponse(topsearchController, urlUngrouped);
        Assert.assertEquals(apiResponse.getStatusCode(), "2XX", "Invalid status code in response.");

        List<Typeahead> results = getDataAsObjectList(
                apiResponse.getData(),
                TypeaheadConstants.GSON_TOKEN_TYPE_TYPEAHEAD_LIST);
        Assert.assertTrue(results.size() <= rows, "Row-Limiting failed. Rows recieved = " + results.size()
                + " .Expected not more than "
                + rows);

        logger.info("RUNNING TEST (row-limiting-Grouped). Url = " + urlGrouped);
        apiResponse = mockRequestAndGetAPIResponse(topsearchController, urlGrouped);
        Assert.assertEquals(apiResponse.getStatusCode(), "2XX", "Invalid status code in response.");

        List<Typeahead> resultsGrouped = getDataAsObjectList(
                apiResponse.getData(),
                TypeaheadConstants.GSON_TOKEN_TYPE_TYPEAHEAD_LIST);
        Assert.assertTrue(
                resultsGrouped.size() <= rows * 2,
                "Row-Limiting failed. Rows recieved = " + resultsGrouped.size()
                        + " .Expected not more than "
                        + rows
                        * 2);

    }

    @Test(enabled = true)
    public void testSameCity() {

        String url;
        APIResponse apiResponse = null;
        int rows = 100;
        url = TOP_SEARCH_URL + "?"
                + String.format(
                        URL_PARAM_TEMPLATE_TOPSEARCH,
                        defaultEntityId,
                        defaultEntityType,
                        "suburb,locality,builder,project",
                        false,
                        rows);
        logger.info("RUNNING TEST (same-city). Url = " + url);
        apiResponse = mockRequestAndGetAPIResponse(topsearchController, url);
        Assert.assertEquals(apiResponse.getStatusCode(), "2XX", "Invalid status code in response.");
        List<Typeahead> results = getDataAsObjectList(
                apiResponse.getData(),
                TypeaheadConstants.GSON_TOKEN_TYPE_TYPEAHEAD_LIST);
        String recievedCity;
        for (Typeahead t : results) {
            recievedCity = String.valueOf(t.getCity());
            Assert.assertEquals(recievedCity.toLowerCase(), defaultEntityName, "Expected city " + defaultEntityName
                    + "Received city "
                    + recievedCity.toLowerCase());
        }
    }

    @Test(enabled = true)
    public void testValidateRequiredEntities() {

        String url;
        APIResponse apiResponse = null;
        int rows = 40;

        List<String> requiredEntitiesList = Arrays.asList(new String[] { "locality", "project" });
        String requiredEntities = StringUtils.join(requiredEntitiesList, ',');
        url = TOP_SEARCH_URL + "?"
                + String.format(
                        URL_PARAM_TEMPLATE_TOPSEARCH,
                        defaultEntityId,
                        defaultEntityType,
                        requiredEntities,
                        false,
                        rows);

        logger.info("RUNNING TEST (validate-required-entities). Url = " + url);
        apiResponse = mockRequestAndGetAPIResponse(topsearchController, url);
        Assert.assertEquals(apiResponse.getStatusCode(), "2XX", "Invalid status code in response.");

        List<Typeahead> results = getDataAsObjectList(
                apiResponse.getData(),
                TypeaheadConstants.GSON_TOKEN_TYPE_TYPEAHEAD_LIST);
        String type = "";

        for (Typeahead typeahead : results) {
            type = typeahead.getType().toLowerCase();
            Assert.assertTrue(requiredEntitiesList.contains(type), "Expected Required Entities- " + requiredEntities
                    + " Received "
                    + type);
        }

    }

    @Test(enabled = true)
    public void testValidateHierarchy() {

        String url;
        APIResponse apiResponse = null;
        int rows = 40;
        int entityId = 2;
        String entityType = "city";
        String requiredEntities = "locality,project";
        String city = "Bangalore";
        url = TOP_SEARCH_URL + "?"
                + String.format(URL_PARAM_TEMPLATE_TOPSEARCH, entityId, entityType, requiredEntities, false, rows);

        logger.info("RUNNING TEST (validate-hierarchy). Url = " + url);
        apiResponse = mockRequestAndGetAPIResponse(topsearchController, url);
        Assert.assertEquals(apiResponse.getStatusCode(), "2XX", "Invalid status code in response.");

        List<Typeahead> results = getDataAsObjectList(
                apiResponse.getData(),
                TypeaheadConstants.GSON_TOKEN_TYPE_TYPEAHEAD_LIST);

        for (Typeahead typeahead : results) {
            Assert.assertTrue(
                    typeahead.getCity().equalsIgnoreCase(city),
                    "Validate-hierarchy failed. Entity City recieved = " + typeahead.getCity() + " .Expected  " + city);
        }

    }

    @Test(enabled = true)
    public void testValidateSorting() {

        String url;
        APIResponse apiResponse = null;
        int rows = 40;
        int entityId = 2;
        String entityType = "city";
        String requiredEntities = "locality,project";

        url = TOP_SEARCH_URL + "?"
                + String.format(URL_PARAM_TEMPLATE_TOPSEARCH, entityId, entityType, requiredEntities, false, rows);

        logger.info("RUNNING TEST (validate-sorting). Url = " + url);
        apiResponse = mockRequestAndGetAPIResponse(topsearchController, url);
        Assert.assertEquals(apiResponse.getStatusCode(), "2XX", "Invalid status code in response.");

        List<Typeahead> results = getDataAsObjectList(
                apiResponse.getData(),
                TypeaheadConstants.GSON_TOKEN_TYPE_TYPEAHEAD_LIST);
        Assert.assertTrue(
                Ordering.from(new TypeaheadUtils.AbstractTypeaheadComparatorScore()).isOrdered(results),
                "Result set Not sorted by score.");
    }

    @Test(enabled = true)
    public void testValidateGrouping() {

        String url;
        APIResponse apiResponse = null;
        int rows = 40;
        int entityId = 2;
        String entityType = "city";
        String requiredEntities = "locality,project,suburb,builder";

        url = TOP_SEARCH_URL + "?"
                + String.format(URL_PARAM_TEMPLATE_TOPSEARCH, entityId, entityType, requiredEntities, true, rows);

        logger.info("RUNNING TEST (validate-grouping). Url = " + url);
        apiResponse = mockRequestAndGetAPIResponse(topsearchController, url);
        Assert.assertEquals(apiResponse.getStatusCode(), "2XX", "Invalid status code in response.");

        List<Typeahead> results = getDataAsObjectList(
                apiResponse.getData(),
                TypeaheadConstants.GSON_TOKEN_TYPE_TYPEAHEAD_LIST);

        Assert.assertTrue(
                Ordering.from(new TypeaheadUtils.TypeaheadComparatorTypeaheadType()).isOrdered(results),
                "Required Entities are Not Grouped.");
    }

    @Test(enabled = true)
    public void testNoEmptyResults() {

        String url;
        APIResponse apiResponse = null;
        int rows = 40;
        int entityId = 2;
        String entityType = "city";
        String requiredEntities = "locality,project,suburb,builder";

        url = TOP_SEARCH_URL + "?"
                + String.format(URL_PARAM_TEMPLATE_TOPSEARCH, entityId, entityType, requiredEntities, true, rows);

        logger.info("RUNNING TEST (validate-no-empty-result). Url = " + url);
        apiResponse = mockRequestAndGetAPIResponse(topsearchController, url);
        Assert.assertEquals(apiResponse.getStatusCode(), "2XX", "Invalid status code in response.");

        List<Typeahead> results = getDataAsObjectList(
                apiResponse.getData(),
                TypeaheadConstants.GSON_TOKEN_TYPE_TYPEAHEAD_LIST);

        Assert.assertTrue(results.size() > 0, "test no-empty-results failed. Result Count Received 0. url - " + url);

    }

}
