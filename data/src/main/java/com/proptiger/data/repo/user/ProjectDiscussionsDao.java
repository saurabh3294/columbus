package com.proptiger.data.repo.user;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.ProjectDiscussion;

public interface ProjectDiscussionsDao extends PagingAndSortingRepository<ProjectDiscussion, Long> {

    public ProjectDiscussion findByIdAndProjectId(long commentId, int projectId);

    @Query("SELECT pd FROM ProjectDiscussion pd WHERE pd.projectId = ?1 AND pd.status = '1' ORDER BY pd.createdDate DESC")
    public List<ProjectDiscussion> getDiscussionsByProjectIdOrderByCreatedDateDesc(int projectId);
    
    @Query("SELECT pd " + "FROM ProjectDiscussion pd  "
            + "WHERE pd.projectId = ?1 "
            + "AND pd.status = '1' "
            + "ORDER BY pd.id DESC")
    public List<ProjectDiscussion> getProjectDiscussionsOrderByDiscussionIdDesc(int projectId);
    
    @Query("SELECT pd FROM ProjectDiscussion pd WHERE pd.parentId = ?1")
    public List<ProjectDiscussion> getChildrenProjectDiscussions(Long commentId);
}
