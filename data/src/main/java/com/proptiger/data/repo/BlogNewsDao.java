package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.WordpressPost;

public interface BlogNewsDao extends JpaRepository<WordpressPost, Long>{

	@Query("SELECT D " +
            " FROM WordpressTerms AS A, WordpressTermTaxonomy AS B, WordpressTermRelationship AS C, WordpressPost AS D "+
                "  WHERE A.termId = B.termId AND"+
                "  C.termTaxonomyId = B.termTaxonomyId AND"+
                "  D.id = C.objectId AND"+
            " A.name= ?1 AND D.postStatus = 'publish' AND D.postTitle!='' AND D.postContent!='' ORDER BY D.postDate DESC ")
	
	public List<WordpressPost> findPublishedBlogNewsByCity(String cityName);
	
	@Query("Select WP.guid from WordpressPost WP where WP.parentId=?1 and WP.postMimeType like 'image%' order by WP.postDate")
	public List<String> findImageUrlsForPost(Long postId);
}
