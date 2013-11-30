package com.proptiger.data.model.portfolio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;

/**
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "portfolio_property_price")
@JsonFilter("fieldFilter")
public class PortfolioPropertyPrice {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "portfolio_property_id")
	private Integer portfolioPropertyId;
	
	@Column(name = "amount")
	private Double amount;
	
	@Column(name = "component_name")
	private String componentName;
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_at")
	private Date updatedAt;
}
