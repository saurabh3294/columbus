package com.proptiger.data.service.portfolio;

import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.model.ForumUserComments;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.portfolio.ProjectCommentLikes;
import com.proptiger.data.repo.ForumUserDao;
import com.proptiger.data.repo.portfolio.ForumUserCommentsDao;
import com.proptiger.data.repo.portfolio.ProjectCommentLikesDao;
import com.proptiger.data.service.ProjectService;
import com.proptiger.data.util.Constants;
import com.proptiger.exception.ResourceAlreadyExistException;

@Service
public class ForumUserCommentsService {

	@Autowired
	private ForumUserCommentsDao forumUserCommentsDao;
	
	@Autowired
	private SubscriptionService subscriptionService;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private ForumUserDao forumUserDao;
	
	@Autowired
	private ProjectCommentLikesDao projectCommentLikesDao;
	
	public ForumUserComments saveProjectComments(ForumUserComments forumUserComments, UserInfo userInfo){
		
		if(forumUserComments.getComments() == null || forumUserComments.getComments().isEmpty() ){
			throw new IllegalArgumentException("Comments cannot be null");
		}
		
		if( projectService.getProjectDetails(forumUserComments.getProjectId()) == null){
			throw new IllegalArgumentException("Enter valid Project Id");
		}
		
		ForumUser forumUser = forumUserDao.findOne(userInfo.getUserIdentifier());
		
		forumUserComments.setUserId(userInfo.getUserIdentifier());
		forumUserComments.setAdminUsername(forumUser.getUsername());
		forumUserComments.setLevel(0);
		forumUserComments.setLikes(0);
		forumUserComments.setStatus("0");
		forumUserComments.setReply(Constants.ForumUserComments.FalseReply);
		
		ForumUserComments parentUserComments = null;
		if(forumUserComments.getParentId() > 0)
		{
			parentUserComments = forumUserCommentsDao.findByCommentId(forumUserComments.getParentId());
			forumUserComments.setLevel(parentUserComments.getLevel()+1);
			forumUserComments.setReply(Constants.ForumUserComments.TrueReply);
		}
		ForumUserComments savedUserComments = forumUserCommentsDao.save(forumUserComments);
		
		subscriptionService.enableOrAddUserSubscription(userInfo.getUserIdentifier(), forumUserComments.getProjectId(), Project.class.getAnnotation(Table.class).name(), Constants.SubscriptionType.FORUM);
		return savedUserComments;
	}
	
	@Transactional
	public ForumUserComments incrementProjectCommentLikes(long commentId, UserInfo userInfo){
		ForumUserComments forumUserComments = forumUserCommentsDao.findOne(commentId);
		
		if(forumUserComments == null){
			return null;
		}
		// User has already liked the comment
		if( createProjectCommentLikes(commentId, userInfo.getUserIdentifier()) == null )
			throw new ResourceAlreadyExistException("User has already liked the comment.");
		
		forumUserComments.setLikes(forumUserComments.getLikes() +1);
		
		return forumUserComments;
	}
	
	public ProjectCommentLikes findProjectCommentLikesOnUserIdAndCommentId(long commentId, int userId){
		return projectCommentLikesDao.findByCommentIdAndUserId(commentId, userId);
	}
	public ProjectCommentLikes createProjectCommentLikes(long commentId, int userId){
		ProjectCommentLikes alreadyLikes = findProjectCommentLikesOnUserIdAndCommentId(commentId, userId);
		if(alreadyLikes != null)
			return null;
			
			
		ProjectCommentLikes projectCommentLikes = new ProjectCommentLikes();
		
		projectCommentLikes.setCommentId(commentId);
		projectCommentLikes.setUserId(userId);
		
		return projectCommentLikesDao.save(projectCommentLikes);
	}
}
