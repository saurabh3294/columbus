package com.proptiger.data.model.seller;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Rajeev Pandey
 *
 */
@Table(name = "academic_qualifications")
public class AcademicQualification {
	@Id
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "qualification")
	private String qualification;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}
	
}
