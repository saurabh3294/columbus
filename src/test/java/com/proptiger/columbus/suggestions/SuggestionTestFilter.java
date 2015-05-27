package com.proptiger.columbus.suggestions;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.testng.Assert;

import com.proptiger.columbus.service.SuggestionTest;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.URLUtil;

@Component
public class SuggestionTestFilter implements SuggestionTest {

    private static Logger   logger = LoggerFactory.getLogger(SuggestionTestFilter.class);

    @Autowired
    private HttpRequestUtil httpRequestUtil;

    @Value("${proptiger.url}")
    private String          BASE_URL;

    @Value("${project.listing.api.url}")
    private String          projectListingApiUrl;
    
    private String selecterTemplate = "selector={\"filters\":%s,\"paging\":{\"start\":0,\"rows\":0}}";

    public void test(List<Typeahead> suggestions) {
        String filter;
        for (Typeahead suggestion : suggestions) {
            filter = suggestion.getRedirectUrlFilters();
            Assert.assertTrue(isFilterValid(filter), ("test failed for " + suggestion.toString()));
        }
    }

    private boolean isFilterValid(String filter) {
        String url = projectListingApiUrl + "?" + String.format(selecterTemplate, filter);
        URI uri = URLUtil.getEncodedURIObject(url, BASE_URL);
        logger.info("Testing url : " + uri.toString());
        APIResponse apiResponse = httpRequestUtil.getInternalApiResultAsTypeFromCache(
                uri,
                APIResponse.class);
        logger.info("Validator response : " + apiResponse.getStatusCode());
        return (apiResponse.getStatusCode() == "2XX");
    }

}