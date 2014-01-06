package com.proptiger.data.model.seller;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.data.model.BaseModel;
import com.proptiger.data.model.enums.ContactNumberType;


/**
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "cms.contact_numbers")
public class ContactNumber  implements BaseModel{

	@Id
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "table_name")
	private String tableName;
	
	@Column(name = "table_id")
	private Integer tableId;
	
	@Column(name = "contry_code")
	private Integer contryCode;
	
	@Column(name = "contact_no")
	private String contactNumber;
	
	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private ContactNumberType type;

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

	public Integer getContryCode() {
		return contryCode;
	}

	public void setContryCode(Integer contryCode) {
		this.contryCode = contryCode;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public ContactNumberType getType() {
		return type;
	}

	public void setType(ContactNumberType type) {
		this.type = type;
	}
	
	
}
