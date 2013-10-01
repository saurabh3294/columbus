package com.proptiger.data.service.portfolio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.data.dto.UserWishList;
import com.proptiger.data.repo.portfolio.UserWishListDao;

/**
 * @author Rajeev Pandey
 *
 */
@Component
public class UserWishListService {

	@Autowired
	private UserWishListDao userWishListDao;
	
	public List<UserWishList> getRecentlyViewdProperties(Integer userId){
		List<Object[]> result = userWishListDao.findRecentlyViewdProjects(userId);
		List<UserWishList> convertedResult = convertDaoResultToDtoObject(result);
		return convertedResult;
	}

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
