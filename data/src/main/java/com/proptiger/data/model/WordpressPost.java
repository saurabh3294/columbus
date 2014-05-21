package com.proptiger.data.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * @author Rajeev Pandey
 * 
 */
@Entity
@Table(name = "wp_posts")
@ResourceMetaInfo
@NamedQueries({
        @NamedQuery(
                name = "Post.blogOrNews",
                query = "SELECT D " + " FROM WordpressPost AS D, WordpressTerms AS A, WordpressTermTaxonomy AS B, WordpressTermRelationship AS C "
                        + "  WHERE A.termId = B.termId AND"
                        + "  C.termTaxonomyId = B.termTaxonomyId AND"
                        + "  D.id = C.objectId AND"
                        + " A.name IN :cityName AND D.postStatus = 'publish' AND D.postTitle!='' AND D.postContent!='' GROUP BY D.id ORDER BY D.postDate DESC "),
        @NamedQuery(
                name = "Post.imageUrl",
                query = "Select WP.guid from WordpressPost WP where WP.parentId=:postId and WP.postMimeType like 'image%' order by WP.postDate") })
public class WordpressPost extends BaseModel {

    private static final long serialVersionUID = -4623450582253484193L;

    @Id
    @Column(name = "ID")
    private long              id;

    @Column(name = "post_parent")
    @JsonIgnore
    private long              parentId;

    @Column(name = "post_title")
    private String            postTitle;

    @Column(name = "post_content")
    private String            postContent;

    @Column(name = "guid")
    private String            guid;

    @Column(name = "post_mime_type")
    @JsonIgnore
    private String            postMimeType;

    @Column(name = "post_status")
    private String            postStatus;

    @Column(name = "post_date")
    private Date              postDate;

    @Column(name = "comment_count")
    private int               commentCount;

    @Transient
    private String            primaryImageUrl;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getPrimaryImageUrl() {
        return primaryImageUrl;
    }

    public void setPrimaryImageUrl(String primaryImageUrl) {
        this.primaryImageUrl = primaryImageUrl;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getPostMimeType() {
        return postMimeType;
    }

    public void setPostMimeType(String postMimeType) {
        this.postMimeType = postMimeType;
    }

}
