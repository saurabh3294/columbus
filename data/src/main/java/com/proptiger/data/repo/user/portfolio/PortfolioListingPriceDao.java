package com.proptiger.data.repo.user.portfolio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.core.model.proptiger.PortfolioListingPrice;

public interface PortfolioListingPriceDao extends JpaRepository<PortfolioListingPrice, Integer> {

}
