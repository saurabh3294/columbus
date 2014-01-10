package com.proptiger.data.service.portfolio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.internal.dto.UserWishList;
import com.proptiger.data.model.UserWishlist;
import com.proptiger.data.repo.portfolio.UserWishListDao;

/**
 * @author Rajeev Pandey
 *
 */
@Component
public class UserWishListService {

	@Autowired
	private UserWishListDao userWishListDao;
	
	/**
	 * This method returns user wish list or favourite projects/properties details based on user id
	 * @param userId
	 * @return
	 */
	public List<UserWishList> getUserWishList(Integer userId){
		List<Object[]> result = userWishListDao.findUserWishList(userId);
		List<UserWishList> convertedResult = convertDaoResultToDtoObject(result);
		return convertedResult;
	}

	public int saveUserWishList(UserWishlist userWishlist, UserInfo userInfo){
		if(userWishlist.getProjectId() == null || userWishlist.getProjectId() < 0 || userWishlist.getTypeId() != null )
			return -1;
		
		UserWishlist alreadyUserWishlist = userWishListDao.findByProjectIdAndUserId(userWishlist.getProjectId(), userInfo.getUserIdentifier());
		if(alreadyUserWishlist != null)
			return 0;
		
		return userWishListDao.save(userWishlist) != null? 1: -2;
		
	}
	
	/**
	 * A utility method to convert Object[] list to domain object list 
	 * @param result
	 * @return
	 */
	private List<UserWishList> convertDaoResultToDtoObject(List<Object[]> result) {
		List<UserWishList> list = new ArrayList<UserWishList>();
		if(result != null){
			for(Object[] rowValues: result){
				if(rowValues.length != 10){
					throw new IllegalArgumentException("Unexpected result length");
				}
				UserWishList userWishListDto = new UserWishList();
				userWishListDto.setProjectId((Integer)rowValues[0]);
				userWishListDto.setProjectName((String)rowValues[1]);
				userWishListDto.setProjectUrl((String)rowValues[2]);
				userWishListDto.setTypeId((Integer)rowValues[3]);
				userWishListDto.setBedrooms((Integer)rowValues[4]);
				userWishListDto.setWishListId((Integer)rowValues[5]);
				userWishListDto.setCityLabel((String)rowValues[6]);
				userWishListDto.setUnitName((String)rowValues[7]);
				userWishListDto.setBuilderName((String)rowValues[8]);
				userWishListDto.setDatetime((Date)rowValues[9]);
				
				list.add(userWishListDto);
			}
		}
		return list;
	}
}
