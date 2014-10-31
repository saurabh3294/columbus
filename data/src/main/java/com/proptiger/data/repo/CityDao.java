package com.proptiger.data.repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proptiger.core.enums.DocumentType;
import com.proptiger.core.model.cms.City;
import com.proptiger.core.pojo.Selector;
import com.proptiger.core.repo.SolrDao;
import com.proptiger.data.enums.filter.Operator;
import com.proptiger.data.model.SolrResult;
import com.proptiger.data.model.filter.SolrQueryBuilder;

/**
 * @author Rajeev Pandey
 * 
 */
@Repository
public class CityDao {
    private static Logger logger = LoggerFactory.getLogger(CityDao.class);

    @Autowired
    private SolrDao       solrDao;

    public List<City> getCities(Selector selector) {
        SolrQuery solrQuery = SolrDao.createSolrQuery(DocumentType.CITY);

        SolrQueryBuilder<City> solrQueryBuilder = new SolrQueryBuilder<>(solrQuery, City.class);
        solrQueryBuilder.buildQuery(selector, null);
        
        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
        List<SolrResult> response = queryResponse.getBeans(SolrResult.class);

        logger.debug("City solr query {}", solrQuery.toString());
        List<City> data = new ArrayList<>();
        for (int i = 0; i < response.size(); i++) {
            data.add(response.get(i).getProject().getLocality().getSuburb().getCity());
        }

        return data;
    }

    public City getCity(int cityId) {

        Selector selector = new Selector();

        Map<String, List<Map<String, Map<String, Object>>>> filter = new HashMap<String, List<Map<String, Map<String, Object>>>>();
        List<Map<String, Map<String, Object>>> list = new ArrayList<>();
        Map<String, Map<String, Object>> searchType = new HashMap<>();
        Map<String, Object> filterCriteria = new HashMap<>();

        filterCriteria.put("id", cityId);
        searchType.put(Operator.equal.name(), filterCriteria);
        list.add(searchType);
        filter.put(Operator.and.name(), list);

        selector.setFilters(filter);

        List<City> cities = getCities(selector);
        if (cities == null || cities.size() < 1)
            return null;

        return cities.get(0);
    }

    /*
     * @Autowired private EntityManagerFactory emf;
     * 
     * public List<City> getCities(Selector selector) {
     * 
     * EntityManager em = emf.createEntityManager(); CriteriaBuilder builder =
     * em.getCriteriaBuilder(); List<City> result = new ArrayList<City>();
     * 
     * MySqlQueryBuilder<City> mySqlQueryBuilder = new
     * MySqlQueryBuilder<City>(builder, City.class);
     * 
     * mySqlQueryBuilder.buildQuery(selector, null); //executing query to get
     * result result =
     * em.createQuery(mySqlQueryBuilder.getQuery()).getResultList();
     * 
     * return result; }
     */

}
