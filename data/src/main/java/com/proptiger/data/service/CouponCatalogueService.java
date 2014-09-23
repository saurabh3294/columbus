package com.proptiger.data.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.model.CouponCatalogue;
import com.proptiger.data.repo.CouponCatalogueDao;

@Service
public class CouponCatalogueService {

    @Autowired
    private CouponCatalogueDao couponCatalogueDao;

    /**
     * This method will return the coupon catalogue for a propertyId
     * 
     * @param propertyId
     * @return
     */
    public CouponCatalogue getCouponCatalogueByPropertyId(int propertyId) {
        return couponCatalogueDao.findByPropertyIdAndInventoryLeftGreaterThanAndPurchaseExpiryAtGreaterThan(
                propertyId,
                0,
                new Date());
    }

    /**
     * This method will return the coupon catalogue for the properties.
     * 
     * @param propertyIds
     * @return
     */
    public List<CouponCatalogue> getCouponCataloguesByPropertyIds(List<Integer> propertyIds) {
        return couponCatalogueDao.findByPropertyIdInAndInventoryLeftGreaterThanAndPurchaseExpiryAtGreaterThan(
                propertyIds,
                0,
                new Date());
    }

    /**
     * This method will create a Map Of propertyId and Coupon Catalogue which
     * have coupons left to be sold.
     * 
     * @param propertyIds
     * @return
     */
    public Map<Integer, CouponCatalogue> getCouponCatalogueMapByPropertyIds(List<Integer> propertyIds) {
        List<CouponCatalogue> listCouponCatalogue = getCouponCataloguesByPropertyIds(propertyIds);

        Map<Integer, CouponCatalogue> map = new HashMap<Integer, CouponCatalogue>();

        if (listCouponCatalogue == null || listCouponCatalogue.isEmpty())
            return map;

        for (CouponCatalogue couponCatalogue : listCouponCatalogue) {
            map.put(couponCatalogue.getPropertyId(), couponCatalogue);
        }

        return map;
    }

    @Transactional
    public CouponCatalogue updateCouponCatalogueInventoryLeft(int couponId, int inventoryCount) {
        Integer numberOfRowsAffected = couponCatalogueDao.updateCouponInventory(couponId, inventoryCount);
        if (numberOfRowsAffected != null && numberOfRowsAffected > 0) {
            return couponCatalogueDao.findOne(couponId);
        }

        return null;
    }

    public CouponCatalogue findOne(int couponId) {
        return couponCatalogueDao.findByIdAndPurchaseExpiryAtGreaterThan(couponId, new Date());
    }
}
