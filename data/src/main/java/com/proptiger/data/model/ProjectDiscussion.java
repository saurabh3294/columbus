package com.proptiger.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.proptiger.data.meta.ResourceMetaInfo;

@Entity
@Table(name="FORUM_USER_COMMENTS")
@ResourceMetaInfo(name = "Project Discussion")
@JsonFilter("fieldFilter")
public class ProjectDiscussion implements BaseModel {
    @Column(name="COMMENT_ID")
    @Id
    private int id;

    @Column(name="PARENT_ID")
    private int parentId;

    @Column(name="PROJECT_ID")
    private int projectId;
    
    @Column(name="LEVEL")
    private int level;

    @Column(name="COMMENTS")
    private String comment;
    
    @Column(name="LIKES")
    private int numLikes;

    @Column(name="REPLY")
    private boolean isReplied;

    @Column(name="CREATED_DATE")
    private Date createdDate;

    @Column(name="STATUS")
    private int status;
    
    @ManyToOne
    @JoinColumn(name = "USER_ID", insertable=false, updatable=false)
    private ForumUser user;
}
