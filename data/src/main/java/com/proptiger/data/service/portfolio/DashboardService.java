package com.proptiger.data.service.portfolio;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.dto.DashboardDto;
import com.proptiger.data.model.portfolio.Dashboard;
import com.proptiger.data.model.resource.Resource;
import com.proptiger.data.repo.portfolio.DashboardDao;
import com.proptiger.exception.ConstraintViolationException;
import com.proptiger.exception.DuplicateResourceException;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 * Dashboard service class to provide CRUD operation  over Dashboard resource
 * @author Rajeev Pandey
 *
 */
@Component
public class DashboardService extends AbstractService{

	private static Logger logger = LoggerFactory.getLogger(DashboardService.class);
	
	@Autowired
	private DashboardDao dashboardDao;

	/**
	 * Finds all dashboard for given user id
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Dashboard> getAllByUserId(Integer userId){
		logger.debug("Finding all dashboards for userid {}"+userId);
		List<Dashboard> result = dashboardDao.findByUserId(userId);
		return result;
	}
	
	/**
	 * Finds dashboard for given user id and dashboard id
	 * @param userId
	 * @param dashboardId
	 * @throws ResourceNotAvailableException
	 * @return
	 */
	@Transactional(readOnly = true)
	public Dashboard getDashboardById(Integer userId, Integer dashboardId){
		logger.debug("Finding dashboard {} for userid {}",userId, dashboardId);
		Dashboard result = dashboardDao.findByIdAndUserId(dashboardId, userId);
		if(result == null){
			logger.error("Dashboard id {} not found for userid {}",dashboardId, userId);
			throw new ResourceNotAvailableException("Resource not available");
		}
		return result;
	}
	
	/**
	 * Creating a dashboard resource
	 * @param dashboardDto
	 * @return
	 */
	@Transactional(rollbackFor = DuplicateResourceException.class)
	public Dashboard createDashboard(DashboardDto dashboardDto) {
		logger.debug("Creating dashboard for userid {}",dashboardDto.getUserId());
		Dashboard dashboard = Dashboard
				.getBuilder(dashboardDto.getName(), dashboardDto.getUserId())
				.setTotalColumns(dashboardDto.getTotalColumn())
				.setTotalRows(dashboardDto.getTotalRows()).build();
		preProcessCreate(dashboard);
		Dashboard created = null;
		try{
			created = dashboardDao.save(dashboard);
		}catch(Exception exception){
			throw new ConstraintViolationException(exception.getMessage(), exception);
		}
		logger.debug("Created dashboard id {} for userid {}",created.getId(),dashboardDto.getUserId());
		return created;
	}

	/* (non-Javadoc)
	 * @see com.proptiger.data.service.portfolio.AbstractService#preProcessCreate(com.proptiger.data.model.resource.Resource)
	 */
	@Override
	protected <T extends Resource> void preProcessCreate(T resource) {
		super.preProcessCreate(resource);
		Dashboard toCreate = (Dashboard)resource;
		Dashboard dashboardPresent = dashboardDao.findByNameAndUserId(toCreate.getName(), toCreate.getUserId());
		if(dashboardPresent != null){
			logger.error("Duplicate resource {}",dashboardPresent.getId());
			throw new DuplicateResourceException("Resource with same name exist");
		}
	}
	
	/**
	 * Updating a dashboard resource
	 * @param dashboardDto
	 * @throws ResourceNotAvailableException
	 * @return
	 */
	@Transactional(rollbackFor = ResourceNotAvailableException.class)
	public Dashboard updateDashboard(DashboardDto dashboardDto){
		logger.debug("Updating dashboard id {} for userid {}",dashboardDto.getId(), dashboardDto.getUserId());
		Dashboard dashboard = Dashboard
				.getBuilder(dashboardDto.getName(), dashboardDto.getUserId())
				.setTotalColumns(dashboardDto.getTotalColumn())
				.setTotalRows(dashboardDto.getTotalRows())
				.setId(dashboardDto.getId()).build();
		Dashboard updated = preProcessUpdate(dashboard);
		updated.update(dashboard.getName(), dashboard.getTotalColumns(), dashboard.getTotalRows());
		return updated;
		
	}
	
	/* (non-Javadoc)
	 * @see com.proptiger.data.service.portfolio.AbstractService#preProcessUpdate(com.proptiger.data.model.resource.Resource)
	 */
	
	@Override
	@SuppressWarnings("unchecked")
	protected <T extends Resource> T preProcessUpdate(T resource) {
		super.preProcessUpdate(resource);
		Dashboard toUpdate = (Dashboard)resource;
		Dashboard dashboardPresent = dashboardDao.findOne(toUpdate.getId());
		if(dashboardPresent == null){
			logger.error("Dashboard id {} not found",toUpdate.getId());
			throw new ResourceNotAvailableException("Resource "+toUpdate.getId()+" not available");
		}
		return (T) dashboardPresent;
	}
	
	/**
	 * Deletes a dashboard resource from data store
	 * @throws ResourceNotAvailableException
	 * @param userId
	 * @param dashboardId
	 * @return
	 */
	public Dashboard deleteDashboard(Integer userId, Integer dashboardId){
		Dashboard deleted = getDashboardById(userId, dashboardId);
		dashboardDao.delete(deleted);
		return deleted;
	}
}