package com.proptiger.columbus.propguide;

import java.io.IOException;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptiger.columbus.model.PropguideDocument;
import com.proptiger.columbus.mvc.PropguideController;
import com.proptiger.columbus.service.AbstractTest;
import com.proptiger.columbus.util.TestEssential;
import com.proptiger.columbus.util.ResultEssential;
import com.proptiger.core.pojo.response.APIResponse;

@Component
@Test(singleThreaded = true)
public class PropguideTest extends AbstractTest {

    private static Logger                    logger                      = LoggerFactory.getLogger(PropguideTest.class);

    private String                           URL_PARAM_TEMPLATE_PROPGUDE = "query=%s&rows=%s";

    @Autowired
    private PropguideController              propguideController;

    @Autowired
    private TestEssential<PropguideDocument> testEssential;

    @Value("${propguide.api.url}")
    private String                           PROPGUIDE_URL;

    @Test(enabled = true)
    public void testControllerResponseValidity() {

        String url;
        MockHttpServletResponse mhsr = null;
        url = PROPGUIDE_URL + "?" + "query=property";
        logger.info("RUNNING TEST (basic-api-response). Url = " + url);
        mhsr = mockRequestAndGetResponse(propguideController, url);
        Assert.assertTrue(mhsr.getStatus() == 200, "Non Ok response. Url = " + url);
    }

    @Test(enabled = true)
    public void testRowLimiting() {

        String url;
        APIResponse apiResponse = null;
        int rows = 3;
        String query = "property";
        url = PROPGUIDE_URL + "?" + String.format(URL_PARAM_TEMPLATE_PROPGUDE, query, rows);
        logger.info("RUNNING TEST (row-limiting). Url = " + url);
        apiResponse = mockRequestAndGetAPIResponse(propguideController, url);
        Assert.assertEquals(apiResponse.getStatusCode(), "2XX", "Invalid status code in response.");

        @SuppressWarnings("unchecked")
        List<Object> results = (List<Object>) (apiResponse.getData());

        Assert.assertTrue(results.size() <= rows, "Row-Limiting failed. Rows recieved = " + results.size()
                + " .Expected not more than "
                + rows);

        Assert.assertTrue(results.size() > 0, "Row-Limiting failed. Rows recieved = " + results.size()
                + " .Atlease one result should be returned.");

    }

    @Test(enabled = true)
    public void testPropguideObject() {

        String url;
        APIResponse apiResponse = null;
        int rows = 5;
        String query = "property";
        url = PROPGUIDE_URL + "?" + String.format(URL_PARAM_TEMPLATE_PROPGUDE, query, rows);
        logger.info("RUNNING TEST (propguide-object). Url = " + url);
        apiResponse = mockRequestAndGetAPIResponse(propguideController, url);
        Assert.assertEquals(apiResponse.getStatusCode(), "2XX", "Invalid status code in response.");

        @SuppressWarnings("unchecked")
        List<Object> results = (List<Object>) (apiResponse.getData());

        Assert.assertNotNull(results, "Null pgd-list in apiResponse.");
        Assert.assertTrue(results.size() > 0, "0 documents recieved");

        PropguideDocument pd = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            pd = mapper.readValue(mapper.writer().writeValueAsString(results.get(rows - 1)), PropguideDocument.class);
        }
        catch (IOException e) {
            Assert.assertTrue(false, "Error mapping response to PropguideDocument.");
        }
        testObjectValidity(pd);
    }

    private void testObjectValidity(PropguideDocument pd) {

        List<ResultEssential> results = testEssential.testEssentialFields(pd);
        for (ResultEssential result : results) {
            Assert.assertTrue(result.isPassed(), result.getMessage());
        }

        String id = pd.getId();
        Assert.assertTrue(StringUtils.contains(id, "PROPGUIDE-"));
    }

}
