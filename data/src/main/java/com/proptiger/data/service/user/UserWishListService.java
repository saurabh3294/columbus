package com.proptiger.data.service.user;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.internal.dto.UserWishListDto;
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

    private static Logger   logger = LoggerFactory.getLogger(UserWishListService.class);

    @Autowired
    private UserWishListDao userWishListDao;

    @Autowired
    private ProjectService  projectService;

    /**
     * This method returns user wish list or favourite projects/properties
     * details based on user id
     * 
     * @param userId
     * @return
     */
    @Transactional
    public List<UserWishListDto> getUserWishList(Integer userId) {
        List<UserWishlist> list = userWishListDao.findByUserId(userId);
        List<UserWishListDto> convertedResult = convertDaoResultToDtoObject(list);
        return convertedResult;
    }

    /**
     * This method returns user wish list or favourite projects details based on
     * user id
     * 
     * @param userId
     * @return
     */
    @Transactional
    public List<UserWishListDto> getProjectUserWishList(Integer userId) {
        List<UserWishlist> list = userWishListDao.findByUserIdAndTypeIdIsNull(userId);
        List<UserWishListDto> convertedResult = convertDaoResultToDtoObject(list);
        return convertedResult;
    }

    /**
     * This method returns user wish list or favourite properties details based
     * on user id
     * 
     * @param userId
     * @return
     */
    @Transactional
    public List<UserWishListDto> getPropertyUserWishList(Integer userId) {
        List<UserWishlist> list = userWishListDao.findByUserIdAndTypeIdIsNotNull(userId);
        List<UserWishListDto> convertedResult = convertDaoResultToDtoObject(list);
        return convertedResult;
    }

    /**
     * This method will delete the wish list based on the wish list id.
     * 
     * @param wishlistId
     */
    @Transactional
    public List<UserWishListDto> deleteWishlist(int wishlistId) {
        UserWishlist userWishlist = userWishListDao.findOne(wishlistId);
        if (userWishlist == null)
            throw new IllegalArgumentException("Wish List Id does not exists.");

        userWishListDao.delete(wishlistId);
        if (userWishlist.getTypeId() == null) {
            return getProjectUserWishList(userWishlist.getUserId());
        }
        else {
            return getPropertyUserWishList(userWishlist.getUserId());
        }
    }

    /**
     * This method will save the project Id in the Wish List. It will only take
     * project Id to be saved. It will validate the project Id whether it exists
     * in the database or already present in the wish list. If not then it will
     * save and return the get response of the new id created.
     * 
     * @param userWishlist
     * @param userId
     * @return
     */
    
    public List<UserWishListDto> createUserWishList(UserWishlist userWishlist, Integer userId) {
        if (userWishlist.getProjectId() == null || userWishlist.getProjectId() < 0 || userWishlist.getTypeId() != null)
            throw new IllegalArgumentException("Invalid Project Id. Property Id not allowed.");

        UserWishlist alreadyUserWishlist = userWishListDao
                .findByProjectIdAndUserId(userWishlist.getProjectId(), userId);
        if (alreadyUserWishlist != null)
            throw new ResourceAlreadyExistException("Project Id already exists as Favourite.");
        if (projectService.getProjectDetails(userWishlist.getProjectId()) == null)
            throw new IllegalArgumentException("Project Id does not exists.");

        userWishlist.setUserId(userId);
        UserWishlist savedObject = userWishListDao.save(userWishlist);

        if (savedObject.getTypeId() == null) {
            return getProjectUserWishList(userId);
        }
        else {
            return getPropertyUserWishList(userId);
        }
    }

    /**
     * A utility method to convert Object[] list to domain object list
     * 
     * @param result
     * @return
     */
    private List<UserWishListDto> convertDaoResultToDtoObject(List<UserWishlist> result) {
        List<UserWishListDto> list = new ArrayList<UserWishListDto>();
        if (result != null) {
            for (UserWishlist userWishlist : result) {
                UserWishListDto userWishListDto = convertToUserListDto(userWishlist);

                list.add(userWishListDto);
            }
        }
        return list;
    }

    private UserWishListDto convertToUserListDto(UserWishlist userWishlist) {
        UserWishListDto userWishListDto = new UserWishListDto();
        Project project = userWishlist.getProject();
        Property property = userWishlist.getProperty();

        String city = null;
        String projectName = null;
        String projectUrl = null;
        String builderName = null;
        if (project != null) {
            projectName = project.getName();
            projectUrl = project.getURL();
            city = project.getLocality().getSuburb().getCity().getLabel();
            builderName = project.getBuilder().getName();
        }
        String unitName = null;
        Integer bedrooms = null;
        if (property != null) {
            try {
                unitName = property.getUnitName();
                bedrooms = property.getBedrooms();
            }
            catch (EntityNotFoundException e) {
                logger.debug("Property not found in table for id {}", userWishlist.getTypeId());
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
        return userWishListDto;
    }
}
