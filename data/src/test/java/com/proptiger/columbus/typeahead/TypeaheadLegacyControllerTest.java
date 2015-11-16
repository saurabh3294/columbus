package com.proptiger.columbus.typeahead;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.proptiger.columbus.mvc.TypeaheadController;
import com.proptiger.columbus.service.AbstractTest;

public class TypeaheadLegacyControllerTest extends AbstractTest {

    @Autowired
    private TypeaheadController typeaheadController;

    @Value("${typeahead.api.url.pattern}")
    private String              TYPEAHEAD_API_URL_PATTERN;

    @Test(enabled = true)
    public void testLegacyControllerResponse() {

        testLegacyControllerResponseValidityByVersion("v1");
        testLegacyControllerResponseValidityByVersion("v2");
        testLegacyControllerResponseValidityByVersion("v3");
        testLegacyControllerResponseValidityByVersion("v4");
    }

    private void testLegacyControllerResponseValidityByVersion(String version) {

        String url = String.format(TYPEAHEAD_API_URL_PATTERN, version) + "?" + "query=noida";
        logger.info("RUNNING TEST (basic-api-response). Url = " + url);
        MockHttpServletResponse mhsr = mockRequestAndGetResponse(typeaheadController, url);
        Assert.assertTrue(mhsr.getStatus() == 200, "Non Ok response. Url = " + url);
    }

}
