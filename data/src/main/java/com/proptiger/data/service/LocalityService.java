/**
 * 
 */
package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.util.CharArrayMap.EntrySet;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.LocalityAmenity;
import com.proptiger.data.model.LocalityAmenityTypes;
import com.proptiger.data.model.SolrResult;
import com.proptiger.data.model.enums.DomainObject;
import com.proptiger.data.model.filter.Operator;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.repo.LocalityDao;
import com.proptiger.data.repo.LocalityDaoImpl;
import com.proptiger.data.repo.ProjectDao;
import com.proptiger.data.repo.PropertyDao;
import com.proptiger.data.service.pojo.SolrServiceResponse;
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
	private static Logger logger = LoggerFactory
			.getLogger(LocalityService.class);

	@Autowired
	private LocalityAmenityTypeService amenityTypeService;
	@Autowired
	private LocalityDao localityDao;

	@Autowired
	private LocalityDaoImpl localityDaoImpl;

	@Autowired
	private LocalityReviewService localityReviewService;

	@Autowired
	private LocalityAmenityService localityAmenityService;

	@Autowired
	private ImageEnricher imageEnricher;

	@Autowired
	private ProjectDao projectDao;

	@Autowired
	private PropertyDao propertyDao;

	@Value("${minimum.rating.for.top.locality}")
	private Double minimumRatingForTopLocality;

	@Value("${radius.one.for.top.locality}")
	private Double radiusOneForTopLocality;

	@Value("${radius.two.for.top.locality}")
	private Double radiusTwoForTopLocality;

	@Value("${radius.three.for.top.locality}")
	private Double radiusThreeForTopLocality;

	@Value("${popular.locality.threshold.count}")
	private Integer popularLocalityThresholdCount;

	public List<Locality> getLocalities(Selector selector) {
		return Lists.newArrayList(localityDao.getLocalities(selector));
	}

	public SolrServiceResponse<List<Locality>> getLocalityListing(
			Selector selector) {
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

		SolrServiceResponse<List<Locality>> localities = localityDao
				.findByLocalityIds(localityIds, selector);

		Map<String, Map<String, Map<String, FieldStatsInfo>>> priceStats = propertyDao
				.getStatsFacetsAsMaps(selector,
						Arrays.asList("resalePrice"),
						Arrays.asList("localityId"));
		setProjectStatusCountAndProjectCountAndPriceOnLocality(
				localities.getResult(), solrProjectStatusCountAndProjectCount,
				priceStats);
		return localities;
	}

	public void setProjectStatusCountAndProjectCountAndPriceOnLocality(
			List<Locality> localities,
			Map<String, Map<String, Integer>> solrProjectStatusCountAndProjectCount,
			Map<String, Map<String, Map<String, FieldStatsInfo>>> priceStats) {

		Map<Integer, Map<String, Integer>> localityProjectStatusCount = getProjectStatusCountOnLocalityByCity(solrProjectStatusCountAndProjectCount
				.get("LOCALITY_ID_PROJECT_STATUS"));
		Map<String, Integer> projectCountOnLocality = solrProjectStatusCountAndProjectCount
				.get("LOCALITY_ID");
		Map<String, FieldStatsInfo> resalePriceStats = priceStats.get(
				"resalePrice").get("LOCALITY_ID");
		
		int size = localities.size();
		Locality locality;
		Integer projectCount;
		int localityId;
		for (int i = 0; i < size; i++) {
			locality = localities.get(i);
			localityId = locality.getLocalityId();
			String localityIdStr = localityId + "";
			// setting Project Count
			locality.setProjectStatusCount(localityProjectStatusCount
					.get(localityId));
			projectCount = projectCountOnLocality.get(localityIdStr);
			if (projectCount != null)
				locality.setProjectCount(projectCount.intValue());

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

	public Map<Integer, Map<String, Integer>> getProjectStatusCountOnLocalityByCity(
			Map<String, Integer> solrProjectStatusCount) {
		Map<Integer, Map<String, Integer>> localityProjectStatusCount = new HashMap<Integer, Map<String, Integer>>();
		String[] split;
		Integer localityId;
		Map<String, Integer> projectStatusCount = null;
		for (Map.Entry<String, Integer> entry : solrProjectStatusCount
				.entrySet()) {
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
		List<Locality> localities = localityDao.findByLocationOrderByPriority(
				localityId, "locality", null, null);// findByLocalityId(localityId);
		if (localities.size() < 1)
			return null;
		Locality locality = localities.get(0);

		List<SolrResult> projectSolrResults = projectDao
				.getProjectsByGEODistanceByLocality(localityId,
						locality.getLatitude(), locality.getLongitude(), 1);

		if (projectSolrResults.size() > 0)
			return projectSolrResults.get(0).getProject().getLocality()
					.getMaxRadius();
		return null;
	}

	/**
	 * Get locality for locality id
	 * 
	 * @param localityId
	 * @return
	 */
	public Locality getLocality(int localityId) {
		return localityDao.findOne(localityId);
	}

	/**
	 * This method get locality information with some more application specific
	 * data. Pass the image count if you need images of this locality.
	 * 
	 * @param localityId
	 * @param imageCount
	 * @return
	 */
	public Locality getLocalityInfo(int localityId, Integer imageCount) {
		logger.debug("Get locality info for locality id {}", localityId);
		Locality locality = getLocality(localityId);
		if(locality == null)
			return null;
		
		Map<String, Object> localityReviewDetails = localityReviewService
				.findReviewByLocalityId(localityId, null);

		List<LocalityAmenity> amenities = localityAmenityService
				.getLocalityAmenities(localityId, null);
		Map<String, Integer> localityAmenityCountMap = getLocalityAmenitiesCount(amenities);

		locality.setAmenityTypeCount(localityAmenityCountMap);
		locality.setAverageRating(localityReviewDetails
				.get(LocalityReviewService.AVERAGE_RATINGS) == null ? 0
				: (Double) localityReviewDetails
						.get(LocalityReviewService.AVERAGE_RATINGS));
		/*
		 * Hit image service only if images are required
		 */
		if (imageCount != null && imageCount > 0) {
			imageEnricher.setLocalityImages(null, locality, imageCount);
		}
		/*
		 * Setting total rating counts
		 */
		locality.setRatingsCount(localityReviewDetails
				.get(LocalityReviewService.TOTAL_RATINGS) == null ? 0
				: (Long) localityReviewDetails
						.get(LocalityReviewService.TOTAL_RATINGS));
		/*
		 * Setting total reviews counts
		 */
		locality.setTotalReviews(localityReviewDetails
				.get(LocalityReviewService.TOTAL_REVIEWS) == null ? 0
				: (Long) localityReviewDetails
						.get(LocalityReviewService.TOTAL_REVIEWS));
		
		/*
		 * Setting the average price BHK wise
		 */
		locality.setAvgBHKPrice( getAvgPricePerUnitAreaBHKWise("localityId", locality.getLocalityId(), locality.getDominantUnitType()) );
		
		return locality;
	}

	/**
	 * This methods returns the number of each amenities.
	 * 
	 * @param amenities
	 * @return
	 */
	private Map<String, Integer> getLocalityAmenitiesCount(
			List<LocalityAmenity> amenities) {
		Map<Integer, LocalityAmenityTypes> amenityTypes = amenityTypeService
				.getLocalityAmenityTypes();
		Map<String, Integer> localityAmenityCountMap = new HashMap<>();
		for (LocalityAmenity amenity : amenities) {
			LocalityAmenityTypes amenityType = amenityTypes.get(amenity
					.getPlaceTypeId());
			if (amenityType != null) {
				Integer count = localityAmenityCountMap.get(amenityType
						.getDisplayName());
				if (count == null) {
					count = 1;
				} else {
					count = count + 1;
				}
				localityAmenityCountMap
						.put(amenityType.getDisplayName(), count);
			}
		}
		return localityAmenityCountMap;
	}

	/**
	 * Get popular localities of city or suburb based on priority and in case of
	 * tie based on enquiry count in last {enquiryInWeeks} weeks in descending
	 * order
	 * 
	 * So in case of wrong city id and suburb id combination provided then wrong
	 * data will be returned
	 * 
	 * @param cityId
	 * @param suburbId
	 * @param enquiryInWeeks
	 * @return
	 */
	public List<Locality> getPopularLocalities(Integer cityId,
			Integer suburbId, Integer enquiryInWeeks) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.WEEK_OF_YEAR, -enquiryInWeeks);

		Date enquiryCreationDate = cal.getTime();
		Long timeStmap = enquiryCreationDate.getTime() / 1000;
		// TODO need to change this to get from localityDao
		// List<Object[]> localities = localityDao
		// .getPopularLocalitiesOfCityOrderByPriorityASCAndTotalEnquiryDESC(
		// cityId, suburbId, timeStmap);

		List<Locality> result = localityDao.getPopularLocalities(cityId,
				suburbId, timeStmap);
		return result;
	}

	/**
	 * Get top localities either of city or suburb id. In case of city id get
	 * top localities based on their rating is >= α
	 * 
	 * α = 3 star
	 * 
	 * @param cityId
	 * @param suburbId
	 * @param selector
	 * @return
	 */
	public List<Locality> getTopLocalities(Integer cityId, Integer suburbId,
			Selector selector) {
		List<Locality> result = new ArrayList<>();
		List<Object[]> list = null;

		list = localityDao
				.getTopLocalityByCityIdOrSuburbIdAndRatingGreaterThan(cityId,
						suburbId, minimumRatingForTopLocality);

		/*
		 * setting average rating of locality
		 */
		if (list != null) {
			for (Object[] objects : list) {
				if (objects.length == 2) {
					Locality locality = (Locality) objects[0];
					locality.setAverageRating((double) objects[1]);
					result.add(locality);
				}
			}
		}

		return result;
	}

	/**
	 * Get top localities around provided locality id, this is bit different
	 * than finding top localities for city or suburb. We need to find
	 * localities where rating is >= α in X km radius by taking provided
	 * locality's lat lon as center.
	 * 
	 * α = minimumRatingForTopLocality star, X = radiusOneForTopLocality
	 * 
	 * @param localityId
	 * @param selector
	 * @return
	 */
	public List<Locality> getTopLocalitiesAroundLocality(Integer localityId,
			Selector localitySelector) {
		List<Locality> localities = localityDao.findByLocalityIds(
				Arrays.asList(localityId), localitySelector).getResult();
		if (localities == null || localities.size() == 0) {
			throw new ResourceNotAvailableException(ResourceType.LOCALITY,
					ResourceTypeAction.GET);
		}
		/*
		 * Top locality will be found around this locality
		 */
		Locality mainLocality = localities.get(0);

		/*
		 * Create selector
		 */
		Selector geoSelector = createSelectorForTopLocalityWithRadiusAroundLocality(
				mainLocality.getLocalityId(), mainLocality.getLatitude(),
				mainLocality.getLongitude(), radiusOneForTopLocality);

		List<Locality> localitiesAroundMainLocality = localityDao
				.getLocalities(geoSelector);
		/*
		 * If locality not found or there count is less than
		 * popularLocalityThresholdCount in first radius then try finding
		 * localities in radius radiusTwoForTopLocality
		 */
		if (localitiesAroundMainLocality == null
				|| localitiesAroundMainLocality.size() < popularLocalityThresholdCount) {
			logger.debug(
					"Top localities count {} is less than threshold {} in radius {}KM ",
					localitiesAroundMainLocality == null ? 0
							: localitiesAroundMainLocality.size(),
					popularLocalityThresholdCount, radiusOneForTopLocality);

			geoSelector = createSelectorForTopLocalityWithRadiusAroundLocality(
					mainLocality.getLocalityId(), mainLocality.getLatitude(),
					mainLocality.getLongitude(), radiusTwoForTopLocality);
			localitiesAroundMainLocality = localityDao
					.getLocalities(geoSelector);
			/*
			 * If locality not found or there count is less than
			 * popularLocalityThresholdCount in first radius then try finding
			 * localities in radius radiusThreeForTopLocality
			 */
			if (localitiesAroundMainLocality == null
					|| localitiesAroundMainLocality.size() < popularLocalityThresholdCount) {
				logger.debug(
						"Top localities count {} is less than threshold {} in radius {}KM ",
						localitiesAroundMainLocality == null ? 0
								: localitiesAroundMainLocality.size(),
						popularLocalityThresholdCount, radiusTwoForTopLocality);

				geoSelector = createSelectorForTopLocalityWithRadiusAroundLocality(
						mainLocality.getLocalityId(),
						mainLocality.getLatitude(),
						mainLocality.getLongitude(), radiusThreeForTopLocality);

				localitiesAroundMainLocality = localityDao
						.getLocalities(geoSelector);
			}
		}
		/*
		 * All the localities found in specified radius by taking main locality
		 * lat lon as center, now need to filter localities for rating > α
		 */
		Iterator<Locality> localityItr = localitiesAroundMainLocality
				.iterator();
		while (localityItr.hasNext()) {
			Locality locality = localityItr.next();
			/*
			 * Get more information of locality object
			 */
			Locality localityWithMoreInfo = getLocalityInfo(
					locality.getLocalityId(), 0);
			/*
			 * check if average rating is >= to minimum rating threshold
			 */
			if (localityWithMoreInfo.getAverageRating() >= minimumRatingForTopLocality) {
				// if rating is greater than threshold then update average
				// rating value
				locality.setAverageRating(localityWithMoreInfo
						.getAverageRating());
			} else {
				// remove the locality as rating is less that threshold
				localityItr.remove();
			}
		}
		return localitiesAroundMainLocality;
	}

	/**
	 * Creating selector object to find all localities around provided locality
	 * id under given radius from lat, lon
	 * 
	 * @param localityId
	 * @param lat
	 * @param lon
	 * @param radius
	 * @return
	 */
	private Selector createSelectorForTopLocalityWithRadiusAroundLocality(
			Integer localityId, Double lat, Double lon, Double radius) {
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

		list.add(searchType);
		filter.put(Operator.and.name(), list);
		selector.setFilters(filter);
		return selector;
	}

	private List<Integer> getLocalityIdsOnPropertySelector(
			Map<String, Map<String, Integer>> solrMap) {
		Map<String, Integer> projectCountOnLocality = solrMap
				.get("LOCALITY_ID");

		List<Integer> localities = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : projectCountOnLocality
				.entrySet()) {
			localities.add(Integer.parseInt(entry.getKey()));
		}

		return localities;
	}
	
	public Map<Integer, Double> getAvgPricePerUnitAreaBHKWise(String locationType, int locationId, String unitType){
		Map<String, Map<String, Map<String, FieldStatsInfo>>> stats = propertyDao.getAvgPricePerUnitAreaBHKWise(locationType, locationId, unitType);
		
		if(stats == null || stats.get("pricePerUnitArea").get("BEDROOMS") == null)
			return null;
		
		Map<String, FieldStatsInfo> priceStats = stats.get("pricePerUnitArea").get("BEDROOMS");
		Map<Integer, Double> avgPrice = new HashMap<Integer, Double>();
		for(Map.Entry<String, FieldStatsInfo> entry : priceStats.entrySet()){
			avgPrice.put(Integer.parseInt( entry.getKey() ) , (Double)entry.getValue().getMean());
		}
		
		return avgPrice;
	}
}
