package com.proptiger.data.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.util.ReplySerializer;

@Entity
@Table(name = "FORUM_USER_COMMENTS")
@ResourceMetaInfo
@JsonFilter("fieldFilter")
@JsonInclude(Include.NON_NULL)
public class ProjectDiscussion extends BaseModel {

    private static final long serialVersionUID = -9152119195829550249L;

    public enum Replies {
        F("false"), T("true");

        String value;

        private Replies(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

    @Column(name = "COMMENT_ID")
    @Id
    @GeneratedValue
    private long                    id;

    @Column(name = "PARENT_ID")
    private long                    parentId;

    @Column(name = "USER_ID")
    private int                     userId;

    @Column(name = "ADMIN_USERNAME")
    private String                  adminUserName;

    @Column(name = "PROJECT_ID")
    private int                     projectId;

    @Column(name = "LEVEL")
    private int                     level;

    @Column(name = "COMMENTS")
    private String                  comment;

    @Column(name = "LIKES")
    private int                     numLikes;

    @Column(name = "REPLY")
    @Enumerated(EnumType.STRING)
    @JsonSerialize(using = ReplySerializer.class)
    private Replies                 isReplied;

    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date                    createdDate;

    @Column(name = "APPROVED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date                    approvedDate;

    @Column(name = "STATUS")
    private String                  status;

    @Column(name = "TITLE", nullable = true)
    private String                  title = "";

    @Column(name = "URL", nullable = true)
    private String                  url   = "";

    @Transient
    private ForumUser               user;

    @Transient
    private List<ProjectDiscussion> childDiscussions;

    public ForumUser getUser() {
        return user;
    }

    public void setUser(ForumUser user) {
        this.user = user;
    }

    public String getAdminUserName() {
        return adminUserName;
    }

    public void setAdminUserName(String adminUserName) {
        this.adminUserName = adminUserName;
    }

    public List<ProjectDiscussion> getChildDiscussions() {
        return childDiscussions;
    }

    public void setChildDiscussions(List<ProjectDiscussion> childDiscussions) {
        this.childDiscussions = childDiscussions;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    public Replies isReplied() {
        return isReplied;
    }

    public void setReplied(Replies isReplied) {
        this.isReplied = isReplied;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @PrePersist
    public void prePersist() {
        this.createdDate = new Date();
        this.approvedDate = new Date();
    }

    public Date getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(Date approvedDate) {
        this.approvedDate = approvedDate;
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
}
