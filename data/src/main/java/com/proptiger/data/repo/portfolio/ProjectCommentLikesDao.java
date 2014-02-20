package com.proptiger.data.repo.portfolio;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.portfolio.ProjectCommentLikes;

@Repository
public interface ProjectCommentLikesDao extends PagingAndSortingRepository<ProjectCommentLikes, Long> {

    public ProjectCommentLikes findByCommentIdAndUserId(long commentId, int userId);
}
