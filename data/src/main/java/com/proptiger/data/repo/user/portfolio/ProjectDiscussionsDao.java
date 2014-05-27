package com.proptiger.data.repo.user.portfolio;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.ProjectDiscussion;

public interface ProjectDiscussionsDao extends PagingAndSortingRepository<ProjectDiscussion, Long> {

    public ProjectDiscussion findByIdAndProjectId(long commentId, int projectId);

    @Query("SELECT pd FROM ProjectDiscussion pd JOIN FETCH pd.user as U WHERE pd.projectId = ?1 AND " + " pd.status = '1' AND U.status = '1' ORDER BY pd.createdDate DESC")
    public List<ProjectDiscussion> getDiscussionsByProjectIdOrderByCreatedDateDesc(int projectId);
}
