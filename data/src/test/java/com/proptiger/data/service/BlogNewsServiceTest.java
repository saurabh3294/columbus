/**
 * 
 */
package com.proptiger.data.service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.validator.routines.UrlValidator;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.data.model.WordpressPost;
import com.proptiger.data.pojo.Selector;

public class BlogNewsServiceTest extends AbstractTest {
    @Autowired
    private BlogNewsService blogNewsService;
    private Pattern p = Pattern.compile("-(\\d.*?x.\\d.*?)."); //Pattern to find dimension in thumbnail image

    @Test
    public void testGetBlogPostsByCity() {
        String[] schemes = {"http","https"};
        UrlValidator urlValidator = new UrlValidator(schemes);
        Selector blogSelector = new Selector();
        int contentLimit = 200;
        Integer cityId = 2;
        List<WordpressPost> newsList = blogNewsService.getBlogPostsByCityId(cityId, contentLimit, blogSelector);
        for (WordpressPost wp : newsList) {
            if (wp.getPrimaryImageUrl() != null) {
                String url = wp.getPrimaryImageUrl();
                boolean isValidUrl = urlValidator.isValid(url);
                boolean isThumbnailImg = getDimensionPattern(url);
                Assert.assertTrue("Thumbnail Image URL invalid", isValidUrl);
                Assert.assertTrue("Thumbnail Image dimension invalid", isThumbnailImg);
            }
        }
    }

    private boolean getDimensionPattern(String url) {
        Matcher m = p.matcher(url);
        if (m.find()) {
            return true;
        }
        return false;
    }

}
