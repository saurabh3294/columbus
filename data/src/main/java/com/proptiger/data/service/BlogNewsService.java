package com.proptiger.data.service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.City;
import com.proptiger.data.model.WordpressPost;
import com.proptiger.data.pojo.Paging;
import com.proptiger.data.pojo.Selector;
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
    /**
     * Get blog for city name
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
        List<WordpressPost> list = blogNewsDao.findPublishedBlogByCity(
                cityName,
                paging);
        for (WordpressPost post : list) {
            List<String> urlList = blogNewsDao.findImageUrlsForBlogPost(post.getId());
            if (urlList != null && urlList.size() > 0) {
                post.setPrimaryImageUrl(urlList.get(0));
            }
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
     * @param cityName
     * @param contentLimit
     * @param selector
     * @return
     */
    public List<WordpressPost> getNewsByCity(Integer cityId, int contentLimit, Selector selector){

        if (contentLimit <= 0) {
            throw new IllegalArgumentException("Invalid content limit");
        }
        Paging paging = createPaging(selector);
        City city = cityService.getCity(cityId);
        List<WordpressPost> list = blogNewsDao.findPublishedNewsByCity(city.getLabel(), paging);
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
     * @param list
     * @param contentLimit
     */
    private void removeHtmlTagsFromPostContent(List<WordpressPost> list, int contentLimit) {

        Pattern htmlTagPattern = Pattern.compile("(?s)<[^>]*>(\\s*<[^>]*>)*");
        for (WordpressPost post : list) {
            Matcher matcher = htmlTagPattern.matcher(post.getPostContent());
            String contentWithoutHtmlTag = matcher.replaceAll("").substring(0, contentLimit);
            post.setPostContent(contentWithoutHtmlTag);

        }
    }
}
