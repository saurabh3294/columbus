package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "wp_posts")
public class WordpressPost {

	@Id
	@Column(name = "ID")
	@JsonProperty(value = "id")
	private long id;
	
	@Column(name = "post_title")
	@JsonProperty(value = "post_title")
	private String postTitle;
	
	@Column(name = "post_content")
	@JsonProperty(value = "post_content")
	private String postContent;
	
	@Column(name = "guid")
	@JsonProperty(value = "guid")
	private String guid;
	

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
