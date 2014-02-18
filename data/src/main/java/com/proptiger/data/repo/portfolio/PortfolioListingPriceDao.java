package com.proptiger.data.repo.portfolio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.portfolio.PortfolioListingPrice;

public interface PortfolioListingPriceDao extends JpaRepository<PortfolioListingPrice, Integer> {

}
