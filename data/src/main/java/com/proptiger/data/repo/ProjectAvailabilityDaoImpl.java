package com.proptiger.data.repo;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;

public class ProjectAvailabilityDaoImpl {

    @Autowired
    private EntityManagerFactory emf;

    public Integer getSumCurrentAvailabilityFromSupplyIds(Set<Integer> supplyIds, String endMonth) {
        String monthCondition = "";
        if(endMonth != null){
            monthCondition = " and effective_month<='" + endMonth + "' ";
        }
            
        Integer result = null;
        if (!supplyIds.isEmpty()) {
            EntityManager em = emf.createEntityManager();
            Query query = em
                    .createNativeQuery("select sum(availability) "
                            + "from (select substring_index(group_concat(availability order by effective_month desc), ',', 1) availability "
                            + "from cms.project_availabilities "
                            + "where project_supply_id in (?1) " + monthCondition
                            + "group by project_supply_id) t");
            query.setParameter(1, supplyIds);
            Object resultObject = query.getSingleResult();
            if (resultObject != null) {
                result = ((Double) query.getSingleResult()).intValue();
            }
            em.close();

        }
        return result;
    }

}
