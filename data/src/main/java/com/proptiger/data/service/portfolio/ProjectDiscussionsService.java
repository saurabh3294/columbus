package com.proptiger.data.service.portfolio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.ProjectDiscussion;
import com.proptiger.data.model.portfolio.ProjectCommentLikes;
import com.proptiger.data.pojo.Paging;
import com.proptiger.data.repo.ForumUserDao;
import com.proptiger.data.repo.portfolio.ProjectCommentLikesDao;
import com.proptiger.data.repo.portfolio.ProjectDiscussionsDao;
import com.proptiger.data.service.ProjectService;
import com.proptiger.data.service.pojo.PaginatedResponse;
import com.proptiger.data.util.Constants;
import com.proptiger.exception.ResourceAlreadyExistException;

@Service
public class ProjectDiscussionsService {

	@Autowired
	private ProjectDiscussionsDao projectDiscussionDao;
	
	@Autowired
	private SubscriptionService subscriptionService;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private ForumUserDao forumUserDao;
	
	@Autowired
	private ProjectCommentLikesDao projectCommentLikesDao;
	
	public ProjectDiscussion saveProjectComments(ProjectDiscussion projectDiscussion, UserInfo userInfo){
		
		if(projectDiscussion.getComment() == null || projectDiscussion.getComment().isEmpty() ){
			throw new IllegalArgumentException("Comments cannot be null");
		}
		
		if( projectService.getProjectData(projectDiscussion.getProjectId()) == null){
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
		if(projectDiscussion.getParentId() > 0)
		{
			parentProjectDiscussion = projectDiscussionDao.findByIdAndProjectId(projectDiscussion.getParentId(), projectDiscussion.getProjectId());
			if(parentProjectDiscussion == null)
				throw new IllegalArgumentException("Parent Comment Id project should belong to Project Id supplied.");
			
			projectDiscussion.setLevel(parentProjectDiscussion.getLevel()+1);
			projectDiscussion.setReplied(ProjectDiscussion.Replies.T);
		}
		ProjectDiscussion savedProjectDiscussions = projectDiscussionDao.save(projectDiscussion);
		
		subscriptionService.enableOrAddUserSubscription(userInfo.getUserIdentifier(), projectDiscussion.getProjectId(), Project.class.getAnnotation(Table.class).name(), Constants.SubscriptionType.FORUM);
		return savedProjectDiscussions;
	}
	
	@Transactional
	public ProjectDiscussion incrementProjectCommentLikes(long commentId, UserInfo userInfo){
		ProjectDiscussion projectDiscussion = projectDiscussionDao.findOne(commentId);
		
		if(projectDiscussion == null){
			return null;
		}
		// User has already liked the comment
		if( createProjectCommentLikes(commentId, userInfo.getUserIdentifier()) == null )
			throw new ResourceAlreadyExistException("User has already liked the comment.");
		
		projectDiscussion.setNumLikes(projectDiscussion.getNumLikes() +1);
		
		return projectDiscussion;
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
	
	public PaginatedResponse<List<ProjectDiscussion>> getProjectComments(int projectId, Paging paging){
		
		List<ProjectDiscussion> allComments = projectDiscussionDao.getDiscussionsByProjectIdOrderByCreatedDateDesc(projectId);
		
		Map<Long, List<ProjectDiscussion>> parentChildComments = new HashMap<>();
		long parentId;
		List<ProjectDiscussion> projectDiscussionsList;
		Iterator<ProjectDiscussion> it = allComments.iterator();
		ProjectDiscussion projectDiscussions;
		while(it.hasNext())
		{
			projectDiscussions = it.next();
			parentId = projectDiscussions.getParentId();
			if( parentId > 0 )
			{
				projectDiscussionsList = parentChildComments.get(parentId);
				if(projectDiscussionsList == null)
				{
					projectDiscussionsList = new ArrayList<>();
					parentChildComments.put(parentId, projectDiscussionsList);
				}
			
				projectDiscussionsList.add(projectDiscussions);
				it.remove();
			}
		}
		int totalRootComments = allComments.size();
		
		// setting paging of the root comments
		if(paging == null)
		{
			paging = new Paging();
		}
		allComments = allComments.subList(paging.getStart(), paging.getRows()+paging.getStart());
		
		Queue<ProjectDiscussion> queue = new LinkedList<>(allComments);
		while( !queue.isEmpty() )
		{
			projectDiscussions = queue.remove();
			projectDiscussionsList = parentChildComments.get( projectDiscussions.getId() );
			if(projectDiscussionsList != null)
			{
				projectDiscussions.setChildDiscussions( projectDiscussionsList );
				queue.addAll(projectDiscussionsList);
			}
		}
		
		PaginatedResponse<List<ProjectDiscussion>> response = new PaginatedResponse<>();
		response.setResults(allComments);
		response.setTotalCount(totalRootComments);
		
		return response;
	}

}
