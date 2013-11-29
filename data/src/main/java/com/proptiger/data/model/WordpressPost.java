package com.proptiger.data.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "wordpress.wp_posts")
@ResourceMetaInfo
public class WordpressPost implements BaseModel{

	@Id
	@Column(name = "ID")
	private long id;
	
	@Column(name = "post_title")
	private String postTitle;
	
	@Column(name = "post_content")
	private String postContent;
	
	@Column(name = "guid")
	private String guid;
	

	@Column(name = "post_status")
	private String postStatus;
	
	@Column(name = "post_date")
	private Date postDate;
	
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
	
	
}
