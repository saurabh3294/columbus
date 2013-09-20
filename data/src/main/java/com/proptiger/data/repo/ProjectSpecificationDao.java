/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import com.proptiger.data.model.ProjectSpecification;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *
 * @author mukand
 */
public interface ProjectSpecificationDao extends PagingAndSortingRepository<ProjectSpecification, Integer>{
    public ProjectSpecification findById(int projectId);
}
