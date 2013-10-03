package com.proptiger.data.dto;

/**
 * This POJO acts as a data transfer object for Dashboard boject
 * @author Rajeev Pandey
 *
 */
public class DashboardDto {
	
	private Integer id;
	private String name;
	private int totalRows;
	private int totalColumn;
	private Integer userId;
	
	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the totalRows
	 */
	public int getTotalRows() {
		return totalRows;
	}
	/**
	 * @param totalRows the totalRows to set
	 */
	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}
	/**
	 * @return the totalColumn
	 */
	public int getTotalColumn() {
		return totalColumn;
	}
	/**
	 * @param totalColumn the totalColumn to set
	 */
	public void setTotalColumn(int totalColumn) {
		this.totalColumn = totalColumn;
	}
	/**
	 * @return the userId
	 */
	public Integer getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	
}
