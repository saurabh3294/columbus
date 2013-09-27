package com.proptiger.data.model.portfolio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

@Entity
@Table(name = "FORUM_USER_SAVED_SEARCHES")
@ResourceMetaInfo(name = "ForumUserSavedSearch")
public class ForumUserSavedSearch {

	@Id
	@FieldMetaInfo(displayName = "Saved Search Id", description = "Saved Search Id")
	@Column(name = "ID")
	private Integer id;
	
	@FieldMetaInfo(displayName = "User Id", description = "User Id")
	@Column(name = "USER_ID")
	private Integer userId; 

	@FieldMetaInfo(displayName = "Search Query", description = "Search Query")
	@Column(name = "SEARCH_QUERY")
	private String searchQuery;
	
	@FieldMetaInfo(displayName = "Name", description = "Name")
	@Column(name = "NAME")
	private String name;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getSearchQuery() {
		return searchQuery;
	}

	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}