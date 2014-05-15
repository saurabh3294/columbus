package com.proptiger.data.repo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.data.model.ListingPrice.CustomCurrentListingPrice;
import com.proptiger.data.model.b2b.Status;
import com.proptiger.data.model.enums.DataVersion;

/**
 * 
 * @author azi
 * 
 */

public class ListingPriceDaoImpl {
    @Autowired
    private EntityManagerFactory emf;

    public List<CustomCurrentListingPrice> getPrices(List<Integer> listingIds, DataVersion version) {
        List<CustomCurrentListingPrice> listingPrices = new ArrayList<>();
        if (!listingIds.isEmpty()) {
            EntityManager em = emf.createEntityManager();

            Query query = em
                    .createQuery("select NEW com.proptiger.data.model.ListingPrice$CustomCurrentListingPrice(listingId, substring_index(group_concat(pricePerUnitArea,effectiveDate,effectiveDate,1,effectiveDate,-1,effectiveDate), ',', 1), substring_index(group_concat(effectiveDate), ',', 1)) from ListingPrice where listingId in (?1) and version = ?2 and status = ?3 group by listingId");

            query.setParameter(1, listingIds);
            query.setParameter(2, version);
            query.setParameter(3, Status.Active);

            listingPrices = (List<CustomCurrentListingPrice>) query.getResultList();
            em.close();
        }
        return listingPrices;
    }

    public List<CustomCurrentListingPrice> getPrices(List<Integer> listingIds) {
        return getPrices(listingIds, DataVersion.Website);
    }
}