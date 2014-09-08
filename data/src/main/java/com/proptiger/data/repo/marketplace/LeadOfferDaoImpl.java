package com.proptiger.data.repo.marketplace;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.data.model.filter.AbstractQueryBuilder;
import com.proptiger.data.model.filter.JPAQueryBuilder;
import com.proptiger.data.model.marketplace.LeadOffer;
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
        
        StringBuilder queryStr = new StringBuilder("select * from marketplace.lead_offers lo join marketplace.leads l on lo.lead_id=l.id ");
        queryStr.append(" join marketplace.master_lead_statuses mls on mls.id=lo.status_id ");
        queryStr.append(" left join marketplace.lead_tasks nt on nt.id=lo.next_task_id ");
        queryStr.append(" where lo.agent_id = ").append(agentId).append(" ");
        if(statusIds != null && !statusIds.isEmpty()){
            boolean firstId = true;
            queryStr.append(" and lo.status_id in (");
            for(Integer id: statusIds){
                if(!firstId){
                    queryStr.append(",");
                }
                queryStr.append(id);
                firstId = false;
            }
            queryStr.append(") "); 
        }
        if(dueDate != null && !dueDate.isEmpty()){
            if(dueDate.equalsIgnoreCase("today")){
                queryStr.append(" and nt.scheduled_for >= :now").append(" and nt.scheduled_for < :tomorrow");
            }
            else if(dueDate.equalsIgnoreCase("overdue")){
                queryStr.append(" and nt.scheduled_for <  :now");
            }
            else{
                logger.error("dueDate value {} not supported, so ignored",dueDate);
            }
        }
        queryStr.append(" order by nt.scheduled_for desc, lo.created_at asc");
        queryStr.append(" limit ").append(selector.getRows()).append(" offset ").append(selector.getStart());
        Query query = em
                .createNativeQuery(queryStr.toString(), LeadOffer.class);
        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();
        cal.add(Calendar.DATE, 1);
        Date tomorrow = cal.getTime();
        
        if(dueDate != null && !dueDate.isEmpty()){
            if(dueDate.equalsIgnoreCase("today")){
                query.setParameter("now", now, TemporalType.TIMESTAMP);
                query.setParameter("tomorrow", tomorrow, TemporalType.TIMESTAMP);
            }
            else if(dueDate.equalsIgnoreCase("overdue")){
                query.setParameter("now", now, TemporalType.TIMESTAMP);
            }
        }
        
        List<LeadOffer> leadOffers = query.getResultList();
        paginatedResponse.setResults(leadOffers);
        paginatedResponse.setTotalCount(leadOffers.size());
        return paginatedResponse;
    }
    
}
