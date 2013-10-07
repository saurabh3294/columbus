/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.ProjectSpecification;

/**
 *
 * @author mukand
 */
public interface ProjectSpecificationDao extends PagingAndSortingRepository<ProjectSpecification, Integer>{
    public ProjectSpecification findById(int projectId);
}
