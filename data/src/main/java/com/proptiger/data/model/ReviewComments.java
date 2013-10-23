/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 *
 * @author mukand
 */
@Entity
@Table(name = "REVIEW_COMMENTS")
@ResourceMetaInfo
public class ReviewComments implements BaseModel{
    @FieldMetaInfo(displayName = "Comment Id", description = "Comment Id")
    @Column(name = "COMMENT_ID")
    @Id
    private int commentId;
    
    @FieldMetaInfo(displayName = "User Id", description = "User Id")
    @Column(name = "USER_ID")
    private int userId; 
    
    @FieldMetaInfo(displayName = "Project Id", description = "Project Id")
    @Column(name = "PROJECT_ID")
    private int projectId;
    
    @FieldMetaInfo(displayName = "Locality Id", description = "Locality Id")
    @Column(name = "LOCALITY_ID")
    private int localityId;
    
    @FieldMetaInfo(displayName = "Likes Count", description = "Likes Count")
    @Column(name = "LIKES_COUNT")
    private int likesCount;
    
    @FieldMetaInfo(displayName = "Review Label", description = "Review Label")
    @Column(name = "REVIEW_LABEL")
    private String reviewLabel;
    
    @FieldMetaInfo(displayName = "Review", description = "User Review")
    @Column(name = "REVIEW")
    private String review;
    
    @FieldMetaInfo(displayName = "Recommend", description = "Recommend")
    @Column(name = "RECOMMEND")
    private String recommend;
    
    @FieldMetaInfo(displayName = "You know", description = "You Know")
    @Column(name = "YOU_KNOW")
    private int youKnow;
    
    @FieldMetaInfo(displayName = "Comment Time", description = "Comment Time")
    @Column(name = "COMMENTTIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date commenttime;
    
    @FieldMetaInfo(displayName = "User Name", description = "User Name")
    @Column(name = "USER_NAME")
    private String userName;
    
    @FieldMetaInfo(displayName = "Status", description = "Status")
    @Column(name = "STATUS")
    private int status;

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getLocalityId() {
        return localityId;
    }

    public void setLocalityId(int localityId) {
        this.localityId = localityId;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
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

    public void setYouKnow(short youKnow) {
        this.youKnow = youKnow;
    }

    public Date getCommenttime() {
        return commenttime;
    }

    public void setCommenttime(Date commenttime) {
        this.commenttime = commenttime;
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

    public void setStatus(short status) {
        this.status = status;
    }
}
