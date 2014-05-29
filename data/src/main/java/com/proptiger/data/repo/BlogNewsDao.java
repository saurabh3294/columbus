package com.proptiger.data.repo;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.proptiger.data.init.ApplicationConfig;
import com.proptiger.data.model.WordpressPost;
import com.proptiger.data.pojo.Paging;

/**
 * This class handles queries for both wordpress and wordpress_news database
 * 
 * @author Rajeev Pandey
 * 
 */
@Component
public class BlogNewsDao {
    private static String   THUMBNAIL_QUERY_STRING   = "SELECT WPP1.post_id, WPP2.meta_value FROM wordpress.wp_postmeta WPP1 INNER JOIN wordpress.wp_postmeta WPP2 ON WPP1.meta_value = WPP2.post_id WHERE WPP1.post_id IN (postIdList) " + " AND WPP1.meta_key = '_thumbnail_id' AND WPP2.meta_key = '_wp_attachment_metadata'";

    private Pattern FOLDER_PATTERN = Pattern.compile("(\\d{4})/(\\d{2})/");
    private Pattern IMG_PATTERN    = Pattern.compile("\"thumbnail.*?file.*?:\"(.*?)\";");
    
    @Value("${blog.image.base.url}")
    private String  URL_PATH;

    /**
     * Using wordpress database in this method to find published blogs for a city
     * @param cityName
     * @param paging
     * @return
     */
    public List<WordpressPost> findPublishedBlogByCity(List<String> cityName, Paging paging) {
        return findNewsOrBlogsByCity(cityName, ApplicationConfig.getWordpressEntityFactory(), paging);
    }

    /**
     * Using wordpress_news database in this method to find published news for
     * single or multiple city
     * 
     * @param cityName
     * @param paging
     * @return
     */
    public List<WordpressPost> findPublishedNewsByCity(List<String> cityName, Paging paging) {
        return findNewsOrBlogsByCity(cityName, ApplicationConfig.getWordpressNewsEntityFactory(), paging);
    }

    private List<WordpressPost> findNewsOrBlogsByCity(List<String> cityName, EntityManagerFactory emf, Paging paging) {
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Post.blogOrNews").setParameter("cityName", cityName);
        query.setFirstResult(paging.getStart());
        query.setMaxResults(paging.getRows());
        List<WordpressPost> results = query.getResultList();
        return results;
    }
    
    /**
     * Find image url for the blog post
     * @param postId
     * @return
     */
    public List<String> findImageUrlsForBlogPost(Long postId) {
        EntityManager em = ApplicationConfig.getWordpressEntityFactory().createEntityManager();
        Query query = em.createNamedQuery("Post.imageUrl").setParameter("postId", postId);
        List<String> results = query.getResultList();
        return results;
    }
    
    /**
     * Find image url for the blog post
     * @param postId
     * @return
     */
    public List<String> findImageUrlsForNewsPost(Long postId) {
        EntityManager em = ApplicationConfig.getWordpressNewsEntityFactory().createEntityManager();
        Query query = em.createNamedQuery("Post.imageUrl").setParameter("postId", postId);
        List<String> results = query.getResultList();
        return results;
    }

    /**
     * Find Thumbnail image url info for the blog post
     * @param postIdList
     * @return
     */
    public Map<Long, String> findThumbnailImageUrlsForBlogPost(List<Long> postIdList) {
        EntityManager em = ApplicationConfig.getWordpressEntityFactory().createEntityManager();
        String repQueryStr = THUMBNAIL_QUERY_STRING.replaceAll("postIdList", postIdList.toString()).replace("[", "")
                .replace("]", "");
        Query query = em.createNativeQuery(repQueryStr);
        List<Object[]> results = query.getResultList();
        Map<Long, String> idUrlsMap = new HashMap<Long, String>();
        for (Object[] ob : results) {
            Long postId = ((BigInteger) ob[0]).longValue();
            String jsonStr = (String) ob[1];
            idUrlsMap.put(postId, getThumbnailImageUrl(jsonStr));
        }
        return idUrlsMap;
    }

    private String getThumbnailImageUrl(String jsonStr) {
        Matcher m = FOLDER_PATTERN.matcher(jsonStr);
        String folder = "";
        if (m.find()) {
            folder = m.group(0);
        }

        m = IMG_PATTERN.matcher(jsonStr);
        String imgName = "";
        if (m.find()) {
            imgName = m.group(1);
        }

        String imgUrl = URL_PATH + folder + imgName;
        return imgUrl;
    }
}
