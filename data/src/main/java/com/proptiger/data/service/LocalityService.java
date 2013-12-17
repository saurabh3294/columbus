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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.lucene.analysis.util.CharArrayMap.EntrySet;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.proptiger.data.model.DomainObject;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.LocalityAmenity;
import com.proptiger.data.model.LocalityAmenityTypes;
import com.proptiger.data.model.SolrResult;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.repo.LocalityDao;
import com.proptiger.data.repo.LocalityDaoImpl;
import com.proptiger.data.repo.ProjectDao;
import com.proptiger.data.repo.PropertyDao;


/**
 * @author mandeep
 *
 */
@Service
public class LocalityService {
	private static Logger logger = LoggerFactory.getLogger(LocalityService.class);

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
    private ImageService imageService;
    
    @Autowired
    private ProjectDao projectDao;
    
    @Autowired
    private PropertyDao propertyDao;
    
    public List<Locality> getLocalities(Selector selector) {
        return Lists.newArrayList(localityDao.getLocalities(selector));
    }
    
    public List<Locality> getLocalityListing(Selector selector){
    	if(selector.getFields() == null)
		{
			selector.setFields(new HashSet<String>());
		}
		selector.getFields().add("localityId");
    	Map<String,Map<String, Integer>> solrProjectStatusCountAndProjectCount = propertyDao.getProjectStatusCountAndProjectOnLocalityByCity(selector);
    	
    	List<Integer> localityIds = getLocalityIdsOnPropertySelector(solrProjectStatusCountAndProjectCount);
    	
    	List<Locality> localities = localityDao.findByLocalityIds(localityIds, selector);
    	
    	Map<String, Map<String, Map<String, FieldStatsInfo>>> priceStats = propertyDao.getStatsFacetsAsMaps(selector, 
    			Arrays.asList("pricePerUnitArea", "resalePrice"), Arrays.asList("localityId") );
    	setProjectStatusCountAndProjectCountAndPriceOnLocality(localities, solrProjectStatusCountAndProjectCount, priceStats);
    	return localities;
    }
    
    public void setProjectStatusCountAndProjectCountAndPriceOnLocality(List<Locality> localities, Map<String, 
    		Map<String, Integer>> solrProjectStatusCountAndProjectCount,
    		Map<String, Map<String, Map<String, FieldStatsInfo>>> priceStats){
    	
    	Map<Integer, Map<String, Integer>> localityProjectStatusCount = getProjectStatusCountOnLocalityByCity(solrProjectStatusCountAndProjectCount.get("LOCALITY_ID_PROJECT_STATUS"));
    	Map<String, Integer> projectCountOnLocality = solrProjectStatusCountAndProjectCount.get("LOCALITY_ID");
    	Map<String, FieldStatsInfo> resalePriceStats = priceStats.get("resalePrice").get("LOCALITY_ID");
    	Map<String, FieldStatsInfo> primaryPriceStats = priceStats.get("pricePerUnitArea").get("LOCALITY_ID");
    	    	
    	int size = localities.size();
    	Locality locality;
    	Integer projectCount;
    	int localityId;
    	for(int i=0; i<size; i++)
    	{
    		locality = localities.get(i);
    		localityId = locality.getLocalityId();
    		String localityIdStr = localityId+"";
    		// setting Project Count
    		locality.setProjectStatusCount( localityProjectStatusCount.get(localityId) );
    		projectCount = projectCountOnLocality.get( localityIdStr );
    		if( projectCount != null )
    			locality.setProjectCount( projectCount.intValue() );
    		
    		// setting Resale Prices
    		FieldStatsInfo fieldStatsInfo = resalePriceStats.get(localityIdStr);
    		if(fieldStatsInfo != null)
    		{
    			locality.setMinResalePrice( (Double)fieldStatsInfo.getMin() );
    			locality.setMaxResalePrice( (Double)fieldStatsInfo.getMax() );
    		}
    		
    		// setting Primary Prices
    		fieldStatsInfo = primaryPriceStats.get(localityIdStr);
    		if(fieldStatsInfo != null)
    		{
    			locality.setAvgPricePerUnitArea( (Double)fieldStatsInfo.getMean() );
    		}
    	}

    }
    
    public Map<Integer, Map<String, Integer>> getProjectStatusCountOnLocalityByCity(Map<String, Integer> solrProjectStatusCount) {
    	Map<Integer, Map<String, Integer>> localityProjectStatusCount = new HashMap<Integer, Map<String,Integer>>();
    	String[] split;
    	Integer localityId;
    	Map<String, Integer> projectStatusCount=null; 
    	for	(Map.Entry<String, Integer> entry : solrProjectStatusCount.entrySet()){
    		//localityId:projectStatus
    		split = entry.getKey().split(":");
    		localityId = Integer.parseInt(split[0]);
    		projectStatusCount = localityProjectStatusCount.get(localityId);
    		if(projectStatusCount == null)
    		{
    			projectStatusCount = new HashMap<String, Integer>();
    		}
    		projectStatusCount.put(split[1], entry.getValue());
    		localityProjectStatusCount.put(localityId, projectStatusCount);
    	
    	}
    	return localityProjectStatusCount;
   }
    
   public Double getMaxRadiusForLocalityOnProject(int localityId){
	   List<Locality> localities = localityDao.findByLocationOrderByPriority(localityId, "locality", null, null);//findByLocalityId(localityId);
	   if(localities.size() < 1)
		   return null;
	   Locality locality = localities.get(0);
		   
	   List<SolrResult> projectSolrResults = projectDao.getProjectsByGEODistanceByLocality(localityId, locality.getLatitude()
			   , locality.getLongitude(), 1);
	   
	   if(projectSolrResults.size() > 0)
		   return projectSolrResults.get(0).getProject().getLocality().getMaxRadius();
	   return null;
   }
   
	/**
	 * Get locality for locality id
	 * @param localityId
	 * @return
	 */
	public Locality getLocality(int localityId) {
		return localityDao.findOne(localityId);
	}
	
	/**
	 * This method get locality information with some more application specific data.
	 * @param localityId
	 * @return
	 */
	public Locality getLocalityInfo(int localityId, Integer imageCount) {
		logger.debug("Get locality info for locality id {}",localityId);
		Locality locality = getLocality(localityId);

		Map<String, Object> localityReviewDetails = localityReviewService
				.findReviewByLocalityId(localityId, null);

		int totalImages = 0;
		List<Image> images = imageService.getImages(DomainObject.locality,
				null, localityId);
		if (images != null) {
			totalImages = images.size();
		}
		List<LocalityAmenity> amenities = localityAmenityService
				.getLocalityAmenities(localityId, null);
		Map<String, Integer> localityAmenityCountMap = getLocalityAmenitiesCount(amenities);

		locality.setAmenityTypeCount(localityAmenityCountMap);
		locality.setAverageRating(localityReviewDetails
				.get(LocalityReviewService.AVERAGE_RATINGS) == null ? 0
				: (Double) localityReviewDetails
						.get(LocalityReviewService.AVERAGE_RATINGS));
		locality.setImageCount(totalImages);
		if(images != null){
			Iterator<Image> imageItr = images.iterator();
			int counter = 0;
			List<String> imagePath = new ArrayList<>();
			while(imageItr.hasNext() && counter++ < imageCount){
				Image image = imageItr.next();
				imagePath.add(image.getAbsolutePath());
			}
			locality.setImagesPath(imagePath);
		}
		locality.setRatingsCount(localityReviewDetails
				.get(LocalityReviewService.TOTAL_RATINGS) == null ? 0
				: (Long) localityReviewDetails
						.get(LocalityReviewService.TOTAL_RATINGS));
		locality.setTotalReviews(localityReviewDetails
				.get(LocalityReviewService.TOTAL_REVIEWS) == null ? 0
				: (Long) localityReviewDetails
						.get(LocalityReviewService.TOTAL_REVIEWS));
		return locality;
	}

	/**
	 * This methods returns the number of each amenities.
	 * @param amenities
	 * @return
	 */
	private Map<String, Integer> getLocalityAmenitiesCount(
			List<LocalityAmenity> amenities) {
		Map<Integer, LocalityAmenityTypes> amenityTypes = amenityTypeService.getLocalityAmenityTypes();
		Map<String, Integer> localityAmenityCountMap = new HashMap<>();
		for(LocalityAmenity amenity: amenities){
			LocalityAmenityTypes amenityType = amenityTypes.get(amenity.getPlaceTypeId());
			if(amenityType != null){
				Integer count = localityAmenityCountMap.get(amenityType.getDisplayName());
				if(count == null){
					count = 1;
				}
				else{
					count = count + 1;
				}
				localityAmenityCountMap.put(amenityType.getDisplayName(), count);
			}
		}
		return localityAmenityCountMap;
	}
	
	/**
	 * Get popular localities of city or suburb based on priority and in case of
	 * tie base on enquiry in last enquiryInWeeks weeks in desc order
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
		Long timeStmap = enquiryCreationDate.getTime()/1000;
		//TODO need to change this to get from localityDao
//		List<Object[]> localities = localityDao
//				.getPopularLocalitiesOfCityOrderByPriorityASCAndTotalEnquiryDESC(
//						cityId, suburbId, timeStmap);
		
		List<Locality> result = localityDaoImpl.getPopularLocalities(cityId, suburbId, timeStmap);
		return result;
	}
	
	private List<Integer> getLocalityIdsOnPropertySelector(Map<String,Map<String, Integer>> solrMap){
		Map<String, Integer> projectCountOnLocality = solrMap.get("LOCALITY_ID");
		
		List<Integer> localities = new ArrayList<>();
		for	(Map.Entry<String, Integer> entry : projectCountOnLocality.entrySet()){
			localities.add( Integer.parseInt( entry.getKey() ) );
		}
    	    	    	
    	return localities;
	}
		
}
