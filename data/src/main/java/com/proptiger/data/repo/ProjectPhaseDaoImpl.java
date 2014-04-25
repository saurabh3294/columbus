package com.proptiger.data.repo;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.data.model.ProjectPhase;
import com.proptiger.data.model.filter.AbstractQueryBuilder;
import com.proptiger.data.model.filter.JPAQueryBuilder;
import com.proptiger.data.pojo.FIQLSelector;

public class ProjectPhaseDaoImpl {
    @Autowired
    private ProjectPhaseDao     projectPhaseDao;

    @Autowired
    public EntityManagerFactory emf;

    public List<ProjectPhase> getFilteredPhases(FIQLSelector fiqlSelector) {
        EntityManager entityManager = emf.createEntityManager();
        AbstractQueryBuilder<ProjectPhase> builder = new JPAQueryBuilder<>(entityManager, ProjectPhase.class);
        builder.buildQuery(fiqlSelector);
        List<ProjectPhase> result = builder.retrieveResults();
        entityManager.close();
        return result;
    }
}