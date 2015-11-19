package com.proptiger.columbus.typeahead;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.proptiger.columbus.mvc.TypeaheadController;
import com.proptiger.columbus.response.ColumbusAPIResponse;
import com.proptiger.columbus.service.AbstractTest;
import com.proptiger.core.model.Typeahead;

@Test(singleThreaded = true)
public class TestRedirectable extends AbstractTest{
    private static Logger       logger = LoggerFactory.getLogger(Typeahead.class);

    @Value("${typeahead.api.url.pattern}")
    private String TYPEAHEAD_URL;
    
    private String                           URL_PARAM_TEMPLATE_TYPEAHEAD= "?query=%s&rows=%s";
    
    @Autowired
    private TypeaheadController typeaheadController;
    
    @Test(enabled = true)
    public void testRedirectable() {
        String url;
        ColumbusAPIResponse apiResponse = null;
        int rows = 1;
        String query = "3c";
        url = String.format(TYPEAHEAD_URL,"v4") + String.format(URL_PARAM_TEMPLATE_TYPEAHEAD, query, rows);
        
        logger.info("RUNNING TEST (Typeahead-Redirectable). Url = " + url);
        apiResponse = mockRequestAndGetColumbusAPIResponse(typeaheadController, url);
        Assert.assertTrue(apiResponse.getRedirectable() != null,"Redirectable null for Url: "+url);
    }

}
