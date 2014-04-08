package com.proptiger.data.repo.portfolio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.UserWishlist;

/**
 * @author Rajeev Pandey
 * 
 */
public interface UserWishListDao extends JpaRepository<UserWishlist, Integer> {

    public List<UserWishlist> findByUserId(Integer userId);
    
    public List<UserWishlist> findByUserIdAndTypeIdIsNull(Integer userId);
    
    public List<UserWishlist> findByUserIdAndTypeIdIsNotNull(Integer userId);
   
    public UserWishlist findByProjectIdAndUserId(int projectId, int userId);

}
