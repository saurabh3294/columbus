package com.proptiger.data.repo.marketplace;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.data.model.marketplace.LeadRequirement;

/**
 * @author Rajeev Pandey
 *
 */
public class LeadRequirementsDaoImpl {
    @Autowired
    private EntityManagerFactory emf;
    
    public List<LeadRequirement> getRequirements(Integer bedroom, Integer localityId, Integer projectId, int leadId) {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LeadRequirement> cq = cb.createQuery(LeadRequirement.class);
        Root<LeadRequirement> c = cq.from(LeadRequirement.class);
        List<Predicate> conditions = new ArrayList<>();

        if (bedroom == null) {
            conditions.add(cb.isNull(c.get("bedroom")));
        }
        else {
            conditions.add(cb.equal(c.get("bedroom"), bedroom));
        }

        if (localityId == null) {
            conditions.add(cb.isNull(c.get("localityId")));
        }
        else {
            conditions.add(cb.equal(c.get("localityId"), localityId));
        }

        if (projectId == null) {
            conditions.add(cb.isNull(c.get("projectId")));
        }
        else {
            conditions.add(cb.equal(c.get("projectId"), projectId));
        }
        
        conditions.add(cb.equal(c.get("leadId"), leadId));        
        cq.where(conditions.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList();
    }
    
}
