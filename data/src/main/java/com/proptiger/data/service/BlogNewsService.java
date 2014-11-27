package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.proptiger.core.model.cms.City;
import com.proptiger.core.pojo.Paging;
import com.proptiger.core.pojo.Selector;
import com.proptiger.data.model.WordpressPost;
import com.proptiger.data.repo.BlogNewsDao;

/**
 * @author Rajeev Pandey
 * 
 */
@Service
public class BlogNewsService {

    @Autowired
    private BlogNewsDao blogNewsDao;

    @Autowired
    private CityService cityService;
    
    private Pattern htmlTagPattern = Pattern.compile("(?s)<[^>]*>(\\s*<[^>]*>)*");

    /**
     * Get blog for city name
     * 
     * @param cityName
     * @param contentLimit
     * @param selector
     * @return
     */
    public List<WordpressPost> getBlogPostsByCity(String cityName, int contentLimit, Selector selector) {
        if (contentLimit <= 0) {
            throw new IllegalArgumentException("Invalid content limit");
        }
        Paging paging = createPaging(selector);
        List<String> cityNameList = Collections.singletonList(cityName + " Property");
        List<WordpressPost> list = blogNewsDao.findPublishedBlogByCity(cityNameList, paging);
        List<Long> postIdList = new ArrayList<Long>();
        for (WordpressPost post : list) {
            postIdList.add(post.getId());
        }
        Map<Long, String> idUrlMap = blogNewsDao.findThumbnailImageUrlsForBlogPost(postIdList);
        for (WordpressPost post : list) {
            String url = idUrlMap.get(post.getId());
            post.setPrimaryImageUrl(url);
        }
        removeHtmlTagsFromPostContent(list, contentLimit);
        return list;
    }

    public List<WordpressPost> getBlogPostsByCityId(Integer cityId, int contentLimit, Selector selector) {
        City city = cityService.getCity(cityId);
        return getBlogPostsByCity(city.getLabel(), contentLimit, selector);
    }

    /**
     * Get published news of city
     * 
     * @param cityName
     * @param contentLimit
     * @param selector
     * @return
     */
    public List<WordpressPost> getNewsByCity(List<Integer> cityId, int contentLimit, Selector selector) {
        if (contentLimit <= 0) {
            throw new IllegalArgumentException("Invalid content limit");
        }
        StringUtils.join(cityId, ',');
        String selectorString = "{\"filters\":{\"and\":[{\"equal\":{\"id\":" + cityId + "}}]}}";
        Gson gson = new Gson();
        Selector citySelector = gson.fromJson(selectorString, Selector.class);
        List<City> cities = cityService.getCityList(citySelector);
        Paging paging = createPaging(selector);
        List<String> cityNames = new ArrayList<String>();
        for (City city : cities) {
            cityNames.add(city.getLabel());
        }
        List<WordpressPost> list = blogNewsDao.findPublishedNewsByCity(cityNames, paging);
        for (WordpressPost post : list) {
            List<String> urlList = blogNewsDao.findImageUrlsForNewsPost(post.getId());
            if (urlList != null && urlList.size() > 0) {
                post.setPrimaryImageUrl(urlList.get(0));
            }
        }
        removeHtmlTagsFromPostContent(list, contentLimit);
        return list;
    }

    /**
     * Create paging object
     * 
     * @param selector
     * @return
     */
    private Paging createPaging(Selector selector) {
        Paging paging = new Paging();
        if (selector != null && selector.getPaging() != null) {
            paging = selector.getPaging();
        }
        return paging;
    }

    /**
     * Removing html tags from post content
     * 
     * @param list
     * @param contentLimit
     */
    private void removeHtmlTagsFromPostContent(List<WordpressPost> list, int contentLimit) {

        for (WordpressPost post : list) {
            if (post.getPostExcerpt() != null && !post.getPostExcerpt().isEmpty()) {
                post.setPostContent(post.getPostExcerpt());
            }
            else {
                Matcher matcher = htmlTagPattern.matcher(post.getPostContent());
                String contentWithoutHtmlTag = matcher.replaceAll("");
                int len = contentWithoutHtmlTag.length() < contentLimit ? contentWithoutHtmlTag.length(): contentLimit;
                post.setPostContent(contentWithoutHtmlTag.substring(0, len));
            }
        }
    }

}
