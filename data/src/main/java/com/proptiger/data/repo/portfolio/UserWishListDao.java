package com.proptiger.data.repo.portfolio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.UserWishlist;

/**
 * @author Rajeev Pandey
 *
 */
public interface UserWishListDao extends JpaRepository<UserWishlist, Integer>{
	
	@Query("SELECT A.projectId, A.projectName, A.projectUrl, B.typeId, B.bedrooms, C.id, " +
		   " CT.label, B.unitName, A.builderName, C.datetime " +
		   " FROM ProjectDB A, UserWishlist C, ProjectType B, City CT " +
           " WHERE A.projectId = C.projectId "+ 
           " AND C.userId = ?1 "+
           " AND B.typeId = C.typeId "+ 
           " AND (A.cityId = CT.id) "+ 
           " ")
	public List<Object[]> findUserWishList(Integer userId);
	
	public UserWishlist findByProjectIdAndUserId(int projectId, int userId);
	
}
