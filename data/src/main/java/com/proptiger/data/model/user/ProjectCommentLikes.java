package com.proptiger.data.model.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.core.model.BaseModel;

@Entity
@Table(name = "FORUM_USER_LIKES")
public class ProjectCommentLikes extends BaseModel {

    private static final long serialVersionUID = 9201411629880607950L;

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private long              id;

    @Column(name = "USER_ID")
    private int               userId;

    @Column(name = "COMMENT_ID")
    private long              commentId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }
}
