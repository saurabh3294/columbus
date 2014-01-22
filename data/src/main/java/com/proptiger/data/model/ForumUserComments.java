/*
 * To change this template, choose Tools  Templates
 * and open the template in the editor.
 */
package com.proptiger.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author mukand
 */
@Entity
@Table(name = "FORUM_USER_COMMENTS")
public class ForumUserComments implements BaseModel{
    @Column(name = "COMMENT_ID")
    @Id
    @GeneratedValue
    private long  commentId;
    
    @Column(name = "USER_ID")
    private int  userId;
    
    @Column(name = "ADMIN_USERNAME")
    private String  adminUsername;
    
    @Column(name = "PARENT_ID")
    private long  parentId;
    
    @Column(name = "LEVEL")
    private int  level;
    
    @Column(name = "PROJECT_ID")
    private int  projectId;
    
    @Column(name = "TITLE", nullable = true)
    private String  title = "";
    
    @Column(name = "URL", nullable = true)
    private String  url = "";
    
    @Column(name = "COMMENTS")
    private String  comments;
    
    @Column(name = "LIKES", nullable = true)
    private int  likes = 0;
    
    @Column(name = "REPLY", nullable = true)
    private String reply;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATE")
    private Date  createdDate;
    
    @Column(name = "STATUS")
    private String status = "0";
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "APPROVED_DATE")
    private Date approvedDate;

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    @PrePersist
    public void prePersist(){
    	this.createdDate = new Date();
    	this.approvedDate = new Date();
    }

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}
}
