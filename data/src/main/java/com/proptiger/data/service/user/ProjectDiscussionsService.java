package com.proptiger.data.service.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.core.constants.ResponseCodes;
import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.model.cms.Project;
import com.proptiger.core.model.user.User;
import com.proptiger.core.util.PropertyReader;
import com.proptiger.data.model.ProjectDiscussion;
import com.proptiger.data.model.user.ProjectCommentLikes;
import com.proptiger.data.pojo.Paging;
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.repo.ProjectDBDao;
import com.proptiger.data.repo.user.ProjectDiscussionsDao;
import com.proptiger.data.repo.user.portfolio.ProjectCommentLikesDao;
import com.proptiger.data.service.ProjectService;
import com.proptiger.data.service.mail.MailSender;
import com.proptiger.data.service.mail.TemplateToHtmlGenerator;
import com.proptiger.data.util.Caching;
import com.proptiger.data.util.Constants;
import com.proptiger.exception.ResourceAlreadyExistException;

@Service
public class ProjectDiscussionsService {

    @Autowired
    private ProjectDiscussionsDao   projectDiscussionDao;

    @Autowired
    private SubscriptionService     subscriptionService;

    @Autowired
    private ProjectService          projectService;

    @Autowired
    private ProjectCommentLikesDao  projectCommentLikesDao;

    @Autowired
    private TemplateToHtmlGenerator mailBodyGenerator;

    @Autowired
    private MailSender              mailSender;

    @Autowired
    private PropertyReader          propertyReader;

    @Autowired
    private Caching                 caching;
    
    @Autowired
    private ProjectDBDao         projectDBDao;
    
    @Autowired
    private UserService userService;

    public ProjectDiscussion saveProjectComments(ProjectDiscussion projectDiscussion, ActiveUser activeUser) {

        if (projectDiscussion.getComment() == null || projectDiscussion.getComment().isEmpty()) {
            throw new IllegalArgumentException("Comments cannot be null");
        }
        Project project = projectService.getProjectData(projectDiscussion.getProjectId());
        if (project == null) {
            throw new IllegalArgumentException("Enter valid Project Id");
        }

        User user = userService.getUserById(activeUser.getUserIdentifier());

        projectDiscussion.setUserId(activeUser.getUserIdentifier());
        projectDiscussion.setAdminUserName(user.getFullName());
        projectDiscussion.setLevel(0);
        projectDiscussion.setNumLikes(0);
        projectDiscussion.setStatus("0");
        projectDiscussion.setReplied(ProjectDiscussion.Replies.F);

        ProjectDiscussion parentProjectDiscussion = null;
        if (projectDiscussion.getParentId() > 0) {
            parentProjectDiscussion = projectDiscussionDao.findByIdAndProjectId(
                    projectDiscussion.getParentId(),
                    projectDiscussion.getProjectId());
            if (parentProjectDiscussion == null) {
                throw new IllegalArgumentException("Parent Comment Id project should belong to Project Id supplied.");
            }

            projectDiscussion.setLevel(parentProjectDiscussion.getLevel() + 1);
            projectDiscussion.setReplied(ProjectDiscussion.Replies.T);
        }
        ProjectDiscussion savedProjectDiscussions = projectDiscussionDao.save(projectDiscussion);

        subscriptionService.enableOrAddUserSubscription(
                activeUser.getUserIdentifier(),
                projectDiscussion.getProjectId(),
                Project.class.getAnnotation(Table.class).name(),
                Constants.SubscriptionType.FORUM);

        return savedProjectDiscussions;
    }

    @Transactional
    public ProjectDiscussion incrementProjectCommentLikes(long commentId, ActiveUser userInfo) {
        ProjectDiscussion projectDiscussion = projectDiscussionDao.findOne(commentId);

        if (projectDiscussion == null) {
            return null;
        }
        // User has already liked the comment
        if (createProjectCommentLikes(commentId, userInfo.getUserIdentifier()) == null) {
            throw new ResourceAlreadyExistException(
                    "User has already liked the comment.",
                    ResponseCodes.NAME_ALREADY_EXISTS);
        }

        projectDiscussion.setNumLikes(projectDiscussion.getNumLikes() + 1);

        // deleting the project discussion cache.
        caching.deleteMultipleResponseFromCacheOnRegex(
                ":" + projectDiscussion.getProjectId() + ":",
                Constants.CacheName.PROJECT_DISCUSSION);

        return projectDiscussion;
    }

    public ProjectCommentLikes findProjectCommentLikesOnUserIdAndCommentId(long commentId, int userId) {
        return projectCommentLikesDao.findByCommentIdAndUserId(commentId, userId);
    }

    public ProjectCommentLikes createProjectCommentLikes(long commentId, int userId) {
        ProjectCommentLikes alreadyLikes = findProjectCommentLikesOnUserIdAndCommentId(commentId, userId);
        if (alreadyLikes != null) {
            return null;
        }

        ProjectCommentLikes projectCommentLikes = new ProjectCommentLikes();

        projectCommentLikes.setCommentId(commentId);
        projectCommentLikes.setUserId(userId);

        return projectCommentLikesDao.save(projectCommentLikes);
    }

    @Cacheable(
            value = Constants.CacheName.PROJECT_DISCUSSION,
            key = "':'+#projectId+':'+#paging.getStart()+'-'+#paging.getRows()")
    /**
     * This method will take all project comments and then make a hierarichal structure of the 
     * comments based on the parent comment Id. Hence, a root comment can have infinite level
     * of tree structure in the worst case. The paging is applied on the number of
     * root comments.
     * @param projectId
     * @param paging
     * @return
     */
    public PaginatedResponse<List<ProjectDiscussion>> getProjectComments(int projectId, Paging paging) {

        List<ProjectDiscussion> allComments = projectDiscussionDao
                .getDiscussionsByProjectIdOrderByCreatedDateDesc(projectId);
        if (allComments.size() < 1){
            return new PaginatedResponse<>();
        }

        populateUserDetails(allComments);
        Map<Long, List<ProjectDiscussion>> parentChildComments = new HashMap<>();
        long parentId;
        List<ProjectDiscussion> projectDiscussionsList;
        Iterator<ProjectDiscussion> it = allComments.iterator();
        ProjectDiscussion projectDiscussions;
        while (it.hasNext()) {
            projectDiscussions = it.next();
            parentId = projectDiscussions.getParentId();

            if ("proptiger".equals(projectDiscussions.getUser().getUsername())) {
                projectDiscussions.getUser().setUsername(projectDiscussions.getAdminUserName());
            }

            if (parentId > 0) {
                projectDiscussionsList = parentChildComments.get(parentId);
                if (projectDiscussionsList == null) {
                    projectDiscussionsList = new ArrayList<>();
                    parentChildComments.put(parentId, projectDiscussionsList);
                }

                projectDiscussionsList.add(projectDiscussions);
                it.remove();
            }
        }
        /**
         * Paging on the number of root comments.
         */
        int totalRootComments = allComments.size();
        allComments = setPagingOnProjectDiscussion(allComments, paging);

        Queue<ProjectDiscussion> queue = new LinkedList<>(allComments);
        while (!queue.isEmpty()) {
            projectDiscussions = queue.remove();
            projectDiscussionsList = parentChildComments.get(projectDiscussions.getId());
            if (projectDiscussionsList != null) {
                projectDiscussions.setChildDiscussions(projectDiscussionsList);
                queue.addAll(projectDiscussionsList);
            }
        }

        PaginatedResponse<List<ProjectDiscussion>> response = new PaginatedResponse<>();
        response.setResults(allComments);
        response.setTotalCount(totalRootComments);

        return response;
    }

    private void populateUserDetails(List<ProjectDiscussion> discussions) {
        if(discussions != null && !discussions.isEmpty()){
            Set<Integer> usersIds = new HashSet<>();   
            for(ProjectDiscussion pd: discussions){
                usersIds.add(pd.getUserId());
            }
            Map<Integer, User> userMap = userService.getUsers(usersIds);
            Iterator<ProjectDiscussion> it = discussions.iterator();
            while(it.hasNext()){
                ProjectDiscussion pd = it.next();
                User u = userMap.get(pd.getUserId());
                if(u != null){
                    pd.setUser(u.toForumUser());
                }
                else{
                    it.remove();
                }
            }
        }
    }

    /**
     * Paging is being applied on the number of root comments to return in the response. Each root comments
     * can have infinite hierarchal structure of comments.
     * @param comments
     * @param paging
     * @return
     */
    private List<ProjectDiscussion> setPagingOnProjectDiscussion(List<ProjectDiscussion> comments, Paging paging) {
        int totalRootComments = comments.size();
        // setting paging of the root comments
        if (paging == null) {
            paging = new Paging();
        }
        if (paging.getStart() > totalRootComments) {
            return new ArrayList<ProjectDiscussion>();
        }
        
        // End Index of the subList.
        int pagingRows = paging.getRows() + paging.getStart();
        pagingRows = pagingRows > totalRootComments ? totalRootComments : pagingRows;

        return new ArrayList<ProjectDiscussion>(comments.subList(paging.getStart(), pagingRows));
    }
    
    /**
     * Returns all discussions for a project
     * 
     * @param projectId
     * @param commentId
     * @return
     */
    public List<ProjectDiscussion> getDiscussions(int projectId, Long commentId) {
        List<ProjectDiscussion> discussions = null;
        if (commentId == null) {
            discussions = projectDiscussionDao.getProjectDiscussionsOrderByDiscussionIdDesc(projectId);
        }
        else {
            discussions = projectDiscussionDao.getChildrenProjectDiscussions(commentId);
        }
        populateUserDetails(discussions);
        for (ProjectDiscussion projectDiscussion : discussions) {
            if ("proptiger".equals(projectDiscussion.getUser().getUsername())) {
                projectDiscussion.getUser().setUsername(projectDiscussion.getAdminUserName());
            }
        }

        return discussions;
    }
    

}
