package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "wordpress.wp_terms")
public class WordpressTerms extends BaseModel{

	private static final long serialVersionUID = 806356402805138067L;

	@Id
	@Column(name = "term_id")
	private long termId;
	
	@Column(name = "name")
	private String name;

	public long getTermId() {
		return termId;
	}

	public void setTermId(long termId) {
		this.termId = termId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
