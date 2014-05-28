package com.proptiger.data.repo.trend;

import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.data.model.filter.AbstractQueryBuilder;
import com.proptiger.data.model.filter.JPAQueryBuilder;
import com.proptiger.data.model.trend.Graph;
import com.proptiger.data.pojo.FIQLSelector;

public class GraphDaoImpl {
    @Autowired
    GraphDao                       graphDao;

    @Autowired
    public EntityManagerFactory emf;

    public List<Graph> getFilteredGraphs(FIQLSelector fiqlSelector) {
        AbstractQueryBuilder<Graph> builder = new JPAQueryBuilder<>(emf.createEntityManager(), Graph.class);
        builder.buildQuery(fiqlSelector);
        return builder.retrieveResults();
    }
}
