/**
 * 
 */
package com.proptiger.data.repo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.Locality;
import com.proptiger.data.model.SolrResult;
import com.proptiger.data.model.enums.DocumentType;
import com.proptiger.data.model.filter.Operator;
import com.proptiger.data.model.filter.SolrQueryBuilder;
import com.proptiger.data.pojo.Paging;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.SortBy;
import com.proptiger.data.pojo.SortOrder;
import com.proptiger.data.service.pojo.PaginatedResponse;

/**
 * @author mandeep
 * 
 */
@Repository
public class LocalityDaoImpl {

    @Autowired
    private SolrDao              solrDao;
    @Autowired
    private EntityManagerFactory emf;

    public PaginatedResponse<List<Locality>> getLocalities(Selector selector) {
        SolrQuery solrQuery = createSolrQuery(selector);

        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
        List<SolrResult> response = queryResponse.getBeans(SolrResult.class);

        return getPaginatedResponse(response, queryResponse);
    }

    public List<Locality> findByLocationOrderByPriority(
            Object locationId,
            String locationType,
            Paging paging,
            SortOrder sortOrder) {
        if (sortOrder == null)
            sortOrder = SortOrder.DESC;
        if (paging == null)
            paging = new Paging();

        Selector selector = new Selector();

        Map<String, List<Map<String, Map<String, Object>>>> filter = new HashMap<String, List<Map<String, Map<String, Object>>>>();
        List<Map<String, Map<String, Object>>> list = new ArrayList<>();
        Map<String, Map<String, Object>> searchType = new HashMap<>();
        Map<String, Object> filterCriteria = new HashMap<>();
        LinkedHashSet<SortBy> sorting = new LinkedHashSet<>();
        SortBy sortBy = new SortBy();

        sortBy.setField("localityPriority");
        sortBy.setSortOrder(sortOrder);
        sorting.add(sortBy);

        sortBy = new SortBy();
        sortBy.setField("localityLabel");
        sortBy.setSortOrder(SortOrder.ASC);
        sorting.add(sortBy);

        String param = "";
        switch (locationType) {
            case "city":
                param = "cityId";
                break;
            case "suburb":
                param = "suburbId";
                break;
            default:
                param = "localityId";
        }

        filterCriteria.put(param, locationId);
        searchType.put(Operator.equal.name(), filterCriteria);
        list.add(searchType);
        filter.put(Operator.and.name(), list);

        selector.setFilters(filter);
        selector.setPaging(paging);
        selector.setSort(sorting);

        return getLocalities(selector).getResults();
    }

    public PaginatedResponse<List<Locality>> findByLocalityIds(List<Integer> localityIds, Selector propertySelector) {
        if (localityIds == null || localityIds.isEmpty()) {
            PaginatedResponse<List<Locality>> a = new PaginatedResponse<>();
            a.setTotalCount(0);
            a.setResults(new ArrayList<Locality>());
            return a;
        }

        Selector selector = new Selector();

        Map<String, List<Map<String, Map<String, Object>>>> filter = new HashMap<String, List<Map<String, Map<String, Object>>>>();
        List<Map<String, Map<String, Object>>> list = new ArrayList<>();
        Map<String, Map<String, Object>> searchType = new HashMap<>();
        Map<String, Object> filterCriteria = new HashMap<>();

        filterCriteria.put("localityId", localityIds);
        searchType.put(Operator.equal.name(), filterCriteria);
        list.add(searchType);
        filter.put(Operator.and.name(), list);

        selector.setFilters(filter);
        if (propertySelector != null) {
            selector.setFields(propertySelector.getFields());
            selector.setPaging(propertySelector.getPaging());
        }

        if (selector.getPaging() == null)
            selector.setPaging(new Paging(0, localityIds.size()));

        return getLocalities(selector);
    }

    private SolrQuery createSolrQuery(Selector selector) {
        SolrQuery solrQuery = SolrDao.createSolrQuery(DocumentType.LOCALITY);
        if (selector.getSort() == null) {
            selector.setSort(new LinkedHashSet<SortBy>());
        }

        selector.getSort().addAll(getDefaultSort());

        SolrQueryBuilder<SolrResult> solrQueryBuilder = new SolrQueryBuilder<>(solrQuery, SolrResult.class);
        solrQueryBuilder.buildQuery(selector, null);

        return solrQuery;
    }

    private Set<SortBy> getDefaultSort() {
        Set<SortBy> sortBySet = new LinkedHashSet<SortBy>();
        SortBy sortBy = new SortBy();
        sortBy.setField("localityPriority");
        sortBy.setSortOrder(SortOrder.ASC);
        sortBySet.add(sortBy);

        sortBy = new SortBy();
        sortBy.setField("localityLabel");
        sortBy.setSortOrder(SortOrder.ASC);
        sortBySet.add(sortBy);

        return sortBySet;
    }

    /**
     * This method is getting all the popular localities of city, criteria of
     * popularity is first with priority in asc and in case of tie total enquiry
     * in desc
     * 
     * @param cityId
     * @param suburbId
     * @param enquiryCreationDate
     * @return
     */
    public List<Locality> getPopularLocalities(
            Integer cityId,
            Integer suburbId,
            String dateString,
            Selector selector) {
        Paging paging = new Paging();
        if (selector != null && selector.getPaging() != null) {
            paging = selector.getPaging();
        }
        EntityManager em = emf.createEntityManager();
        Query query = em
                .createNativeQuery(
                        "select *, count(enquiry1_.ID) as ENQUIRY_COUNT from proptiger.LOCALITY locality0_ " + " left outer join  proptiger.ENQUIRY enquiry1_ ON (locality0_.LOCALITY_ID = enquiry1_.LOCALITY_ID AND "
                                + " enquiry1_.CREATED_DATE >"
                                + " \""
                                + dateString
                                + "\")"
                                + " where (locality0_.CITY_ID = "
                                + " "
                                + cityId
                                + " or locality0_.SUBURB_ID = "
                                + " "
                                + suburbId
                                + ")"
                                + " group by locality0_.LOCALITY_ID order by ENQUIRY_COUNT DESC, locality0_.PRIORITY ASC"
                                + " LIMIT "
                                + paging.getRows()
                                + " OFFSET "
                                + paging.getStart(),
                        Locality.class);
        List<Locality> result = query.getResultList();
        return result;
    }

    public Locality getLocality(int localityId) {
        List<Locality> localities = findByLocationOrderByPriority(localityId, "locality", null, null);
        if (localities == null || localities.size() < 1)
            return null;

        return localities.get(0);
    }

    public PaginatedResponse<List<Locality>> getPaginatedResponse(List<SolrResult> response, QueryResponse queryResponse) {
        List<Locality> data = new ArrayList<>();
        for (int i = 0; i < response.size(); i++) {
            data.add(response.get(i).getProject().getLocality());
        }

        PaginatedResponse<List<Locality>> solrRes = new PaginatedResponse<List<Locality>>();
        solrRes.setTotalCount(queryResponse.getResults().getNumFound());
        solrRes.setResults(data);

        return solrRes;
    }

    public PaginatedResponse<List<Locality>> getNearLocalitiesByDistance(
            Locality locality,
            int minDistance,
            int maxDistance) {
        if (locality == null || locality.getLatitude() == null || locality.getLongitude() == null)
            return null;

        SolrQuery solrQuery = SolrDao.createSolrQuery(DocumentType.LOCALITY);

        solrQuery.add("pt", locality.getLatitude() + "," + locality.getLongitude());
        solrQuery.add("sfield", "GEO");

        /*
         * incl means lower limit inclusive or not. by default true. incu means
         * upper limit inclusive or not. by default true.
         */
        solrQuery.addFilterQuery("{!frange l=" + minDistance + " u=" + maxDistance + " incl=false}geodist()");
        solrQuery.addFilterQuery("HAS_GEO:1");
        solrQuery.addFilterQuery("CITY_ID:" + locality.getSuburb().getCityId());
        solrQuery.addSort("geodist()", ORDER.asc);
        solrQuery.addSort("LOCALITY_PRIORITY", ORDER.asc);
        solrQuery.setRows(Integer.MAX_VALUE);

        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);

        return getPaginatedResponse(queryResponse.getBeans(SolrResult.class), queryResponse);
    }
}
