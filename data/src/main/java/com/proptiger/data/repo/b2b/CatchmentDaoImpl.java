package com.proptiger.data.repo.b2b;

import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.data.model.b2b.Catchment;
import com.proptiger.data.model.filter.AbstractQueryBuilder;
import com.proptiger.data.model.filter.JPAQueryBuilder;
import com.proptiger.data.pojo.FIQLSelector;

public class CatchmentDaoImpl {
    @Autowired
    CatchmentDao                catchmentDao;

    @Autowired
    public EntityManagerFactory emf;

    public Integer countByName(String name) {
        return catchmentDao.findByName(name).size();
    }

    public List<Catchment> getFilteredCatchments(FIQLSelector fiqlSelector) {
        AbstractQueryBuilder<Catchment> builder = new JPAQueryBuilder<>(emf.createEntityManager(), Catchment.class);
        builder.buildQuery(fiqlSelector);
        return builder.retrieveResults();
    }
}