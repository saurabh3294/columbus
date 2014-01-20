package com.proptiger.data.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.B2b;
import com.proptiger.data.pojo.Selector;


public interface B2bDao extends
	PagingAndSortingRepository<B2b, Integer>, B2bCustomDao{
}