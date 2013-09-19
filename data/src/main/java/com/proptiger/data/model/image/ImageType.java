package com.proptiger.data.model.image;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.proptiger.data.model.ObjectType;

@Entity(name = "ImageType")
public class ImageType implements Serializable {
	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
	@Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@ManyToOne(targetEntity = ObjectType.class)
	@Transient
	@JoinColumn(name = "ObjectType_id", referencedColumnName = "id")
	private ObjectType objectType;
	
	@Column(name = "ObjectType_id")
	private String objectTypeId;
	
	@Column(name = "type")
	private String type;

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the objectType
	 */
	public ObjectType getObjectType() {
		return objectType;
	}

	/**
	 * @param objectType the objectType to set
	 */
	public void setObjectType(ObjectType objectType) {
		this.objectType = objectType;
	}

	/**
	 * @return the objectTypeId
	 */
	public String getObjectTypeId() {
		return objectTypeId;
	}

	/**
	 * @param objectTypeId the objectTypeId to set
	 */
	public void setObjectTypeId(String objectTypeId) {
		this.objectTypeId = objectTypeId;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

}
