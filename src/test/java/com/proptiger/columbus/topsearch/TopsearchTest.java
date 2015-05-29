package com.proptiger.columbus.topsearch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Component;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.proptiger.columbus.model.TypeaheadConstants;
import com.proptiger.columbus.mvc.TopsearchController;
import com.proptiger.columbus.service.AbstractTest;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.pojo.response.APIResponse;

@Component
@Test(singleThreaded = true)
public class TopsearchTest extends AbstractTest {

    private static Logger logger                       = LoggerFactory.getLogger(TopsearchTest.class);

    private String        URL_PARAM_TEMPLATE_TOPSEARCH = "entityId=%s&entityType=%s&requiredEntities=%s&rows=%s";

    private int           defaultEntityId              = 2;
    private String        defaultEntityName            = "bangalore";
    private String        defaultEntityType            = "city";

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

    }

    @Test(enabled = true)
    public void testRowLimiting() {

        String url;
        APIResponse apiResponse = null;
        int rows = 2;
        url = TOP_SEARCH_URL + "?"
                + String.format(
                        URL_PARAM_TEMPLATE_TOPSEARCH,
                        defaultEntityId,
                        defaultEntityType,
                        "locality,project",
                        rows);
        logger.info("RUNNING TEST (row-limiting). Url = " + url);
        apiResponse = mockRequestAndGetAPIResponse(topsearchController, url);
        Assert.assertEquals(apiResponse.getStatusCode(), "2XX", "Invalid status code in response.");

        List<Typeahead> results = getDataAsObjectList(
                apiResponse.getData(),
                TypeaheadConstants.GSON_TOKEN_TYPE_TYPEAHEAD_LIST);
        Assert.assertTrue(results.size() <= rows, "Row-Limiting failed. Rows recieved = " + results.size()
                + " .Expected not more than "
                + rows);
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
            Assert.assertEquals(recievedCity.toLowerCase(), defaultEntityName);
        }
    }

}
