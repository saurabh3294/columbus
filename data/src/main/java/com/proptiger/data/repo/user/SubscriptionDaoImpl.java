package com.proptiger.data.repo.user;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.core.model.filter.AbstractQueryBuilder;
import com.proptiger.core.model.filter.JPAQueryBuilder;
import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.data.model.Subscription;

public class SubscriptionDaoImpl {
    @Autowired
    public EntityManagerFactory emf;

    public List<Subscription> getSubscriptions(FIQLSelector fiqlSelector) {
        EntityManager em = emf.createEntityManager();
        AbstractQueryBuilder<Subscription> builder = new JPAQueryBuilder<>(em, Subscription.class);
        builder.buildQuery(fiqlSelector);
        List<Subscription> list = builder.retrieveResults();
        em.close();
        return list;
    }
    

}
