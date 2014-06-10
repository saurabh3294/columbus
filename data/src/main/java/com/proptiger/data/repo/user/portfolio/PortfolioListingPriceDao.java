package com.proptiger.data.repo.user.portfolio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.user.portfolio.PortfolioListingPrice;

public interface PortfolioListingPriceDao extends JpaRepository<PortfolioListingPrice, Integer> {

}
