package com.proptiger.data.repo.marketplace;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.data.model.filter.AbstractQueryBuilder;
import com.proptiger.data.model.filter.JPAQueryBuilder;
import com.proptiger.data.model.marketplace.LeadOffer;
import com.proptiger.data.model.marketplace.LeadTask;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.PaginatedResponse;

/**
 * @author Rajeev Pandey
 *
 */
public class LeadOfferDaoImpl {

    private static Logger             logger = LoggerFactory.getLogger(LeadOfferDaoImpl.class);

    @Autowired
    private EntityManagerFactory emf;

    public PaginatedResponse<List<LeadOffer>> getLeadOffers(FIQLSelector selector) {
        EntityManager em = emf.createEntityManager();
        AbstractQueryBuilder<LeadOffer> leadOffer = new JPAQueryBuilder<>(em, LeadOffer.class);
        leadOffer.buildQuery(selector);
        PaginatedResponse<List<LeadOffer>> paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setResults(leadOffer.retrieveResults());
        paginatedResponse.setTotalCount(leadOffer.retrieveCount());
        em.close();
        return paginatedResponse;
    }
    
    //TODO should be done with FIQL selector using method getLeadOffers(FIQLSelector selector)
    /**
     * Get lead offers using native query
     *
     * @param agentId
     * @param statusIds
     * @param dueDate
     * @return
     */
    public PaginatedResponse<List<LeadOffer>> getLeadOffers(int agentId, List<Integer> statusIds, String dueDate, FIQLSelector selector){
        PaginatedResponse<List<LeadOffer>> paginatedResponse = new PaginatedResponse<>();
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LeadOffer> cq = cb.createQuery(LeadOffer.class);
        Root<LeadOffer> c = cq.from(LeadOffer.class);
        c.fetch("lead");
        c.fetch("masterLeadOfferStatus");
        Join<LeadOffer, LeadTask> leadTaskJoin = c.join("nextTask", JoinType.LEFT);

        cq.select(c);
        cq.where(cb.equal(c.get("agentId"), agentId));
        
        List<Predicate> conditions = new ArrayList<>();
        conditions.add(cb.equal(c.get("agentId"), agentId));

        cq.orderBy(cb.asc(leadTaskJoin.<Date>get("scheduledFor")), cb.asc(c.get("createdAt")));

        if (statusIds != null && !statusIds.isEmpty()) {
            conditions.add(c.get("statusId").in(statusIds));
        }

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date today = cal.getTime();

        try {
            today = sdf.parse(sdf.format(cal.getTime()));
        }
        catch (ParseException e) {
        }

        cal.add(Calendar.DATE, 1);
        Date tomorrow = cal.getTime();

        if ("today".equalsIgnoreCase(dueDate)) {
            conditions.add(cb.between(leadTaskJoin.<Date>get("scheduledFor"), cb.literal(today), cb.literal(tomorrow)));
        }
        else if ("overdue".equalsIgnoreCase(dueDate)) {
            conditions.add(cb.lessThan(leadTaskJoin.<Date>get("scheduledFor"), cb.currentTimestamp()));
        }

        cq.where(conditions.toArray(new Predicate[0]));
        List<LeadOffer> leadOffers = em.createQuery(cq).getResultList();
        paginatedResponse.setResults(leadOffers);
        paginatedResponse.setTotalCount(leadOffers.size());
        return paginatedResponse;
    }
}
