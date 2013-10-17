package com.proptiger.data.model.portfolio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.data.meta.FieldMetaInfo;

@Entity
@Table(name = "portfolio_listings_price")
public class PortfolioListingPrice {

	@Id
	@FieldMetaInfo(displayName = "Property Id", description = "Property Id")
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	private Integer portfolioListingId;
	
}
