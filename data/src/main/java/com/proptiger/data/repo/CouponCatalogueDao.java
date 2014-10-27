package com.proptiger.data.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.core.model.cms.CouponCatalogue;

public interface CouponCatalogueDao extends JpaRepository<CouponCatalogue, Integer> {
    CouponCatalogue findByPropertyIdAndInventoryLeftGreaterThanAndPurchaseExpiryAtGreaterThan(
            int propertyId,
            int inventoryLeft,
            Date date);

    CouponCatalogue findByIdAndPurchaseExpiryAtGreaterThan(int couponId, Date date);

    List<CouponCatalogue> findByPropertyIdInAndInventoryLeftGreaterThanAndPurchaseExpiryAtGreaterThan(
            List<Integer> propertyId,
            int inventoryLeft,
            Date date);

    @Modifying
    @Query("UPDATE CouponCatalogue  set inventoryLeft = inventoryLeft + ?2 where id=?1 AND purchaseExpiryAt > ?3 AND inventoryLeft + ?2 BETWEEN 0 AND totalInventory ")
    int updateCouponInventory(int couponId, int inventoryCount, Date date);
}
