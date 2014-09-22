package com.proptiger.data.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.CouponCatalogue;

public interface CouponCatalogueDao extends JpaRepository<CouponCatalogue, Integer> {
    CouponCatalogue findByPropertyIdAndInventoryLeftGreaterThanAndPurchaseExpiryAtGreaterThan(int propertyId, int inventoryLeft, Date date);

    List<CouponCatalogue> findByPropertyIdInAndInventoryLeftGreaterThanAndPurchaseExpiryAtGreaterThan(List<Integer> propertyId, int inventoryLeft, Date date);
}
