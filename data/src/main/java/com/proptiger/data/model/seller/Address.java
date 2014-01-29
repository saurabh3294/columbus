package com.proptiger.data.model.seller;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.proptiger.data.model.BaseModel;

/**
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "cms.addresses")
@JsonFilter("fieldFilter")
public class Address  extends BaseModel{

	@Id
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "table_name")
	private String tableName;
	
	@Column(name = "table_id")
	private Integer tableId;
	
	@Column(name = "address_line_1")
	private String addressLine1;
	
	@Column(name = "address_line_2")
	private String addressLine2;
	
	@Column(name = "city_id")
	private Integer cityId;
	
	@Column(name = "pincode")
	private String pinCode;
	
	@Column(name = "locality_id")
	private Integer localityId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Integer getTableId() {
		return tableId;
	}

	public void setTableId(Integer tableId) {
		this.tableId = tableId;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public Integer getLocalityId() {
		return localityId;
	}

	public void setLocalityId(Integer localityId) {
		this.localityId = localityId;
	}
	
}
