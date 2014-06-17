package com.proptiger.data.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.RedirectUrlMap;

@Deprecated
public interface RedirectUrlMapDao extends PagingAndSortingRepository<RedirectUrlMap, String> {

}
