/**
 * 
 */
package com.proptiger.data.repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proptiger.core.enums.SortOrder;
import com.proptiger.core.enums.filter.Operator;
import com.proptiger.core.model.cms.Suburb;
import com.proptiger.core.model.filter.SolrQueryBuilder;
import com.proptiger.core.pojo.Selector;
import com.proptiger.core.pojo.SortBy;
import com.proptiger.core.repo.SolrDao;
import com.proptiger.data.model.SolrResult;

/**
 * @author mandeep
 * 
 */
@Repository
public class SuburbDaoImpl {
    @Autowired
    private SolrDao solrDao;

    public List<Suburb> getSuburbs(Selector selector) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("*:*");
        solrQuery.setFilterQueries("DOCUMENT_TYPE:SUBURB");

        SolrQueryBuilder<Suburb> solrQueryBuilder = new SolrQueryBuilder<>(solrQuery, Suburb.class);
        if (selector.getSort() == null) {
            selector.setSort(new LinkedHashSet<SortBy>());
        }

        selector.getSort().addAll(getDefaultSort());
        solrQueryBuilder.buildQuery(selector, null);

        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
        List<SolrResult> response = queryResponse.getBeans(SolrResult.class);

        List<Suburb> data = new ArrayList<>();
        for (int i = 0; i < response.size(); i++) {
            data.add(response.get(i).getProject().getLocality().getSuburb());
        }

        return data;
    }

    private Set<SortBy> getDefaultSort() {
        Set<SortBy> sortBySet = new LinkedHashSet<SortBy>();
        SortBy sortBy = new SortBy();
        sortBy.setField("priority");
        sortBy.setSortOrder(SortOrder.ASC);
        sortBySet.add(sortBy);
        return sortBySet;
    }

    public Suburb getSuburb(int suburbId) {

        Selector selector = new Selector();

        Map<String, List<Map<String, Map<String, Object>>>> filter = new HashMap<String, List<Map<String, Map<String, Object>>>>();
        List<Map<String, Map<String, Object>>> list = new ArrayList<>();
        Map<String, Map<String, Object>> searchType = new HashMap<>();
        Map<String, Object> filterCriteria = new HashMap<>();

        filterCriteria.put("id", suburbId);
        searchType.put(Operator.equal.name(), filterCriteria);
        list.add(searchType);
        filter.put(Operator.and.name(), list);

        selector.setFilters(filter);

        List<Suburb> suburbs = getSuburbs(selector);
        if (suburbs == null || suburbs.size() < 1)
            return null;

        return suburbs.get(0);
    }
    /*
     * @Autowired private EntityManagerFactory emf;
     * 
     * public List<Suburb> getSuburbs(Selector selector) { EntityManager em =
     * emf.createEntityManager(); CriteriaBuilder builder =
     * em.getCriteriaBuilder(); List<Suburb> result = new ArrayList<Suburb>();
     * 
     * MySqlQueryBuilder<Suburb> mySqlQueryBuilder = new
     * MySqlQueryBuilder<Suburb>(builder, Suburb.class);
     * 
     * mySqlQueryBuilder.buildQuery(selector, null); //executing query to get
     * result result =
     * em.createQuery(mySqlQueryBuilder.getQuery()).getResultList();
     * 
     * return result; }
     */
}
