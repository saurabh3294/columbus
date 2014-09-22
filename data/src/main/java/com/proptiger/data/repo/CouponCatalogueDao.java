package com.proptiger.data.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.CouponCatalogue;

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
    @Query("UPDATE CouponCatalogue cc set inventory_left = inventory_left + ?2 where id=?1")
    int updateCouponInventory(int couponId, int inventoryCount);
}
