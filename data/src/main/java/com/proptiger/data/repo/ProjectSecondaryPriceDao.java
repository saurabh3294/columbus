package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.ProjectSecondaryPrice;

@Repository
@Deprecated
public interface ProjectSecondaryPriceDao extends PagingAndSortingRepository<ProjectSecondaryPrice, Integer>{
	@Query(" select psp from ProjectSecondaryPrice psp where id IN "
			+ " ( select max(id) as id from ProjectSecondaryPrice group by projectId )  ")
	List<ProjectSecondaryPrice> getLatestProjectPrices();
	
	List<ProjectSecondaryPrice> findByProjectIdOrderByIdDesc(int projectId, Pageable pageable);
}
