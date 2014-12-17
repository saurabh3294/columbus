package com.proptiger.data.repo.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.core.model.user.UserRoles;

public interface UserRolesDao extends JpaRepository<UserRoles, Integer>{

    @Query("select MR.name from UserRoles UR join UR.role MR where UR.userId=?1")
    public List<String> getUserRolesName(int userId);
}
