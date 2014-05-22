package com.proptiger.data.repo;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

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
}
