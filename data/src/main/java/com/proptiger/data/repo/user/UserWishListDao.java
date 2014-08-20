package com.proptiger.data.repo.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.UserWishlist;

/**
 * @author Rajeev Pandey
 * 
 */
public interface UserWishListDao extends JpaRepository<UserWishlist, Integer> {

    @Query("SELECT U FROM UserWishlist U JOIN FETCH U.project as P WHERE P.version = 'Website' AND U.userId= ?1")
    public List<UserWishlist> findByUserId(Integer userId);

    @Query("SELECT U FROM UserWishlist U JOIN FETCH U.project as P WHERE P.version = 'Website' AND U.userId= ?1 AND U.typeId is null")
    public List<UserWishlist> findByUserIdAndTypeIdIsNull(Integer userId);

    @Query("SELECT U FROM UserWishlist U JOIN FETCH U.project as P WHERE P.version = 'Website' AND U.userId= ?1 AND U.typeId is not null")
    public List<UserWishlist> findByUserIdAndTypeIdIsNotNull(Integer userId);

    @Query("SELECT U FROM UserWishlist U JOIN FETCH U.project as P WHERE P.version = 'Website' AND U.projectId = ?1 AND U.typeId is null AND U.userId = ?2")
    public UserWishlist findByProjectIdAndUserId(int projectId, int userId);

}
