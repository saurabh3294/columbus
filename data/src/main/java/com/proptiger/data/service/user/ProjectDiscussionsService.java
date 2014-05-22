package com.proptiger.data.service.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.enums.mail.MailTemplateDetail;
import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.internal.dto.mail.MailBody;
import com.proptiger.data.internal.dto.mail.MailDetails;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.ProjectDiscussion;
import com.proptiger.data.model.portfolio.ProjectCommentLikes;
import com.proptiger.data.pojo.Paging;
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.repo.ForumUserDao;
import com.proptiger.data.repo.portfolio.ProjectCommentLikesDao;
import com.proptiger.data.repo.portfolio.ProjectDiscussionsDao;
import com.proptiger.data.service.ProjectService;
import com.proptiger.data.service.mail.MailSender;
import com.proptiger.data.service.mail.TemplateToHtmlGenerator;
import com.proptiger.data.util.Caching;
import com.proptiger.data.util.Constants;
import com.proptiger.data.util.PropertyReader;
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
    private ForumUserDao            forumUserDao;

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

    public ProjectDiscussion saveProjectComments(ProjectDiscussion projectDiscussion, UserInfo userInfo) {

        if (projectDiscussion.getComment() == null || projectDiscussion.getComment().isEmpty()) {
            throw new IllegalArgumentException("Comments cannot be null");
        }
        Project project = projectService.getProjectData(projectDiscussion.getProjectId());
        if (project == null) {
            throw new IllegalArgumentException("Enter valid Project Id");
        }

        ForumUser forumUser = forumUserDao.findOne(userInfo.getUserIdentifier());

        projectDiscussion.setUserId(userInfo.getUserIdentifier());
        projectDiscussion.setAdminUserName(forumUser.getUsername());
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
                userInfo.getUserIdentifier(),
                projectDiscussion.getProjectId(),
                Project.class.getAnnotation(Table.class).name(),
                Constants.SubscriptionType.FORUM);

        return savedProjectDiscussions;
    }

    @Transactional
    public ProjectDiscussion incrementProjectCommentLikes(long commentId, UserInfo userInfo) {
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
    public PaginatedResponse<List<ProjectDiscussion>> getProjectComments(int projectId, Paging paging) {

        List<ProjectDiscussion> allComments = projectDiscussionDao
                .getDiscussionsByProjectIdOrderByCreatedDateDesc(projectId);
        if (allComments == null || allComments.size() < 1)
            return new PaginatedResponse<>();

        Map<Long, List<ProjectDiscussion>> parentChildComments = new HashMap<>();
        long parentId;
        List<ProjectDiscussion> projectDiscussionsList;
        Iterator<ProjectDiscussion> it = allComments.iterator();
        ProjectDiscussion projectDiscussions;
        while (it.hasNext()) {
            projectDiscussions = it.next();
            parentId = projectDiscussions.getParentId();

            if (!projectDiscussions.getUser().getUsername().equals("proptiger")) {
                projectDiscussions.setAdminUserName(projectDiscussions.getUser().getUsername());
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

    private List<ProjectDiscussion> setPagingOnProjectDiscussion(List<ProjectDiscussion> comments, Paging paging) {
        int totalRootComments = comments.size();
        // setting paging of the root comments
        if (paging == null) {
            paging = new Paging();
        }
        if (paging.getStart() > totalRootComments) {
            throw new ArrayIndexOutOfBoundsException("Max comments in the project is: " + totalRootComments);
        }

        int pagingRows = paging.getRows() + paging.getStart();
        pagingRows = pagingRows > totalRootComments ? totalRootComments : pagingRows;

        return new ArrayList<ProjectDiscussion>(comments.subList(paging.getStart(), pagingRows));
    }

    @Deprecated
    private boolean sendMailOnProjectComment(ForumUser forumUser, Project project, ProjectDiscussion projectDiscussion) {

        String mailTo = propertyReader.getRequiredProperty("mail.project.comment.to.recipient");

        String mailCC = propertyReader.getRequiredProperty("mail.project.comment.cc.recipient");

        MailBody mailBody = mailBodyGenerator.generateMailBody(
                MailTemplateDetail.ADD_NEW_PROJECT_COMMENT,
                new ProjectDiscussionMailDTO(project, forumUser, projectDiscussion));
        MailDetails mailDetails = new MailDetails(mailBody).setMailTo(mailTo).setMailCC(mailCC);
        return mailSender.sendMailUsingAws(mailDetails);

    }

    public static class ProjectDiscussionMailDTO {
        public Project           project;
        public ForumUser         forumUser;
        public ProjectDiscussion projectDiscussion;

        public ProjectDiscussionMailDTO(Project project, ForumUser forumUser, ProjectDiscussion projectDiscussion) {
            this.project = project;
            this.forumUser = forumUser;
            this.projectDiscussion = projectDiscussion;
        }

        public Project getProject() {
            return project;
        }

        public ForumUser getForumUser() {
            return forumUser;
        }

        public ProjectDiscussion getProjectDiscussion() {
            return projectDiscussion;
        }

    }

}
