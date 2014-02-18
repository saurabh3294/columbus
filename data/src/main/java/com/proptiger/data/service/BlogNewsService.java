package com.proptiger.data.service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.WordpressPost;
import com.proptiger.data.pojo.LimitOffsetPageRequest;
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

    public List<WordpressPost> getBlogNewsPostsByCity(String cityName, int contentLimit, Selector selector) {
        if (contentLimit <= 0) {
            throw new IllegalArgumentException("Invalid content limit");
        }
        Paging paging = new Paging();
        if (selector != null && selector.getPaging() != null) {
            paging = selector.getPaging();
        }
        LimitOffsetPageRequest pageable = new LimitOffsetPageRequest(paging.getStart(), paging.getRows());
        List<WordpressPost> list = blogNewsDao.findPublishedBlogNewsByCity(cityName, pageable);
        for (WordpressPost post : list) {
            List<String> urlList = blogNewsDao.findImageUrlsForPost(post.getId());
            if (urlList != null && urlList.size() > 0) {
                post.setPrimaryImageUrl(urlList.get(0));
            }
        }
        removeHtmlTagsFromPostContent(list, contentLimit);
        return list;
    }

    private void removeHtmlTagsFromPostContent(List<WordpressPost> list, int contentLimit) {

        Pattern htmlTagPattern = Pattern.compile("(?s)<[^>]*>(\\s*<[^>]*>)*");
        for (WordpressPost post : list) {
            Matcher matcher = htmlTagPattern.matcher(post.getPostContent());
            String contentWithoutHtmlTag = matcher.replaceAll("").substring(0, contentLimit);
            post.setPostContent(contentWithoutHtmlTag);

        }
    }
}
