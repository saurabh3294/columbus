/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import java.io.Serializable;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.ProjectDB;

/**
 *
 * @author mukand
 */
@Repository
public interface ProjectDBDao extends PagingAndSortingRepository<ProjectDB, Serializable>{
    public ProjectDB findByProjectId(int projectId);
}
