/**
 * 
 */
package com.proptiger.data.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.proptiger.data.model.LandMark;
import com.proptiger.data.model.LandMarkTypes;
import com.proptiger.data.model.Locality;


import com.proptiger.data.model.LocalityRatings.LocalityAverageRatingByCategory;
import com.proptiger.data.model.LocalityRatings.LocalityRatingDetails;
import com.proptiger.data.model.LocalityReviewComments;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.SolrResult;
import com.proptiger.data.model.Suburb;
import com.proptiger.data.model.b2b.InventoryPriceTrend;
import com.proptiger.data.model.enums.DomainObject;
import com.proptiger.data.model.filter.Operator;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.LimitOffsetPageRequest;
import com.proptiger.data.pojo.Paging;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.SortOrder;
import com.proptiger.data.repo.LocalityDao;
import com.proptiger.data.repo.ProjectDao;
import com.proptiger.data.repo.PropertyDao;
import com.proptiger.data.service.b2b.TrendService;
import com.proptiger.data.service.pojo.PaginatedResponse;
import com.proptiger.data.thirdparty.Circle;
import com.proptiger.data.thirdparty.Point;
import com.proptiger.data.thirdparty.SEC;
import com.proptiger.data.util.Constants;
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.data.util.ResourceType;
import com.proptiger.data.util.ResourceTypeAction;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 * @author mandeep
 * @author Rajeev Pandey
 * 
 */
@Service
public class LocalityService {
    private static Logger              logger             = LoggerFactory.getLogger(LocalityService.class);

    private static int                 LOCALITY_PAGE_SIZE = 15;

    @Value("${b2b.price-inventory.max.month}")
    private String                     currentMonth;

    @Autowired
    private LocalityAmenityTypeService amenityTypeService;
    @Autowired
    private LocalityDao                localityDao;

    @Autowired
    private LocalityReviewService      localityReviewService;

    @Autowired
    private LandMarkService            localityAmenityService;

    @Autowired
    private LocalityRatingService      localityRatingService;

    @Autowired
    private ImageEnricher              imageEnricher;

    @Autowired
    private PropertyService            propertyService;

    @Autowired
    private ProjectDao                 projectDao;

    @Autowired
    private PropertyDao                propertyDao;

    @Autowired
    private PropertyReader             propertyReader;

    @Autowired
    private TrendService               trendService;

    /**
     * This method will return the List of localities selected based on the
     * selector provided.
     * 
     * @param selector
     * @return List<Locality>
     */
    public PaginatedResponse<List<Locality>> getLocalities(Selector selector) {
        PaginatedResponse<List<Locality>> paginatedRes = new PaginatedResponse<List<Locality>>();
        paginatedRes = localityDao.getLocalities(selector);
        List<Locality> localities = paginatedRes.getResults();
        
        if(localities != null){
            for (Locality locality : localities) {
                updateLocalityRatingAndReviewDetails(locality);
            }
        }
        return paginatedRes;
    }

    /**
     * This method will return the list of localities for locality listing. The
     * localities will be selected based on the selector provided. The list of
     * seperate data added is : 1: project status count for each locality 2:
     * resale Price info. 3: number of projects in a locality.
     * 
     * @param selector
     * @return SolrServiceResponse<List<Locality>> it will contain the list of
     *         localities based on paging in the selector and the total
     *         localities found based on selector in the object.
     */
    public PaginatedResponse<List<Locality>> getLocalityListing(Selector selector) {
        // adding the locality in the selector as we needed localityId
        boolean isSelectorFieldsEmpty = false;
        if (selector.getFields() == null) {
            isSelectorFieldsEmpty = true;
            selector.setFields(new HashSet<String>());
        }
        selector.getFields().add("localityId");

        Map<String, Map<String, Integer>> solrProjectStatusCountAndProjectCount = propertyDao
                .getProjectStatusCountAndProjectOnLocalityByCity(selector);

        List<Integer> localityIds = getLocalityIdsOnPropertySelector(solrProjectStatusCountAndProjectCount);

        // as the selector was empty, hence making fields empty again as now it
        // will affect the
        // fields returned.
        if (isSelectorFieldsEmpty)
            selector.setFields(null);

        PaginatedResponse<List<Locality>> localities = localityDao.findByLocalityIds(localityIds, selector);

        Map<String, Map<String, Map<String, FieldStatsInfo>>> priceStats = propertyService.getStatsFacetsAsMaps(
                selector,
                Arrays.asList("resalePrice"),
                Arrays.asList("localityId"));
        setProjectStatusCountAndProjectCountAndPriceOnLocality(
                localities.getResults(),
                solrProjectStatusCountAndProjectCount,
                priceStats);

        sortLocalities(localities.getResults());
        return localities;
    }

    /**
     * Sorts localities as per the logic that first X ones are either priority
     * based or project count based. Remaining ones are alphabetically sorted.
     * 
     * @param localities
     */
    private void sortLocalities(List<Locality> localities) {
        if (!localities.isEmpty()) {
            if (localities.get(0).getPriority() == Locality.MAX_PRIORITY) {
                Collections.sort(localities, new Comparator<Locality>() {

                    @Override
                    public int compare(Locality o1, Locality o2) {
                        return o2.getProjectCount() - o1.getProjectCount();
                    }

                });

                if (localities.size() > LOCALITY_PAGE_SIZE) {
                    List<Locality> remainingLocalities = localities.subList(LOCALITY_PAGE_SIZE, localities.size() - 1);
                    Collections.sort(remainingLocalities, new Comparator<Locality>() {

                        @Override
                        public int compare(Locality o1, Locality o2) {
                            return o1.getLabel().compareToIgnoreCase(o2.getLabel());
                        }
                    });

                    localities = localities.subList(0, LOCALITY_PAGE_SIZE - 1);
                    localities.addAll(remainingLocalities);
                }
            }
        }
    }

    /**
     * This method will take the list of localities , Price stats and project
     * status count and project count from solr. This method will iterate on
     * localities and set the data for each locality in the locality object.
     * 
     * @param localities
     * @param solrProjectStatusCountAndProjectCount
     * @param priceStats
     */
    public void setProjectStatusCountAndProjectCountAndPriceOnLocality(
            List<Locality> localities,
            Map<String, Map<String, Integer>> solrProjectStatusCountAndProjectCount,
            Map<String, Map<String, Map<String, FieldStatsInfo>>> priceStats) {

        Map<Integer, Map<String, Integer>> localityProjectStatusCount = getProjectStatusCountOnLocalityByCity(solrProjectStatusCountAndProjectCount
                .get("LOCALITY_ID_PROJECT_STATUS"));
        Map<String, Integer> projectCountOnLocality = solrProjectStatusCountAndProjectCount.get("LOCALITY_ID");
        Map<String, FieldStatsInfo> resalePriceStats = null;

        if (priceStats != null)
            resalePriceStats = priceStats.get("resalePrice").get("LOCALITY_ID");

        int size = localities.size();
        Locality locality;
        Integer projectCount;
        int localityId;
        for (int i = 0; i < size; i++) {
            locality = localities.get(i);
            localityId = locality.getLocalityId();
            String localityIdStr = localityId + "";
            // setting Project Count
            locality.setProjectStatusCount(localityProjectStatusCount.get(localityId));
            
            FieldStatsInfo fieldStatsInfo;
            // setting Resale Prices
            if (resalePriceStats != null) {
                fieldStatsInfo = resalePriceStats.get(localityIdStr);
                if (fieldStatsInfo != null) {
                    locality.setMinResalePrice((Double) fieldStatsInfo.getMin());
                    locality.setMaxResalePrice((Double) fieldStatsInfo.getMax());
                }
            }
        }

    }

    /**
     * This method will take the solr Output of locality project status count
     * and convert it into a map where key will be the locality Id and project
     * status and their counts.
     * 
     * @param solrProjectStatusCount
     *            Map<String Integer> Here String will be the
     *            localityId:project_status and Integer will the count of
     *            project status on that locality.
     * @return Map<Integer, Map<String, Integer>> Here Integer will be the
     *         localityId, String will be the project status and then Integer
     *         will be the project status count on that locality.
     */
    public Map<Integer, Map<String, Integer>> getProjectStatusCountOnLocalityByCity(
            Map<String, Integer> solrProjectStatusCount) {
        Map<Integer, Map<String, Integer>> localityProjectStatusCount = new HashMap<Integer, Map<String, Integer>>();
        String[] split;
        Integer localityId;
        Map<String, Integer> projectStatusCount = null;
        for (Map.Entry<String, Integer> entry : solrProjectStatusCount.entrySet()) {
            // localityId:projectStatus
            split = entry.getKey().split(":");
            localityId = Integer.parseInt(split[0]);
            projectStatusCount = localityProjectStatusCount.get(localityId);
            if (projectStatusCount == null) {
                projectStatusCount = new HashMap<String, Integer>();
            }
            projectStatusCount.put(split[1], entry.getValue());
            localityProjectStatusCount.put(localityId, projectStatusCount);

        }
        return localityProjectStatusCount;
    }

    public Double getMaxRadiusForLocalityOnProject(int localityId) {
        List<Locality> localities = localityDao.findByLocationOrderByPriority(localityId, "locality", null, null);// findByLocalityId(localityId);
        if (localities.size() < 1)
            return null;
        Locality locality = localities.get(0);

        List<SolrResult> projectSolrResults = projectDao.getProjectsByGEODistanceByLocality(
                localityId,
                locality.getLatitude(),
                locality.getLongitude(),
                1);

        if (projectSolrResults.size() > 0)
            return projectSolrResults.get(0).getProject().getLocality().getMaxRadius();
        return null;
    }

    /**
     * Get locality for locality id
     * 
     * @param localityId
     * @return Locality
     */
    @Cacheable(value = Constants.CacheName.LOCALITY, key = "#localityId")
    public Locality getLocality(int localityId) {
        return localityDao.getLocality(localityId);
    }

    /**
     * This method get locality information with some more application specific
     * data. Pass the image count if you need images of this locality. The more
     * information inserted in the locality data is as follows: 1: The average
     * rating and total rating users. 2: The distribution of ratings by users.
     * 3: The average price per BHK.
     * 
     * @param localityId
     * @param imageCount
     * @return Locality
     */
    public Locality getLocalityInfo(int localityId, Integer imageCount) {
        logger.debug("Get locality info for locality id {}", localityId);
        Locality locality = getLocality(localityId);
        if (locality == null) {
            return null;
        }
        List<LandMark> amenities = localityAmenityService.getLocalityAmenities(localityId, null);
        Map<String, Integer> localityAmenityCountMap = getLocalityAmenitiesCount(amenities);

        locality.setAmenityTypeCount(localityAmenityCountMap);
        imageEnricher.setLocalityImages(locality, imageCount);

        /*
         * Setting Rating and Review Details.
         */
        updateLocalityRatingAndReviewDetails(locality);

        /*
         * Setting the average price BHK wise
         */
        locality.setAvgBHKPriceUnitArea(getAvgPricePerUnitAreaBHKWise(
                "localityId",
                locality.getLocalityId(),
                locality.getDominantUnitType()));

        return locality;
    }

    /**
     * This methods returns the number of each amenities.
     * 
     * @param amenities
     * @return Map<String, Integer> Here String will represent the amenity type
     *         and the Integer will mean the count of amenities found.
     */
    private Map<String, Integer> getLocalityAmenitiesCount(List<LandMark> amenities) {
        Map<Integer, LandMarkTypes> amenityTypes = amenityTypeService.getLocalityAmenityTypes();
        Map<String, Integer> localityAmenityCountMap = new HashMap<>();
        for (LandMark amenity : amenities) {
            LandMarkTypes amenityType = amenityTypes.get(amenity.getPlaceTypeId());
            if (amenityType != null) {
                Integer count = localityAmenityCountMap.get(amenityType.getDisplayName());
                if (count == null) {
                    count = 1;
                }
                else {
                    count = count + 1;
                }
                localityAmenityCountMap.put(amenityType.getDisplayName(), count);
            }
        }
        return localityAmenityCountMap;
    }

    /**
     * Get popular localities of city or suburb based on enquiry count in last
     * {enquiryInWeeks} weeks in descending and in case of tie based on priority
     * order ASC
     * 
     * So in case of wrong city id and suburb id combination provided then wrong
     * data will be returned
     * 
     * @param cityId
     * @param suburbId
     * @param enquiryInWeeks
     * @param selector
     * @return List<Locality>
     */
    public List<Locality> getPopularLocalities(
            Integer cityId,
            Integer suburbId,
            Integer enquiryInWeeks,
            Selector selector) {

        // The colon is being escaped as to avoid native query colon param name
        // meaning.
        Date date = new DateTime().minusWeeks(enquiryInWeeks).toDate();
        String dateStr = new SimpleDateFormat("YYYY-MM-DD hh\\:mm\\:ss").format(date);

        List<Locality> result = localityDao.getPopularLocalities(cityId, suburbId, dateStr, selector);
        for (Locality locality : result) {
            updateLocalityRatingAndReviewDetails(locality);
        }
        return result;
    }

    /**
     * Get top localities either of city or suburb id. In case of city id or
     * suburb id get top localities based on their rating is >= α
     * 
     * α = 3 star, specfied in property file
     * 
     * @param cityId
     * @param suburbId
     * @param selector
     * @return List<Locality>
     */
    public List<Locality> getTopRatedLocalities(Integer cityId, Integer suburbId, Selector selector, Integer imageCount) {
        return getTopRatedLocalities_(cityId, suburbId, selector, imageCount, null);
    }
    
    /**
     * This method take a locality id that should be exclude while getting locality for city/suburb
     * @param cityId
     * @param suburbId
     * @param selector
     * @param imageCount
     * @param excludeLocalityId
     * @return
     */
    private List<Locality> getTopRatedLocalities_(
            Integer cityId,
            Integer suburbId,
            Selector selector,
            Integer imageCount,
            Integer excludeLocalityId) {
        List<Locality> result = null;
        List<Object[]> list = null;

        LimitOffsetPageRequest pageable = new LimitOffsetPageRequest();
        if (selector != null && selector.getPaging() != null) {
            pageable = new LimitOffsetPageRequest(selector.getPaging().getStart(), selector.getPaging().getRows());
        }
        list = localityDao.getTopLocalityByCityIdOrSuburbIdAndRatingGreaterThan(
                cityId,
                suburbId,
                propertyReader.getRequiredPropertyAsType(PropertyKeys.MINIMUM_RATING_FOR_TOP_LOCALITY, Double.class),
                excludeLocalityId,
                pageable);

        /*
         * setting average rating of locality
         */
        if (list != null) {
            List<Integer> localityIds = new ArrayList<Integer>();
            Map<Integer, Double> map = new HashMap<Integer, Double>();
            for (Object[] objects : list) {
                if (objects.length == 2) {
                    Integer localityId = (Integer) objects[0];
                    localityIds.add(localityId);
                    map.put(localityId, (Double) objects[1]);
                }
            }

            result = getLocalitiesOnIds(localityIds);
            Map<Integer, Locality> localities = new HashMap<Integer, Locality>();
            for (Locality locality : result) {
                locality.setAverageRating(map.get(locality.getLocalityId()));
                LocalityRatingDetails localityReviewDetails = localityRatingService
                        .getUsersCountByRatingOfLocality(locality.getLocalityId());
                locality.setNumberOfUsersByRating(localityReviewDetails.getTotalUsersByRating());
                localities.put(locality.getLocalityId(), locality);
            }

            // Sorting localities as lookup screwed the order
            result.clear();
            if(!localities.isEmpty()){
                for (int localityId: localityIds) {
                    if(localities.get(localityId) != null){
                        result.add(localities.get(localityId));
                    }
                }
            }
        }

        imageEnricher.setLocalitiesImages(result, imageCount);

            
        return result;
    }

    /**
     * Get top localities around provided locality id, this is bit different
     * than finding top localities for city or suburb. We need to find
     * localities where rating is >= α in X km radius by taking provided
     * locality's lat lon as center.
     * 
     * In case if minRatingThresholdForTopLocality is provided null value then
     * it will use minimumRatingForTopLocality as minimum rating threshold to
     * consider a locality as top locality α = minimumRatingForTopLocality star,
     * X = radiusOneForTopLocality
     * 
     * @param localityId
     * @param selector
     * @return List<Locality>
     */
    public List<Locality> getTopRatedLocalitiesAroundLocality(
            Integer localityId,
            Selector localitySelector,
            Integer imageCount,
            Double minRatingThresholdForTopLocality) {
        if (minRatingThresholdForTopLocality == null) {
            minRatingThresholdForTopLocality = propertyReader.getRequiredPropertyAsType(
                    PropertyKeys.MINIMUM_RATING_FOR_TOP_LOCALITY,
                    Double.class);
        }
        List<Locality> localities = localityDao.findByLocalityIds(Arrays.asList(localityId), localitySelector)
                .getResults();
        if (localities == null || localities.size() == 0) {
            throw new ResourceNotAvailableException(ResourceType.LOCALITY, ResourceTypeAction.GET);
        }
        /*
         * Top locality will be found around this locality
         */
        Locality mainLocality = localities.get(0);
        List<Locality> localitiesAroundMainLocality = null;
        Integer popularLocalityThresholdCount = propertyReader.getRequiredPropertyAsType(
                PropertyKeys.POPULAR_LOCALITY_THRESHOLD_COUNT,
                Integer.class);
        if (mainLocality.getLatitude() == null || mainLocality.getLongitude() == null) {
            /*
             * then as a fallback first try to find top rated in suburb of this
             * locality, and if that is not there then try to find for city of
             * that locality
             */
            localitiesAroundMainLocality = getTopRatedLocalityFallBackToSuburbCity(
                    localitySelector,
                    imageCount,
                    mainLocality,
                    popularLocalityThresholdCount);
        }
        else {
            /*
             * Create selector
             */
            Selector geoSelector = createSelectorForTopLocalityWithRadiusAroundLocality(
                    localitySelector,
                    mainLocality.getLocalityId(),
                    mainLocality.getLatitude(),
                    mainLocality.getLongitude(),
                    propertyReader.getRequiredPropertyAsType(PropertyKeys.RADIUS_ONE_FOR_TOP_LOCALITY, Double.class));

            localitiesAroundMainLocality = localityDao.getLocalities(geoSelector).getResults();
            /*
             * If locality not found or there count is less than
             * popularLocalityThresholdCount in first radius then try finding
             * localities in radius radiusTwoForTopLocality
             */
            if (localitiesAroundMainLocality == null || localitiesAroundMainLocality.size() < popularLocalityThresholdCount) {
                logger.debug(
                        "Top localities count {} is less than threshold {} in radius {}KM ",
                        localitiesAroundMainLocality == null ? 0 : localitiesAroundMainLocality.size(),
                        popularLocalityThresholdCount,
                        propertyReader
                                .getRequiredPropertyAsType(PropertyKeys.RADIUS_ONE_FOR_TOP_LOCALITY, Double.class));

                geoSelector = createSelectorForTopLocalityWithRadiusAroundLocality(
                        localitySelector,
                        mainLocality.getLocalityId(),
                        mainLocality.getLatitude(),
                        mainLocality.getLongitude(),
                        propertyReader
                                .getRequiredPropertyAsType(PropertyKeys.RADIUS_TWO_FOR_TOP_LOCALITY, Double.class));
                localitiesAroundMainLocality = localityDao.getLocalities(geoSelector).getResults();
                /*
                 * If locality not found or there count is less than
                 * popularLocalityThresholdCount in second radius then try
                 * finding localities in radius radiusThreeForTopLocality
                 */
                if (localitiesAroundMainLocality == null || localitiesAroundMainLocality.size() < popularLocalityThresholdCount) {
                    logger.debug(
                            "Top localities count {} is less than threshold {} in radius {}KM ",
                            localitiesAroundMainLocality == null ? 0 : localitiesAroundMainLocality.size(),
                            popularLocalityThresholdCount,
                            propertyReader.getRequiredPropertyAsType(
                                    PropertyKeys.RADIUS_TWO_FOR_TOP_LOCALITY,
                                    Double.class));

                    geoSelector = createSelectorForTopLocalityWithRadiusAroundLocality(
                            localitySelector,
                            mainLocality.getLocalityId(),
                            mainLocality.getLatitude(),
                            mainLocality.getLongitude(),
                            propertyReader.getRequiredPropertyAsType(
                                    PropertyKeys.RADIUS_THREE_FOR_TOP_LOCALITY,
                                    Double.class));

                    localitiesAroundMainLocality = localityDao.getLocalities(geoSelector).getResults();
                }
            }
        }

        if(localitiesAroundMainLocality == null || localitiesAroundMainLocality.size() < popularLocalityThresholdCount){
            /*
             * if locality count is not more than or equal to
             * popularLocalityThresholdCount then as a fallback first try to
             * find top rated in suburb of this locality, and if that is not
             * there then try to find for city of that locality
             */
            localitiesAroundMainLocality = getTopRatedLocalityFallBackToSuburbCity(
                    localitySelector,
                    imageCount,
                    mainLocality,
                    popularLocalityThresholdCount);
        }
        
        /*
         * All the localities found in specified radius by taking main locality
         * lat lon as center, now need to filter localities for rating > α
         */
        Iterator<Locality> localityItr = localitiesAroundMainLocality.iterator();
        while (localityItr.hasNext()) {
            Locality locality = localityItr.next();
            /*
             * Get more information of locality object
             */
            Locality localityWithMoreInfo = getLocalityInfo(locality.getLocalityId(), 0);
            /*
             * check if average rating is >= to minimum rating threshold. If
             * minRatingThresholdForTopLocality is 0 then we can safely include
             * locality that do not have review means review is null
             */
            if (minRatingThresholdForTopLocality == 0.0) {
                // do nothing
            }
            else {
                if (localityWithMoreInfo.getAverageRating() != null && localityWithMoreInfo.getAverageRating() >= minRatingThresholdForTopLocality) {
                    // if rating is greater than threshold then update average
                    // rating value
                    locality.setAverageRating(localityWithMoreInfo.getAverageRating());
                    locality.setRatingsCount(localityWithMoreInfo.getRatingsCount());
                    locality.setNumberOfUsersByRating(localityWithMoreInfo.getNumberOfUsersByRating());
                }
                else {
                    // remove the locality as rating is less that threshold
                    localityItr.remove();
                }
            }

        }
        return localitiesAroundMainLocality;
    }

    /**
     * @param localitySelector
     * @param imageCount
     * @param mainLocality
     * @param popularLocalityThresholdCount
     * @return
     */
    private List<Locality> getTopRatedLocalityFallBackToSuburbCity(
            Selector localitySelector,
            Integer imageCount,
            Locality mainLocality,
            Integer popularLocalityThresholdCount) {
        List<Locality> localitiesAroundMainLocality;
        // find in suburb
        localitiesAroundMainLocality = getTopRatedLocalities_(
                null,
                mainLocality.getSuburbId(),
                localitySelector,
                imageCount, mainLocality.getLocalityId());
        if (localitiesAroundMainLocality == null || localitiesAroundMainLocality.size() < popularLocalityThresholdCount) {
            // find in city
            localitiesAroundMainLocality = getTopRatedLocalities_(
                    mainLocality.getSuburb().getCityId(),
                    null,
                    localitySelector,
                    imageCount, mainLocality.getLocalityId());
        }
        return localitiesAroundMainLocality;
    }

    /**
     * Creating selector object to find all localities around provided locality
     * id under given radius from lat, lon
     * 
     * @param localitySelector
     * 
     * @param localityId
     * @param lat
     * @param lon
     * @param radius
     * @return
     */
    private Selector createSelectorForTopLocalityWithRadiusAroundLocality(
            Selector localitySelector,
            Integer localityId,
            Double lat,
            Double lon,
            Double radius) {
        Selector selector = new Selector();
        Map<String, List<Map<String, Map<String, Object>>>> filter = new HashMap<String, List<Map<String, Map<String, Object>>>>();
        List<Map<String, Map<String, Object>>> list = new ArrayList<>();
        Map<String, Map<String, Object>> searchType = new HashMap<>();
        Map<String, Object> geoFilter = new HashMap<>();

        Map<String, Object> geoValueMap = new HashMap<>();
        geoValueMap.put(Operator.distance.name(), radius);
        geoValueMap.put(Operator.lat.name(), lat);
        geoValueMap.put(Operator.lon.name(), lon);

        geoFilter.put(Operator.geo.name(), geoValueMap);
        searchType.put(Operator.geoDistance.name(), geoFilter);

        Map<String, Object> notEqualFilter = new HashMap<>();
        notEqualFilter.put("localityId", localityId);
        searchType.put(Operator.notEqual.name(), notEqualFilter);

        Map<String, Object> equalFilter = new HashMap<>();
        equalFilter.put("hasGeo", 1);
        searchType.put(Operator.equal.name(), equalFilter);

        list.add(searchType);
        filter.put(Operator.and.name(), list);
        selector.setFilters(filter);
        selector.setPaging(localitySelector != null ? localitySelector.getPaging() : new Paging());
        return selector;
    }

    /**
     * This method will return the List of locality ids based on the property
     * selector.
     * 
     * @param solrMap
     * @return List<Integer> list of locality ids.
     */
    private List<Integer> getLocalityIdsOnPropertySelector(Map<String, Map<String, Integer>> solrMap) {
        Map<String, Integer> projectCountOnLocality = solrMap.get("LOCALITY_ID");

        List<Integer> localities = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : projectCountOnLocality.entrySet()) {
            localities.add(Integer.parseInt(entry.getKey()));
        }

        return localities;
    }

    /**
     * This method will return the average price per bhk on the properties found
     * in a locality.
     * 
     * @param locationType
     *            city or suburb or locality.
     * @param locationId
     * @param unitType
     * @return Map<Integer, Double> Here Integer will number of bedrooms and
     *         Double the average price on that bedroom.
     */
    @Cacheable(value=Constants.CacheName.CACHE)
    public Map<Integer, Double> getAvgPricePerUnitAreaBHKWise(String locationType, int locationId, String unitType) {
        FIQLSelector selector = new FIQLSelector().addAndConditionToFilter("month==" + currentMonth)
                                                .addAndConditionToFilter("unitType==" + unitType)
                                                .addAndConditionToFilter(locationType + "==" + locationId)
                                                .addGroupByAtBeginning("bedrooms")
                                                .addField("wavgPricePerUnitAreaOnSupply");

        Map<Integer, Double> avgPrice = new HashMap<Integer, Double>();

        for(InventoryPriceTrend inventoryPriceTrend: trendService.getTrend(selector)) {
            Object avgPricePerUnitArea = inventoryPriceTrend.getExtraAttributes().get("wavgPricePerUnitAreaOnSupply");
            if (avgPricePerUnitArea != null) {
                avgPrice.put(inventoryPriceTrend.getBedrooms(), Double.valueOf(avgPricePerUnitArea.toString()));
            }
        }

        return avgPrice;
    }

    /**
     * This method will retrieve the reviews and rating details about a locality
     * and set the data on the locality Object. The data set on the locality
     * Object is as follows: 1: Number of Reviews on the locality. 2: Average
     * Rating 3: Total Rating Users. 4: Rating Distribution by total users.
     * 
     * @param locality
     */
    public void updateLocalityRatingAndReviewDetails(Locality locality) {

        LocalityAverageRatingByCategory avgRatingsOfLocalityCategory = localityRatingService
                .getAvgRatingsOfLocalityByCategory(locality.getLocalityId());

        locality.setAvgRatingsByCategory(avgRatingsOfLocalityCategory);

        LocalityRatingDetails localityRatingDetails = localityRatingService.getUsersCountByRatingOfLocality(locality
                .getLocalityId());

        locality.setAverageRating(localityRatingDetails.getAverageRatings());
        Long totalNumberOfReviews = (long)0;
        PaginatedResponse<List<LocalityReviewComments>> reviews = localityReviewService.getLocalityReview(
                null,
                new FIQLSelector().addAndConditionToFilter("localityId==" + locality.getLocalityId()));
        if(reviews != null){
            totalNumberOfReviews = reviews.getTotalCount();
        }
        /*
         * Setting total rating counts
         */
        locality.setRatingsCount(localityRatingDetails.getTotalRatings());
        /*
         * Setting total reviews counts
         */
        locality.setTotalReviews(totalNumberOfReviews);

        /*
         * Setting the Rating distribution
         */
        locality.setNumberOfUsersByRating(localityRatingDetails.getTotalUsersByRating());
        /*
         * setting the project status counts and project counts.
         */
        Map<String, Map<String, Integer>> projectAndProjectStatusCounts = propertyDao
                .getProjectStatusCountAndProjectOnLocality(locality.getLocalityId());
        setProjectStatusCountAndProjectCountAndPriceOnLocality(
                Arrays.asList(locality),
                projectAndProjectStatusCounts,
                null);
    }

    /**
     * This method will retrieve the reviews and rating details about a suburb
     * and set the data on the suburb Object. The data set on the suburb Object
     * is as follows: 1: Number of Reviews on the suburb. 2: Average Rating 3:
     * Total Rating Users. 4: Rating Distribution by total users.
     * 
     * @param suburb
     */
    public void updateSuburbRatingAndReviewDetails(Suburb suburb) {

        LocalityAverageRatingByCategory avgRatingsOfLocalityCategory = localityRatingService
                .getAvgRatingsOfSuburbByCategory(suburb.getId());
        suburb.setAvgRatingsByCategory(avgRatingsOfLocalityCategory);
    }

    public int getTopRatedLocalityInCityOrSuburb(String locationType, int locationId) {

        Paging paging = new Paging(0, 1);
        List<Locality> locality = null;
        switch (locationType) {
            case "city":
                locality = localityDao.findByLocationOrderByPriority(locationId, "city", paging, SortOrder.ASC);
                break;
            case "suburb":
                locality = localityDao.findByLocationOrderByPriority(locationId, "suburb", paging, SortOrder.ASC);
                break;
        }

        if ("locality".equals(locationType))
            return locationId;
        else if (locality.size() > 0)
            return locality.get(0).getLocalityId();
        else
            return -1;
    }

    public Point computeCenter(int localityId) {
        Point[] p = new Point[1000];
        int n = 0;
        Point[] b = new Point[3];

        for (Project project : propertyService.getPropertiesGroupedToProjects(
                new Gson().fromJson(
                        "{\"paging\":{\"rows\":1500},\"filters\":{\"and\":[{\"equal\":{\"localityId\":" + localityId
                                + "}}]}}",
                        Selector.class)).getResults()) {
            if (project.getLatitude() != null) {
                p[n++] = new Point(project.getLatitude(), project.getLongitude());
            }
        }

        if (n > 0) {
            Circle sec = SEC.findSec(n, p, 0, b);
            return sec.getCenter();
        }

        return null;
    }

    public PaginatedResponse<List<Locality>> getNearLocalitiesOnLocalityOnConcentricCircle(
            Locality locality,
            int minDistance,
            int maxDistance) {
        return localityDao.getNearLocalitiesByDistance(locality, minDistance, maxDistance);
    }

    public List<Integer> getNearLocalityIdOnLocalityOnConcentricCircle(
            Locality locality,
            int minDistance,
            int maxDistance) {
        PaginatedResponse<List<Locality>> localities = getNearLocalitiesOnLocalityOnConcentricCircle(
                locality,
                minDistance,
                maxDistance);

        return getLocalityIds(localities.getResults());
    }

    public List<Integer> getLocalityIds(List<Locality> localities) {
        List<Integer> localityIds = new ArrayList<>();

        for (Locality locality : localities) {
            localityIds.add(locality.getLocalityId());
        }
        return localityIds;
    }

    /**
     * Get top reviewed localities for city/suburb or a given locality.
     * 
     * @param locationTypeStr
     * @param locationId
     * @param minReviewCount
     * @param numberOfLocalities
     * @return
     */
    public PaginatedResponse<List<Locality>> getTopReviewedLocalities(
            String locationTypeStr,
            int locationId,
            int minReviewCount,
            int numberOfLocalities) {
        LimitOffsetPageRequest pageable = new LimitOffsetPageRequest(0, numberOfLocalities);
        int locationType;
        List<Integer> localities = null;
        switch (locationTypeStr.toLowerCase()) {
            case "city":
                locationType = 1;
                localities = localityReviewService.getTopReviewedLocalityOnCityOrSuburb(
                        locationType,
                        locationId,
                        minReviewCount,
                        pageable);
                break;
            case "suburb":
                locationType = 2;
                localities = localityReviewService.getTopReviewedLocalityOnCityOrSuburb(
                        locationType,
                        locationId,
                        minReviewCount,
                        pageable);
                break;
            case "locality":
                localities = localityReviewService.getTopReviewedNearLocalitiesForLocality(
                        locationId,
                        minReviewCount,
                        pageable);
                break;
            default:
                throw new IllegalArgumentException("location Type must be either city or locality or suburb");
        }

        if (localities == null || localities.size() < 1)
            return null;

        return localityDao.findByLocalityIds(localities, null);
    }

    /**
     * This method is used for getting the localities based on their
     * appreciation rate in descending order. For city and suburb, the
     * localities in that type are retrieved. For locality, the locailties
     * within radius of of (5,10,15) are retrieved.
     * 
     * @param locationTypeStr
     * @param locationId
     * @param numberOfLocalities
     * @return
     */
    public PaginatedResponse<List<Locality>> getHighestReturnLocalities(
            String locationTypeStr,
            int locationId,
            int numberOfLocalities,
            double minimumPriceRise) {

        int radius[] = { 5, 10, 15 };
        PaginatedResponse<List<Locality>> localities = null;

        String json = null;
        Selector selector = null;
        if (!locationTypeStr.equalsIgnoreCase("locality")) {
            json = "{\"paging\":{\"rows\":" + numberOfLocalities
                    + "},\"filters\":{\"and\":[{\"equal\":{\""
                    + locationTypeStr
                    + "Id\":"
                    + locationId
                    + "}},{\"range\":{\"localityAvgPriceRiseMonths\":{\"from\":1},\"localityAvgPriceRisePercentage\":{\"from\":"
                    + minimumPriceRise
                    + "}}}]},\"sort\":[{\"field\":\"localityPriceAppreciationRate\",\"sortOrder\":\"DESC\"}]}";

            selector = new Gson().fromJson(json, Selector.class);
            localities = localityDao.getLocalities(selector);
        }
        else {
            Locality locality = getLocality(locationId);
            if (locality == null || locality.getLatitude() == null || locality.getLongitude() == null) {
                return new PaginatedResponse<List<Locality>>();
            }

            json = "{\"paging\":{\"rows\":" + numberOfLocalities
                    + "},\"filters\":{\"and\":[{\"geoDistance\":{\"geo\":{\"distance\":%d,\"lat\":"
                    + locality.getLatitude()
                    + ",\"lon\":"
                    + locality.getLongitude()
                    + "}}},"
                    + "{\"equal\":{\"hasGeo\":1}},"
                    + "{\"range\":{\"localityAvgPriceRiseMonths\":{\"from\":1},\"localityAvgPriceRisePercentage\":{\"from\":"
                    + minimumPriceRise
                    + "}}}]},\"sort\":[{\"field\":\"localityPriceAppreciationRate\",\"sortOrder\":\"DESC\"},{\"field\":\"geoDistance\",\"sortOrder\":\"ASC\"}]}";

            String jsonWithRadius;
            for (int i = 0; i < radius.length && (localities == null || localities.getTotalCount() < numberOfLocalities); i++) {
                jsonWithRadius = String.format(json, radius[i]);
                selector = new Gson().fromJson(jsonWithRadius, Selector.class);
                localities = localityDao.getLocalities(selector);
            }
        }

        if (localities == null) {
            return new PaginatedResponse<List<Locality>>();
        }

        return localities;
    }

    /**
     * This method will return the localities data for all the locality Ids.
     * 
     * @param localityIds
     * @return
     */
    private List<Locality> getLocalitiesOnIds(List<Integer> localityIds) {
        if (localityIds == null || localityIds.isEmpty())
            return new ArrayList<Locality>();

        String json = "{\"filters\":{\"and\":[{\"equal\":{\"localityId\":[" + StringUtils.join(localityIds, ',')
                + "]}}]}}";

        return getLocalities(new Gson().fromJson(json, Selector.class)).getResults();
    }

    public PaginatedResponse<List<Locality>> getLocalities(FIQLSelector selector) {
        return localityDao.getLocalities(selector);
    }

    public List<Locality> getLocalitiesOnCityOrSuburb(DomainObject domainObject, int domainId, Paging paging) {
        String jsonSelector = "{\"filters\":{\"and\":[{\"equal\":{\"" + domainObject.name()
                + "Id\":"
                + domainId
                + "}}]}, \"paging\":{\"start\":"
                + paging.getStart()
                + ",\"rows\":"
                + paging.getRows()
                + "}}";
        
        Selector selector = new Gson().fromJson(jsonSelector, Selector.class);
        return Lists.newArrayList(localityDao.getLocalities(selector).getResults());
    }
}
