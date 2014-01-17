package com.proptiger.data.service.portfolio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.internal.dto.UserWishList;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.UserWishlist;
import com.proptiger.data.repo.portfolio.UserWishListDao;
import com.proptiger.data.service.ProjectService;
import com.proptiger.exception.ResourceAlreadyExistException;

/**
 * @author Rajeev Pandey
 *
 */
@Component
public class UserWishListService {

	private static Logger logger = LoggerFactory.getLogger(UserWishListService.class);

	@Autowired
	private UserWishListDao userWishListDao;
	
	@Autowired
	private ProjectService projectService;
	
	/**
	 * This method returns user wish list or favourite projects/properties details based on user id
	 * @param userId
	 * @return
	 */
	@Transactional
	public List<UserWishList> getUserWishList(Integer userId){
		List<UserWishlist> list = userWishListDao.findByUserId(userId);
		List<UserWishList> convertedResult = convertDaoResultToDtoObject(list);
		return convertedResult;
	}

	public void deleteWishlist(int wishlistId) {
	    userWishListDao.delete(wishlistId);
	}

	public UserWishList saveUserWishList(UserWishlist userWishlist, Integer userId){
		if(userWishlist.getProjectId() == null || userWishlist.getProjectId() < 0 || userWishlist.getTypeId() != null )
			throw new IllegalArgumentException("Invalid Project Id. Property Id not allowed.");
		
		UserWishlist alreadyUserWishlist = userWishListDao.findByProjectIdAndUserId(userWishlist.getProjectId(), userId);
		if(alreadyUserWishlist != null)
			throw new ResourceAlreadyExistException("Project Id already exists as Favourite.");
		if( projectService.getProjectDetails( userWishlist.getProjectId() ) == null)
			throw new IllegalArgumentException("Project Id does not exists.");
		
		userWishlist.setUserId(userId);
		UserWishlist savedObject = userWishListDao.save(userWishlist);
		return convertDaoResultToDtoObject( Arrays.asList(userWishListDao.findOne(savedObject.getId())) ).get(0);
		
	}
	
	/**
	 * A utility method to convert Object[] list to domain object list 
	 * @param result
	 * @return
	 */
	private List<UserWishList> convertDaoResultToDtoObject(List<UserWishlist> result) {
		List<UserWishList> list = new ArrayList<UserWishList>();
		if(result != null){
			for(UserWishlist userWishlist: result){
				UserWishList userWishListDto = new UserWishList();
				Project project = userWishlist.getProject();
				Property property = userWishlist.getProperty();
				
				String city = null;
				String projectName = null;
				String projectUrl = null;
				String builderName = null;
				if(project != null){
					projectName = project.getName();
					projectUrl = project.getURL();
					city = project.getLocality().getSuburb().getCity().getLabel();
					builderName = project.getBuilder().getName();
				}
				String unitName = null;
				Integer bedrooms = null;
				if(property != null){
					try {
						unitName = property.getUnitName();
						bedrooms = property.getBedrooms();
					} catch (EntityNotFoundException e) {
						logger.debug("Property not found in table for id {}",userWishlist.getTypeId());
					}
				}
				
				userWishListDto.setProjectId(userWishlist.getProjectId());
				userWishListDto.setProjectName(projectName);
				userWishListDto.setProjectUrl(projectUrl);
				userWishListDto.setTypeId(userWishlist.getTypeId());
				userWishListDto.setBedrooms(bedrooms);
				userWishListDto.setWishListId(userWishlist.getId());
				userWishListDto.setCityLabel(city);
				userWishListDto.setUnitName(unitName);
				userWishListDto.setBuilderName(builderName);
				userWishListDto.setDatetime(userWishlist.getDatetime());
				
				list.add(userWishListDto);
			}
		}
		return list;
	}
}
