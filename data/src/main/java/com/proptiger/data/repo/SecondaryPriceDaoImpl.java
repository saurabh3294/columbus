package com.proptiger.data.repo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.core.enums.UnitType;
import com.proptiger.data.model.ProjectPhase.CustomCurrentPhaseSecondaryPrice;
import com.proptiger.data.model.SecondaryPrice;
import com.proptiger.data.util.DateUtil;

public class SecondaryPriceDaoImpl {
    @Autowired
    private EntityManagerFactory emf;

    public List<CustomCurrentPhaseSecondaryPrice> getSecondaryPriceFromPhaseIds(List<Integer> phaseIds) {
        List<CustomCurrentPhaseSecondaryPrice> phaseSecondaryPrices = new ArrayList<>();
        if (!phaseIds.isEmpty()) {
            EntityManager em = emf.createEntityManager();
            Query query = em
                    .createNativeQuery("select phase_id phaseId, unit_type unitType, cast(substring_index(group_concat(avg_price order by effective_date desc), ',', 1) as char(20)), cast(substring_index(group_concat(effective_date order by effective_date desc), ',', 1) as char(10)) effectiveMontha from (select phase_id, unit_type, effective_date, avg((MIN_PRICE+MAX_PRICE)/2) avg_price from " + SecondaryPrice.class
                            .getAnnotation(Table.class).name()
                            + " where phase_id in (?1) group by phase_id, unit_type, effective_date) t group by phase_id, unit_type");
            query.setParameter(1, phaseIds);

            List<Object[]> resultList = query.getResultList();
            em.close();

            for (Object[] objects : resultList) {
                phaseSecondaryPrices.add(new CustomCurrentPhaseSecondaryPrice(
                        Integer.valueOf(objects[0].toString()),
                        UnitType.valueOf(objects[1].toString()),
                        DateUtil.parseYYYYmmddStringToDate(objects[3].toString()),
                        Float.valueOf(objects[2].toString()).intValue()));
            }
        }
        return phaseSecondaryPrices;
    }
}