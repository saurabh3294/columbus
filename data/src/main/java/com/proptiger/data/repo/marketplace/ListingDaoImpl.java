package com.proptiger.data.repo.marketplace;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.core.enums.DataVersion;
import com.proptiger.core.enums.Status;
import com.proptiger.core.model.cms.Listing;
import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.core.pojo.response.PaginatedResponse;
import com.proptiger.data.model.filter.AbstractQueryBuilder;
import com.proptiger.data.model.filter.JPAQueryBuilder;

/**
 * @author Rajeev Pandey
 * 
 */
public class ListingDaoImpl {

    @Autowired
    private EntityManagerFactory emf;

    public PaginatedResponse<List<Listing>> getListings(FIQLSelector selector) {
        EntityManager em = emf.createEntityManager();
        PaginatedResponse<List<Listing>> paginatedResponse = new PaginatedResponse<>();
        AbstractQueryBuilder<Listing> builder = new JPAQueryBuilder<>(em, Listing.class);
        builder.buildQuery(selector);
        paginatedResponse.setResults(builder.retrieveResults());
        paginatedResponse.setTotalCount(builder.retrieveCount());
        em.close();
        return paginatedResponse;

    }

    public List<Listing> findListings(Integer userId, DataVersion dataVersion, Status status, FIQLSelector selector) {
        EntityManager em = emf.createEntityManager();
        Query query = em
                .createQuery("select l from Listing l left join fetch l.projectSupply left join fetch l.currentListingPrice join fetch l.property prop join fetch prop.project as p join fetch p.projectStatusMaster join fetch p.builder join fetch p.locality pl join fetch pl.suburb pls join fetch pls.city where l.sellerId=?1 and p.version=?2  and l.status=?3 order by l.id desc");
        query.setParameter(1, userId);
        query.setParameter(2, dataVersion);
        query.setParameter(3, status);

        query.setFirstResult(selector.getStart());
        query.setMaxResults(selector.getRows());

        List<Listing> listings = query.getResultList();

        return listings;
    }

}
