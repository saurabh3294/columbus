package com.proptiger.data.service.portfolio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.data.dto.DashboardDto;
import com.proptiger.data.model.portfolio.Dashboard;
import com.proptiger.data.model.resource.Resource;
import com.proptiger.data.repo.portfolio.DashboardDao;
import com.proptiger.exception.DuplicateResourceException;

/**
 * Dashboard service class to provide CRUD operation  over Dashboard resource
 * @author Rajeev Pandey
 *
 */
@Component
public class DashboardService extends AbstractService{

	@Autowired
	private DashboardDao dashboardDao;

	
	public List<Dashboard> getAllByUserId(Integer userId){
		List<Dashboard> result = dashboardDao.findByUserId(userId);
		return result;
	}
	
	public List<Dashboard> getDashboardById(Integer userId, Integer dashboardId){
		List<Dashboard> result = dashboardDao.findByIdAndUserId(dashboardId, userId);
		return result;
	}
	
	public Integer createDashboard(DashboardDto dashboardDto) {
		Dashboard dashboard = Dashboard
				.getBuilder(dashboardDto.getName(), dashboardDto.getUserId())
				.setTotalColumns(dashboardDto.getTotalColumn())
				.setTotalRows(dashboardDto.getTotalRows()).build();
		preProcessCreate(dashboard);
		Dashboard created = dashboardDao.save(dashboard);
		return created.getId();
	}

	@Override
	protected <T extends Resource> void preProcessCreate(T resource) {
		super.preProcessCreate(resource);
		Dashboard toCreate = (Dashboard)resource;
		Dashboard dashboardPresent = dashboardDao.findByNameAndUserId(toCreate.getName(), toCreate.getUserId());
		if(dashboardPresent != null){
			throw new DuplicateResourceException("Resource with same name exist");
		}
	}
}
