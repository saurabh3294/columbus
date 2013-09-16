package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.WordpressPost;

public interface BlogNewsDao extends JpaRepository<WordpressPost, Long>{

	@Query("SELECT D.ID, D.postTitle, D.postContent, D.guid " +
            " FROM WordpressTerms AS A"+
                "  JOIN WordpressTermTaxonomy AS B WHERE A.term_id = B.termId"+
                "  JOIN WordpressTermRelationship AS C WHERE C.termTaxonomyId = B.termTaxonomyId"+
                "  JOIN WordpressPost AS D WHERE D.id = C.objectId WHERE"+
            " A.name='Noida' AND D.postStatus = 'publish' AND D.postTitle!='' AND D.postContent!='' ORDER BY D.postDate DESC LIMIT 10")
	public List<WordpressPost> findBlogNews();
}
