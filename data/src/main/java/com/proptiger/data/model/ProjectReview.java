package com.proptiger.data.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonProperty;

@Entity
@Table(name = "REVIEW_COMMENTS")
public class ProjectReview {

	@Column(name = "COMMENT_ID")
	@JsonProperty(value = "comment_id")
	@Id
	private long commentId;
	@Column(name = "USER_ID")
	@JsonProperty(value = "user_id")
	private long userId;
	@Column(name = "PROJECT_ID")
	@JsonProperty(value = "project_id")
	private long projectId;
	@Column(name = "LOCALITY_ID")
	@JsonProperty(value = "locality_id")
	private long localityId;
	@Column(name = "LIKES_COUNT")
	@JsonProperty(value = "likes_count")
	private long likesCount;
	@Column(name = "REVIEW_LABEL")
	@JsonProperty(value = "review_label")
	private String reviewLabel;
	@Column(name = "REVIEW")
	@JsonProperty(value = "review")
	private String review;
	@Column(name = "RECOMMEND")
	@JsonProperty(value = "recommend")
	private String recommend;
	@Column(name = "YOU_KNOW")
	@JsonProperty(value = "you_know")
	private int youKnow;
	@Column(name = "COMMENTTIME")
	@JsonProperty(value = "commenttime")
	private Date commentTime;
	@Column(name = "USER_NAME")
	@JsonProperty(value = "user_name")
	private String userName;
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

	public enum Recommend {
		FAMILY("family"), SINGLES("singles"), COUPLES("couples"), RETIREES(
				"retirees");

		private String type;

		private Recommend(String type) {
			this.type = type;
		}
	}
}
