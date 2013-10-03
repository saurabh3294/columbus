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
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.portfolio.DashboardService;

/**
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "data/v1/entity/{userId}/dashboard")
public class DashboardController extends BaseController{

	@Autowired
	private DashboardService dashboardService;
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ProAPIResponse getDashboards(@PathVariable Integer userId){
		List<Dashboard> result = dashboardService.getAllByUserId(userId);
		return new ProAPISuccessResponse(result, result.size());
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/{dashboardId}")
	@ResponseBody
	public ProAPIResponse getDashboard(@PathVariable Integer userId, @PathVariable Integer dashboardId){
		List<Dashboard> result = dashboardService.getDashboardById(userId, dashboardId);
		return new ProAPISuccessResponse(result, result.size());
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ProAPIResponse createDashboard(@PathVariable Integer userId, @RequestBody(required = true) DashboardDto dashboardDto){
		dashboardDto.setUserId(userId);
		Integer result = dashboardService.createDashboard(dashboardDto);
		return new ProAPISuccessResponse(result);
	}
	
	@RequestMapping(method = RequestMethod.PUT, value = "/{dashboardId}")
	@ResponseBody
	public ProAPIResponse updateDashboard(@PathVariable Integer userId, @PathVariable Integer dashboardId){
		
		return new ProAPISuccessResponse();
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/{dashboardId}")
	@ResponseBody
	public ProAPIResponse deleteDashboard(@PathVariable Integer userId, @PathVariable Integer dashboardId){
		
		return new ProAPISuccessResponse();
	}
}
