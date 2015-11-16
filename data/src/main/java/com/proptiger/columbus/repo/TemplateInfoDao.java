package com.proptiger.columbus.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.columbus.model.TemplateInfo;

public interface TemplateInfoDao extends PagingAndSortingRepository<TemplateInfo, Integer> {
    
    public TemplateInfo findByTemplateType(String templateType);

}
