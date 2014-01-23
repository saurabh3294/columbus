package com.proptiger.data.repo.portfolio;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.ForumUserComments;

public interface ForumUserCommentsDao
		extends
		PagingAndSortingRepository<ForumUserComments, Long> {
	
	public ForumUserComments findByCommentId(long commentId);
}
