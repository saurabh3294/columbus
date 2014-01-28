package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.meta.ResourceMetaInfo;

@Entity
@Table(name="cms.video_links")
@JsonInclude(Include.NON_NULL)
//@ResourceMetaInfo
//@JsonFilter("fieldFilter")
public class VideoLinks implements BaseModel {
	/*public enum Categories{
		WALKTHROUGH("Walkthrough"),
		SAMPLEFLAT("Sample flat"),
		PRESENTATION("Presentation");
		
		private String name;
		
		private Categories(String name){
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}*/
	
	@Id
	@Column(name = "video_id")
	private int videoId;
	
	@Column(name = "table_id")
	@JsonIgnore
	private int tableId;
	
	@Column(name = "table_name")
	@JsonIgnore
	private String tableName;
	
	@Column(name = "category")
	//@Enumerated(EnumType.STRING)
	private String category;
	
	@Column(name = "video_url")
	private String videoUrl;

	public int getVideoId() {
		return videoId;
	}

	public void setVideoId(int videoId) {
		this.videoId = videoId;
	}

	public int getTableId() {
		return tableId;
	}

	public void setTableId(int tableId) {
		this.tableId = tableId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}
	
}
