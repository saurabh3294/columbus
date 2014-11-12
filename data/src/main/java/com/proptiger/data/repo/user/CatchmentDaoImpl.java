package com.proptiger.data.repo.user;

import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.data.model.Catchment;
import com.proptiger.data.model.filter.AbstractQueryBuilder;
import com.proptiger.data.model.filter.JPAQueryBuilder;

public class CatchmentDaoImpl {
    @Autowired
    CatchmentDao                catchmentDao;

    @Autowired
    public EntityManagerFactory emf;

    public List<Catchment> getFilteredCatchments(FIQLSelector fiqlSelector) {
        AbstractQueryBuilder<Catchment> builder = new JPAQueryBuilder<>(emf.createEntityManager(), Catchment.class);
        builder.buildQuery(fiqlSelector);
        return builder.retrieveResults();
    }
}