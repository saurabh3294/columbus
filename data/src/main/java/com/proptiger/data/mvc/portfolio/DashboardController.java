package com.proptiger.data.mvc.portfolio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.dto.DashboardDto;
import com.proptiger.data.model.portfolio.Dashboard;
import com.proptiger.data.model.portfolio.DashboardWidgetMapping;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.portfolio.DashboardService;

/**
 * This class provides the various API to interact with Dash board resource
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "data/v1/entity/user/{userId}/dashboard")
public class DashboardController extends BaseController{

	@Autowired
	private DashboardService dashboardService;
	
	/**
	 * Get all dashboard for user id
	 * @param userId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ProAPIResponse getDashboards(@PathVariable Integer userId){
		List<Dashboard> result = dashboardService.getAllByUserId(userId);
		return new ProAPISuccessResponse(result, result.size());
	}
	
	/**
	 * This method get a dashboard for dashboard id
	 * @param userId
	 * @param dashboardId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/{dashboardId}")
	@ResponseBody
	public ProAPIResponse getDashboard(@PathVariable Integer userId, @PathVariable Integer dashboardId){
		Dashboard result = dashboardService.getDashboardById(userId, dashboardId);
		return new ProAPISuccessResponse(result, 1);
	}
	
	/**
	 * Creates a dashboard
	 * @param userId
	 * @param dashboardDto
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ProAPIResponse createDashboard(@PathVariable Integer userId, @RequestBody(required = true) DashboardDto dashboardDto){
		dashboardDto.setUserId(userId);
		Dashboard result = dashboardService.createDashboard(dashboardDto);
		return new ProAPISuccessResponse(result);
	}
	
	/**
	 * This  method updates a dashboard, but it will update only dashboard properties and not the widget mapping part.
	 * To update widget's positioning mapped with a dashboard use method 
	 * @param userId
	 * @param dashboardId
	 * @param dashboardDto
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT, value = "/{dashboardId}")
	@ResponseBody
	public ProAPIResponse updateDashboard(
			@PathVariable Integer userId,
			@PathVariable Integer dashboardId,
			@RequestBody(required = true) DashboardDto dashboardDto){
		dashboardDto.setId(dashboardId);
		dashboardDto.setUserId(userId);
		Dashboard dashboard = dashboardService.updateDashboard(dashboardDto);
		return new ProAPISuccessResponse(dashboard, 1);
	}
	
	/**
	 * Get all widgets associated with dashboard id
	 * @param userId
	 * @param dashboardId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/{dashboardId}/widget")
	@ResponseBody
	public ProAPIResponse getWidgetMappingWithDashboard(
			@PathVariable Integer userId,
			@PathVariable Integer dashboardId){
		Dashboard dashboard = dashboardService.getDashboardById(userId, dashboardId);
		return new ProAPISuccessResponse(dashboard.getWidgets(), dashboard.getWidgets() == null? 0: dashboard.getWidgets().size());
	}
	
	/**
	 * Get a widget association object for widget id and dashboard id
	 * @param userId
	 * @param dashboardId
	 * @param widgetId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/{dashboardId}/widget/{widgetId}")
	@ResponseBody
	public ProAPIResponse getSingleWidgetMappingWithDashboard(
			@PathVariable Integer userId,
			@PathVariable Integer dashboardId, @PathVariable Integer widgetId){
		Dashboard dashboard = dashboardService.getDashboardById(userId, dashboardId);
		return new ProAPISuccessResponse(dashboard.getWidgets(), dashboard.getWidgets() == null? 0: dashboard.getWidgets().size());
	}
	
	/**
	 * @param userId
	 * @param dashboardId
	 * @param dashboardWidgetMapping
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/{dashboardId}/widget")
	@ResponseBody
	public ProAPIResponse createWidgetMappingWithDashboard(
			@PathVariable Integer userId,
			@PathVariable Integer dashboardId,
			@RequestBody(required = true) DashboardWidgetMapping dashboardWidgetMapping){
		Dashboard dashboard = dashboardService.createSingleWidget(userId, dashboardId, dashboardWidgetMapping);
		return new ProAPISuccessResponse(dashboard, 1);
	}
	/**
	 * This method updates a provided widget id mapping with provided dashboard id 
	 * @param userId
	 * @param dashboardId
	 * @param widgetId
	 * @param dashboardDto
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT, value = "/{dashboardId}/widget/{widgetId}")
	@ResponseBody
	public ProAPIResponse updateWidgetMappingWithDashboard(
			@PathVariable Integer userId,
			@PathVariable Integer dashboardId,
			@PathVariable Integer widgetId,
			@RequestBody(required = true) DashboardWidgetMapping dashboardWidgetMapping){
		Dashboard dashboard = dashboardService.updateWidgetMappingWithDashboard(userId, dashboardId, widgetId, dashboardWidgetMapping);
		return new ProAPISuccessResponse(dashboard, 1);
	}
	
	/**
	 * @param userId
	 * @param dashboardId
	 * @param widgetId
	 * @param dashboardWidgetMapping
	 * @return
	 */
	@RequestMapping(method = RequestMethod.DELETE, value = "/{dashboardId}/widget/{widgetId}")
	@ResponseBody
	public ProAPIResponse deleteWidgetFromDashboard(
			@PathVariable Integer userId,
			@PathVariable Integer dashboardId,
			@PathVariable Integer widgetId,
			@RequestBody(required = true) DashboardWidgetMapping dashboardWidgetMapping){
		Dashboard dashboard = dashboardService.deleteWidgetMappingWithDashboard(userId, dashboardId, widgetId, dashboardWidgetMapping);
		return new ProAPISuccessResponse(dashboard, 1);
	}
	@RequestMapping(method = RequestMethod.DELETE, value = "/{dashboardId}")
	@ResponseBody
	public ProAPIResponse deleteDashboard(@PathVariable Integer userId, @PathVariable Integer dashboardId){
		Dashboard deleted = dashboardService.deleteDashboard(userId, dashboardId);
		return new ProAPISuccessResponse(deleted, 1);
	}
}
