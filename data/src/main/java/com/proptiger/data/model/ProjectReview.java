package com.proptiger.data.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "REVIEW_COMMENTS")
@ResourceMetaInfo(name = "Project Review")
public class ProjectReview {

	@FieldMetaInfo(name = "comment_id", displayName = "Comment Id", dataType = DataType.LONG, description = "Comment Id")
	@Column(name = "COMMENT_ID")
	@JsonProperty(value = "comment_id")
	@Id
	private long commentId;
	
	@FieldMetaInfo(name = "user_id", displayName = "User Id", dataType = DataType.LONG, description = "User Id")
	@Column(name = "USER_ID")
	@JsonProperty(value = "user_id")
	private long userId;
	
	@FieldMetaInfo(name = "project_id", displayName = "Project Id", dataType = DataType.LONG, description = "Project Id")
	@Column(name = "PROJECT_ID")
	@JsonProperty(value = "project_id")
	private long projectId;
	
	@FieldMetaInfo(name = "locality_id", displayName = "Locality Id", dataType = DataType.STRING, description = "Locality Id")
	@Column(name = "LOCALITY_ID")
	@JsonProperty(value = "locality_id")
	private long localityId;
	
	@FieldMetaInfo(name = "likes_count", displayName = "Likes Count", dataType = DataType.STRING, description = "Likes Count")
	@Column(name = "LIKES_COUNT")
	@JsonProperty(value = "likes_count")
	private long likesCount;
	
	@FieldMetaInfo(name = "review_label", displayName = "Review Label", dataType = DataType.STRING, description = "Review Label")
	@Column(name = "REVIEW_LABEL")
	@JsonProperty(value = "review_label")
	private String reviewLabel;
	
	@FieldMetaInfo(name = "review", displayName = "Review", dataType = DataType.STRING, description = "Review")
	@Column(name = "REVIEW")
	@JsonProperty(value = "review")
	private String review;
	
	@FieldMetaInfo(name = "recommend", displayName = "Recommend", dataType = DataType.STRING, description = "Recommend")
	@Column(name = "RECOMMEND")
	@JsonProperty(value = "recommend")
	private String recommend;
	
	@FieldMetaInfo(name = "you_know", displayName = "You Know", dataType = DataType.STRING, description = "You Know")
	@Column(name = "YOU_KNOW")
	@JsonProperty(value = "you_know")
	private int youKnow;
	
	@FieldMetaInfo(name = "commenttime", displayName = "Comment Time", dataType = DataType.DATE, description = "Comment Time")
	@Column(name = "COMMENTTIME")
	@JsonProperty(value = "commenttime")
	private Date commentTime;
	
	@FieldMetaInfo(name = "user_name", displayName = "User Name", dataType = DataType.STRING, description = "User Name")
	@Column(name = "USER_NAME")
	@JsonProperty(value = "user_name")
	private String userName;
	
	@FieldMetaInfo(name = "status", displayName = "Status", dataType = DataType.INTEGER, description = "Status")
	@Column(name = "STATUS")
	@JsonProperty(value = "status")
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
