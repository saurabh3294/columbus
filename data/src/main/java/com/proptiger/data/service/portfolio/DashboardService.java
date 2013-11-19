package com.proptiger.data.service.portfolio;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.internal.dto.DashboardDto;
import com.proptiger.data.model.portfolio.Dashboard;
import com.proptiger.data.model.portfolio.DashboardWidgetMapping;
import com.proptiger.data.model.portfolio.WidgetDisplayStatus;
import com.proptiger.data.model.resource.NamedResource;
import com.proptiger.data.model.resource.Resource;
import com.proptiger.data.repo.portfolio.DashboardDao;
import com.proptiger.data.repo.portfolio.DashboardWidgetMappingDao;
import com.proptiger.data.repo.portfolio.WidgetDao;
import com.proptiger.data.util.Constants;
import com.proptiger.exception.ConstraintViolationException;
import com.proptiger.exception.DuplicateNameResourceException;
import com.proptiger.exception.DuplicateResourceException;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 * Dashboard service class to provide CRUD operation  over Dashboard resource
 * @author Rajeev Pandey
 *
 */
@Service
public class DashboardService extends AbstractService{

	private static Logger logger = LoggerFactory.getLogger(DashboardService.class);
	
	@Autowired
	private DashboardDao dashboardDao;
	@Autowired
	private WidgetDao widgetDao;
	@Autowired
	private DashboardWidgetMappingDao dashboardWidgetMappingDao;
	/**
	 * Finds all dashboard for given user id
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
	@Cacheable(value = "dashboard", key = "userId")
	public List<Dashboard> getAllByUserId(Integer userId){
		logger.debug("Finding all dashboards for userid {}"+userId);
		List<Dashboard> result = dashboardDao.findByUserId(userId);
		
		if(result != null && result.size() == 0){
			logger.debug("creating default dashboard and widgets as of admin for user {}",userId);
			/*
			 * No dashboard exists for this user, need to create defaule dashboard
			 * and and dashboard widget mapping by taking input from admin's mapping 
			 */
			List<Dashboard> adminsDashboard = dashboardDao.findByUserId(Constants.ADMIN_USER_ID);
			logger.debug("Dasboard and widgets for admin is {}",adminsDashboard);
			//need to create copy for current user
			if(adminsDashboard != null && !adminsDashboard.isEmpty()){
				for(Dashboard dashboard: adminsDashboard){
					DashboardDto dashboardDto = new DashboardDto();
					dashboardDto.setName(dashboard.getName());
					dashboardDto.setTotalColumn(dashboard.getTotalColumn());
					dashboardDto.setTotalRow(dashboard.getTotalRow());
					dashboardDto.setUserId(userId);
					dashboardDto.setWidgets(dashboard.getWidgets());
					createDashboard(dashboardDto);
				}
				//again retrieving dashboards for user after creation
				result = dashboardDao.findByUserId(userId);
			}
		}
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
	@Cacheable(value = "dashboard")
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
	 * Find a dashboard widget mapping
	 * @param userId
	 * @param dashboardId
	 * @param widgetId
	 * @return
	 */
	@Transactional(readOnly = true)
	public DashboardWidgetMapping getSingleWidgetMapping(Integer userId, Integer dashboardId, Integer widgetId){
		Dashboard dashboard = getDashboardById(userId, dashboardId);
		DashboardWidgetMapping toFind = null;
		if(dashboard.getWidgets() != null){
			for(DashboardWidgetMapping present: dashboard.getWidgets()){
				if(present.getId().equals(widgetId)){
					toFind = present;
					break;
				}
			}
		}
		if(toFind == null){
			logger.error("Widget id {} not found for dashboard id {}",widgetId, dashboardId);
			throw new ResourceNotAvailableException("Resource not available");
		}
		return toFind;
	}
	
	/* (non-Javadoc)
	 * @see com.proptiger.data.service.portfolio.AbstractService#preProcessCreate(com.proptiger.data.model.resource.Resource)
	 */
	@Override
	protected <T extends Resource & NamedResource> void preProcessCreate(T resource) {
		super.preProcessCreate(resource);
		Dashboard toCreate = (Dashboard)resource;
		Dashboard dashboardPresent = dashboardDao.findByNameAndUserId(toCreate.getName(), toCreate.getUserId());
		if(dashboardPresent != null){
			logger.error("Duplicate resource {}",dashboardPresent.getId());
			throw new DuplicateNameResourceException("Resource with same name exist");
		}
	}
	
	/**
	 * Updating a dashboard resource
	 * @param dashboardDto
	 * @throws ``
	 * @return
	 */
	@Transactional(rollbackFor = ResourceNotAvailableException.class)
	public Dashboard updateDashboard(DashboardDto dashboardDto){
		logger.debug("Updating dashboard id {} for userid {}",dashboardDto.getId(), dashboardDto.getUserId());
		Dashboard dashboard = Dashboard
				.getBuilder(dashboardDto.getName(), dashboardDto.getUserId())
				.setTotalColumns(dashboardDto.getTotalColumn())
				.setTotalRows(dashboardDto.getTotalRow())
				.setId(dashboardDto.getId()).build();
		Dashboard updated = update(dashboard);
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
	 * Deletes a dashboard resource from data store and all its association with widgets
	 * @throws ResourceNotAvailableException
	 * @param userId
	 * @param dashboardId
	 * @return
	 */
	@Transactional(rollbackFor = ResourceNotAvailableException.class)
	public Dashboard deleteDashboard(Integer userId, Integer dashboardId){
		Dashboard deleted = getDashboardById(userId, dashboardId);
		List<DashboardWidgetMapping> widgetMappings = deleted.getWidgets();
		/*
		 * First need to delete widgets association with this dashboard object 
		 */
		dashboardWidgetMappingDao.delete(widgetMappings);
		/*
		 * Now dashboard object can be deleted
		 */
		dashboardDao.delete(deleted);
		return deleted;
	}
	
	@Transactional(rollbackFor = ConstraintViolationException.class)
	public Dashboard createDashboard(DashboardDto dashboardDto){
		logger.debug("Creating dashboard for userid {}",dashboardDto.getUserId());
		Dashboard dashboard = Dashboard
				.getBuilder(dashboardDto.getName(), dashboardDto.getUserId())
				.setTotalColumns(dashboardDto.getTotalColumn())
				.setTotalRows(dashboardDto.getTotalRow()).build();
		
		Dashboard created = create(dashboard);
		/*
		 * creating dashboard and widget association
		 */
		List<DashboardWidgetMapping> widgetMappings = createDashboardWidgetMappings(created.getId(), dashboardDto.getWidgets());
		created.setWidgets(widgetMappings);
		return created;
	}
	
	/**
	 * Creating dashboard and widget mappings
	 * @param created
	 */
	@Transactional(rollbackFor = ConstraintViolationException.class)
	private List<DashboardWidgetMapping> createDashboardWidgetMappings(Integer dashboardId, List<DashboardWidgetMapping> toCreate) {
		
		List<DashboardWidgetMapping> createdWidgetsMapping = new ArrayList<DashboardWidgetMapping>();
		if(toCreate != null){
			for(DashboardWidgetMapping mapping: toCreate){
				//explicitly setting id to null as it would be auto created
				mapping.setId(null);
				mapping.setDashboardId(dashboardId);
				if(mapping.getStatus() == null){
					mapping.setStatus(WidgetDisplayStatus.MAX);
				}
				
			}
			/*
			 * Saving newly created dashboard and widget mapping
			 */
			try{
				createdWidgetsMapping = dashboardWidgetMappingDao.save(toCreate);
			}catch(Exception e){
				throw new ConstraintViolationException(e.getMessage(), e);
			}
			
		}
		
		return createdWidgetsMapping;
	}
	
	@Override
	protected <T extends Resource> T create(T resource) {
		Dashboard toCreate = (Dashboard) resource;
		preProcessCreate(toCreate);
		
		Dashboard created = null;
		try{
			created = dashboardDao.save(toCreate);
		}catch(Exception exception){
			throw new ConstraintViolationException(exception.getMessage(), exception);
		}
		logger.debug("Created dashboard id {} for userid {}",created.getId(),toCreate.getUserId());
		return (T) created;
	}

	@Override
	protected <T extends Resource> T update(T resource) {
		Dashboard dashboard = (Dashboard) resource;
		Dashboard updated = preProcessUpdate(dashboard);
		updated.update(dashboard.getName(), dashboard.getTotalColumn(), dashboard.getTotalRow());
		return (T) updated;
	}

	/**
	 * This method is adding a widget association with dashboard, it will not over right the existing mapping, rather
	 * it will add a new mapping in existing mappings
	 * @param userId
	 * @param dashboardId
	 * @param dashboardWidgetMapping
	 * @return
	 */
	@Transactional(rollbackFor = ResourceNotAvailableException.class)
	public Dashboard createSingleWidget(Integer userId,
			Integer dashboardId,
			DashboardWidgetMapping dashboardWidgetMapping){
		dashboardWidgetMapping.setId(null);
		Dashboard dashboard = getDashboardById(userId, dashboardId);
		if(dashboard.getWidgets() != null){
			for(DashboardWidgetMapping existingMapping: dashboard.getWidgets()){
				if (existingMapping.getWidgetId().equals(
						dashboardWidgetMapping.getWidgetId())) {
					logger.error("Duplicate mapping of dashboard id {} and widgetId {}",
							dashboardId, existingMapping.getWidgetId());
					throw new DuplicateResourceException(
							"Mapping of dashboard id and widget id exist");
				}
			}
		}
		dashboardWidgetMapping.setDashboardId(dashboardId);
		if(dashboardWidgetMapping.getStatus() == null){
			dashboardWidgetMapping.setStatus(WidgetDisplayStatus.MAX);
		}
		DashboardWidgetMapping createdMapping = dashboardWidgetMappingDao.save(dashboardWidgetMapping);
		dashboard.addWidget(createdMapping);
		return dashboard;
	}
	/**
	 * This method updates a dashboard widget mapping attribute
	 * @param userId
	 * @param dashboardId
	 * @param widgetId
	 * @param dashboardWidgetMapping
	 * @return
	 */
	@Transactional(rollbackFor = ResourceNotAvailableException.class)
	public Dashboard updateWidgetMappingWithDashboard(Integer userId,
			Integer dashboardId,
			Integer widgetId,
			DashboardWidgetMapping dashboardWidgetMapping){
		Dashboard dashboard = getDashboardById(userId, dashboardId);
		DashboardWidgetMapping existingMapping = getDashboardWidgetMapping(dashboardId, widgetId);
		existingMapping.update(dashboardWidgetMapping.getWidgetRowPosition(),
				dashboardWidgetMapping.getWidgetColumnPosition(),
				dashboardWidgetMapping.getStatus());
		dashboard = getDashboardById(userId, dashboardId);
		return dashboard;
	}

	/**
	 * This method get a dashboard widget mapping based on dashboard id and widget id
	 * @param dashboardId
	 * @param widgetId
	 * @return
	 */
	private DashboardWidgetMapping getDashboardWidgetMapping(Integer dashboardId, Integer widgetId) {
		DashboardWidgetMapping existingMapping = dashboardWidgetMappingDao.findByDashboardIdAndWidgetId(dashboardId, widgetId);
		if(existingMapping == null){
			logger.error("DashboardWidgetMapping not found for dashboardId {} and widgetId {}",dashboardId, widgetId);
			throw new ResourceNotAvailableException("Resource not available");
		}
		return existingMapping;
	}
	
	/**
	 * This method get all dashboards widget mapping for dashboard id
	 * @param dashboardId
	 * @param widgetId
	 * @return
	 */
	private List<DashboardWidgetMapping> getAllWidgetMapping(Integer dashboardId) {
		List<DashboardWidgetMapping> existingMappings = dashboardWidgetMappingDao.findByDashboardId(dashboardId);
		if(existingMappings == null){
			existingMappings = new ArrayList<DashboardWidgetMapping>();
		}
		return existingMappings;
	}
	
	/**
	 * This method deletes a widget association with dashboard
	 * @param userId
	 * @param dashboardId
	 * @param widgetId
	 * @param dashboardWidgetMapping
	 * @return
	 */
	@Transactional(rollbackFor = ResourceNotAvailableException.class)
	public Dashboard deleteWidgetMappingWithDashboard(Integer userId,
			Integer dashboardId,
			Integer widgetId,
			DashboardWidgetMapping dashboardWidgetMapping){
		Dashboard dashboard = getDashboardById(userId, dashboardId);
		DashboardWidgetMapping existingMapping = getDashboardWidgetMapping(dashboardId, widgetId);
		dashboardWidgetMappingDao.delete(existingMapping);
		dashboard = getDashboardById(userId, dashboardId);
		return dashboard;
	}
}
