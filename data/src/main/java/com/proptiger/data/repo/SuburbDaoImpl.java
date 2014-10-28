/**
 * 
 */
package com.proptiger.data.repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proptiger.core.model.cms.Suburb;
import com.proptiger.core.pojo.Selector;
import com.proptiger.core.repo.SolrDao;
import com.proptiger.data.enums.filter.Operator;
import com.proptiger.data.model.SolrResult;
import com.proptiger.data.model.filter.SolrQueryBuilder;

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
        solrQueryBuilder.buildQuery(selector, null);

        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
        List<SolrResult> response = queryResponse.getBeans(SolrResult.class);

        List<Suburb> data = new ArrayList<>();
        for (int i = 0; i < response.size(); i++) {
            data.add(response.get(i).getProject().getLocality().getSuburb());
        }

        return data;
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
