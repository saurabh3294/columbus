package com.proptiger.data.repo.marketplace;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.core.pojo.response.PaginatedResponse;
import com.proptiger.data.model.filter.AbstractQueryBuilder;
import com.proptiger.data.model.filter.JPAQueryBuilder;
import com.proptiger.data.model.marketplace.LeadOffer;
import com.proptiger.data.model.marketplace.LeadTask;

/**
 * @author Rajeev Pandey
 * 
 */
public class LeadOfferDaoImpl {

    private static Logger        logger = LoggerFactory.getLogger(LeadOfferDaoImpl.class);

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

    // TODO should be done with FIQL selector using method
    // getLeadOffers(FIQLSelector selector)
    /**
     * Get lead offers using native query
     * 
     * @param agentId
     * @param statusIds
     * @param dueDate
     * @return
     */

    public List<LeadOffer> getPage(int agentId, List<Integer> statusIds, String dueDate, FIQLSelector selector) {
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

        cq.orderBy(cb.asc(leadTaskJoin.<Date> get("scheduledFor")), cb.desc(c.get("id")));

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
            conditions
                    .add(cb.between(leadTaskJoin.<Date> get("scheduledFor"), cb.literal(today), cb.literal(tomorrow)));
        }
        else if ("overdue".equalsIgnoreCase(dueDate)) {
            conditions.add(cb.lessThan(leadTaskJoin.<Date> get("scheduledFor"), cb.currentTimestamp()));
        }
        cq.where(conditions.toArray(new Predicate[0]));

        int flagSinglePresent = 0;

        if (selector.getSort() != null && selector.getSort() != "") {
            String[] sortFields = selector.getSort().split(",");
            List<Order> orders = new ArrayList<Order>();
            for (String sortField : sortFields) {
                if (sortField.substring(0, 1).equals("-")) {
                    try {
                        orders.add(cb.desc(c.get(sortField.substring(1))));
                        flagSinglePresent = 1;
                    }
                    catch (Exception e) {
                    }
                }
                else {
                    try {
                        orders.add(cb.asc(c.get(sortField)));
                        flagSinglePresent = 1;
                    }
                    catch (Exception e) {
                    }
                }

            }
            if (flagSinglePresent == 1) {
                cq.orderBy(orders);
            }
        }

        Query query = em.createQuery(cq);

        query.setFirstResult(selector.getStart());
        query.setMaxResults(selector.getRows());

        List<LeadOffer> leadOffers = query.getResultList();
        return leadOffers;
    }

    public List<Long> getTotalCount(int agentId, List<Integer> statusIds, String dueDate, FIQLSelector selector) {
        EntityManager emCount = emf.createEntityManager();
        CriteriaBuilder cbCount = emCount.getCriteriaBuilder();
        CriteriaQuery<Long> cqCount = cbCount.createQuery(Long.class);
        Root<LeadOffer> cCount = cqCount.from(LeadOffer.class);

        cqCount.select(cbCount.count(cCount));

        Join<LeadOffer, LeadTask> leadTaskJoinCount = cCount.join("nextTask", JoinType.LEFT);

        cqCount.where(cbCount.equal(cCount.get("agentId"), agentId));

        List<Predicate> conditionsCount = new ArrayList<>();
        conditionsCount.add(cbCount.equal(cCount.get("agentId"), agentId));

        cqCount.orderBy(cbCount.asc(leadTaskJoinCount.<Date> get("scheduledFor")), cbCount.asc(cCount.get("createdAt")));

        if (statusIds != null && !statusIds.isEmpty()) {
            conditionsCount.add(cCount.get("statusId").in(statusIds));
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
            conditionsCount.add(cbCount.between(
                    leadTaskJoinCount.<Date> get("scheduledFor"),
                    cbCount.literal(today),
                    cbCount.literal(tomorrow)));
        }
        else if ("overdue".equalsIgnoreCase(dueDate)) {
            conditionsCount.add(cbCount.lessThan(
                    leadTaskJoinCount.<Date> get("scheduledFor"),
                    cbCount.currentTimestamp()));
        }

        cqCount.where(conditionsCount.toArray(new Predicate[0]));
        List<Long> countTotal = emCount.createQuery(cqCount).getResultList();

        return countTotal;
    }

    public PaginatedResponse<List<LeadOffer>> getLeadOffers(
            int agentId,
            List<Integer> statusIds,
            String dueDate,
            FIQLSelector selector) {
        PaginatedResponse<List<LeadOffer>> paginatedResponse = new PaginatedResponse<>();
        List<Long> countTotal = getTotalCount(agentId, statusIds, dueDate, selector);
        paginatedResponse.setTotalCount(countTotal.get(0));

        List<LeadOffer> leadOffers;
        if (selector.getStart() > countTotal.get(0)) {
            leadOffers = null;
        }
        else {
            leadOffers = getPage(agentId, statusIds, dueDate, selector);
        }
        paginatedResponse.setResults(leadOffers);
        return paginatedResponse;
    }
}
