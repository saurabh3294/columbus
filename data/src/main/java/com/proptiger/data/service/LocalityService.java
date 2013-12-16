/**
 * 
 */
package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
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


/**
 * @author mandeep
 * @author Rajeev Pandey
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
    
    public List<Locality> getLocalities(Selector selector) {
        return Lists.newArrayList(localityDao.getLocalities(selector));
    }
    
    public List<Locality> getLocalityListing(int cityId){
    	List<Locality> localities = localityDao.findByCityIdAndIsActiveAndDeletedFlagOrderByPriorityAsc(cityId, true, true, null);
    	setProjectStatusCountAndProjectCountOnLocality(localities, cityId);
    	return localities;
    }
    
    public void setProjectStatusCountAndProjectCountOnLocality(List<Locality> localities, int cityId){
    	Map<String,Map<String, Integer>> solrProjectStatusCountAndProjectCount = projectDao.getProjectStatusCountAndProjectOnLocalityByCity(cityId);
    	Map<Integer, Map<String, Integer>> localityProjectStatusCount = getProjectStatusCountOnLocalityByCity(cityId, 
    																										solrProjectStatusCountAndProjectCount.get("LOCALITY_ID_PROJECT_STATUS"));
    	Map<String, Integer> projectCountOnLocality = solrProjectStatusCountAndProjectCount.get("LOCALITY_ID");
    	long totalProjectCountsOnCity = projectDao.getProjectCountCity(cityId);
    	
    	int size = localities.size();
    	Locality locality;
    	Integer projectCount;
    	for(int i=0; i<size; i++)
    	{
    		locality = localities.get(i);
    		locality.setProjectStatusCount( localityProjectStatusCount.get(locality.getLocalityId()) );
    		projectCount = projectCountOnLocality.get( locality.getLocalityId()+"" );
    		if( projectCount != null )
    			locality.setProjectCount( projectCount.intValue() );
    		
    		locality.getSuburb().getCity().setProjectsCount(totalProjectCountsOnCity);
    	}

    }
    
    public Map<Integer, Map<String, Integer>> getProjectStatusCountOnLocalityByCity(int cityId, Map<String, Integer> solrProjectStatusCount) {
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
	   Locality locality = localityDao.findByLocalityId(localityId);
	   List<SolrResult> projectSolrResults = projectDao.getProjectsByGEODistanceByLocality(localityId, locality.getLatitude()
			   , locality.getLongitude(), 1);
	   
	   if(projectSolrResults.size() > 0)
		   return projectSolrResults.get(0).getProject().getLocality().getDerivedMaxRadius();
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

		locality.setDerivedAmenityTypeCount(localityAmenityCountMap);
		locality.setDerivedAverageRating(localityReviewDetails
				.get(LocalityReviewService.AVERAGE_RATINGS) == null ? 0
				: (Double) localityReviewDetails
						.get(LocalityReviewService.AVERAGE_RATINGS));
		locality.setDerivedImageCount(totalImages);
		if(images != null){
			Iterator<Image> imageItr = images.iterator();
			int counter = 0;
			List<String> imagePath = new ArrayList<>();
			while(imageItr.hasNext() && counter++ < imageCount){
				Image image = imageItr.next();
				imagePath.add(image.getAbsolutePath());
			}
			locality.setDerivedImagesPath(imagePath);
		}
		locality.setDerivedTotalRating(localityReviewDetails
				.get(LocalityReviewService.TOTAL_RATINGS) == null ? 0
				: (Long) localityReviewDetails
						.get(LocalityReviewService.TOTAL_RATINGS));
		locality.setDerivedTotalReviews(localityReviewDetails
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
}
