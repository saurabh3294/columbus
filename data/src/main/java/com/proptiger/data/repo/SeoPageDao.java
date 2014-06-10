package com.proptiger.data.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.SeoPage;

@Repository
public interface SeoPageDao extends PagingAndSortingRepository<SeoPage, String> {

}
