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
import com.proptiger.core.enums.seo.ValidURLResponse;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.URLUtil;

@Component
public class SuggestionTestURL implements SuggestionTest {

    private static Logger   logger = LoggerFactory.getLogger(SuggestionTestURL.class);

    @Autowired
    private HttpRequestUtil httpRequestUtil;

    @Value("${proptiger.url}")
    private String          BASE_URL;

    @Value("${url.validation.api.url}")
    private String          urlValidationApiURL;

    public void test(List<Typeahead> suggestions) {
        String url;
        for (Typeahead suggestion : suggestions) {
            url = suggestion.getRedirectUrl();
            Assert.assertTrue(isURLValid(url), ("test failed for " + suggestion.toString()));
        }
    }

    private boolean isURLValid(String urlToTest) {
        String url = urlValidationApiURL + "?url=" + urlToTest;
        URI uri = URLUtil.getEncodedURIObject(url, BASE_URL);
        logger.info("Testing url : " + uri.toString());
        ValidURLResponse validURLResponse = httpRequestUtil.getInternalApiResultAsTypeFromCache(
                uri,
                ValidURLResponse.class);
        logger.info("Validator response : " + validURLResponse.toString());
        return (validURLResponse.getHttpStatus() == 200);
    }

}