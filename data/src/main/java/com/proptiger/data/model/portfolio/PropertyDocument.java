package com.proptiger.data.model.portfolio;

import java.util.Date;

import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * @author Rajeev Pandey
 *
 */
@ResourceMetaInfo(name = "PropertyDocument")
public class PropertyDocument {

	@FieldMetaInfo(displayName = "Property Document Id", description = "Property Document Id")
	private int id;
	
	@FieldMetaInfo(dataType = DataType.DATE, displayName = "date", description = "date")
	private Date date;
	
	@FieldMetaInfo(dataType = DataType.OBJECT, displayName = "Document Type", description = "Document Type")
	private DocumentType documentType;
	
	@FieldMetaInfo(displayName = "Name", description = "Name")
	private String name;
	
	@FieldMetaInfo(displayName = "Document Link", description = "Document Link")
	private String documentLink;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDocumentLink() {
		return documentLink;
	}

	public void setDocumentLink(String documentLink) {
		this.documentLink = documentLink;
	}
	
	
}
