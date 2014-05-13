package com.proptiger.data.repo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.data.model.ListingPrice;
import com.proptiger.data.model.ListingPrice.CustomCurrentListingPrice;
import com.proptiger.data.model.b2b.Status;
import com.proptiger.data.model.enums.DataVersion;
import com.proptiger.data.util.DateUtil;

/**
 * 
 * @author azi
 * 
 */

public class ListingPriceDaoImpl {
    @Autowired
    private EntityManagerFactory emf;

    public List<CustomCurrentListingPrice> getPricesfromListingIds(List<Integer> listingIds, DataVersion version) {
        List<CustomCurrentListingPrice> listingPrices = new ArrayList<>();
        if (!listingIds.isEmpty()) {
            EntityManager em = emf.createEntityManager();

            Query query = em
                    .createNativeQuery("select listing_id listingId, substring_index(group_concat(price_per_unit_area order by effective_date desc), ',', 1) pricePerUnitArea, substring_index(group_concat(effective_date order by effective_date desc), ',', 1) effectiveMonth from " + ListingPrice.class
                            .getAnnotation(Table.class).name()
                            + " where listing_id in (?1) and version = ?2 and status = ?3 group by listing_id");

            query.setParameter(1, listingIds);
            query.setParameter(2, version.name());
            query.setParameter(3, Status.Active.name());

            List<Object[]> resultList = query.getResultList();
            em.close();

            for (Object[] objects : resultList) {
                listingPrices.add(new CustomCurrentListingPrice(Integer.parseInt(objects[0].toString()), Integer
                        .parseInt(objects[1].toString()), DateUtil.parseYYYYmmddStringToDate(objects[2].toString())));
            }
        }
        return listingPrices;
    }

    public List<CustomCurrentListingPrice> getWebsitePricesfromListingIds(List<Integer> listingIds) {
        return getPricesfromListingIds(listingIds, DataVersion.Website);
    }
}