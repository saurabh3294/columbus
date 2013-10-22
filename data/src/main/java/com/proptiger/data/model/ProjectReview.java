package com.proptiger.data.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "REVIEW_COMMENTS")
@ResourceMetaInfo
public class ProjectReview{

	@FieldMetaInfo( displayName = "Comment Id",  description = "Comment Id")
	@Column(name = "COMMENT_ID")
	@Id
	private long commentId;
	
	@FieldMetaInfo( displayName = "User Id",  description = "User Id")
	@Column(name = "USER_ID")
	private long userId;
	
	@FieldMetaInfo( displayName = "Project Id",  description = "Project Id")
	@Column(name = "PROJECT_ID")
	private long projectId;
	
	@FieldMetaInfo( displayName = "Locality Id",  description = "Locality Id")
	@Column(name = "LOCALITY_ID")
	private long localityId;
	
	@FieldMetaInfo( displayName = "Likes Count",  description = "Likes Count")
	@Column(name = "LIKES_COUNT")
	private long likesCount;
	
	@FieldMetaInfo( displayName = "Review Label",  description = "Review Label")
	@Column(name = "REVIEW_LABEL")
	private String reviewLabel;
	
	@FieldMetaInfo( displayName = "Review",  description = "Review")
	@Column(name = "REVIEW")
	private String review;
	
	@FieldMetaInfo( displayName = "Recommend",  description = "Recommend")
	@Column(name = "RECOMMEND")
	private String recommend;
	
	@FieldMetaInfo( displayName = "You Know",  description = "You Know")
	@Column(name = "YOU_KNOW")
	private int youKnow;
	
	@FieldMetaInfo( displayName = "Comment Time",  description = "Comment Time")
	@Column(name = "COMMENTTIME")
	private Date commentTime;
	
	@FieldMetaInfo( displayName = "User Name",  description = "User Name")
	@Column(name = "USER_NAME")
	private String userName;
	
	@FieldMetaInfo( displayName = "Status",  description = "Status")
	@Column(name = "STATUS")
	private int status;

	public long getCommentId() {
		return commentId;
	}

	public void setCommentId(long commentId) {
		this.commentId = commentId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public long getLocalityId() {
		return localityId;
	}

	public void setLocalityId(long localityId) {
		this.localityId = localityId;
	}

	public long getLikesCount() {
		return likesCount;
	}

	public void setLikesCount(long likesCount) {
		this.likesCount = likesCount;
	}

	public String getReviewLabel() {
		return reviewLabel;
	}

	public void setReviewLabel(String reviewLabel) {
		this.reviewLabel = reviewLabel;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public String getRecommend() {
		return recommend;
	}

	public void setRecommend(String recommend) {
		this.recommend = recommend;
	}

	public int getYouKnow() {
		return youKnow;
	}

	public void setYouKnow(int youKnow) {
		this.youKnow = youKnow;
	}

	public Date getCommentTime() {
		return commentTime;
	}

	public void setCommentTime(Date commentTime) {
		this.commentTime = commentTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
