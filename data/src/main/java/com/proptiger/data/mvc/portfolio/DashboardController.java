package com.proptiger.data.mvc.portfolio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.DashboardDto;
import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.model.portfolio.Dashboard;
import com.proptiger.data.model.portfolio.DashboardWidgetMapping;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessCountResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.portfolio.DashboardService;
import com.proptiger.data.util.Constants;

/**
 * This class provides the various API to interact with Dash board resource
 * 
 * @author Rajeev Pandey
 * 
 */
@Controller
@RequestMapping(value = "data/v1/entity/user/{userId}/dashboard")
public class DashboardController extends BaseController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Get all dashboard for user id
     * 
     * @param userId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ProAPIResponse getDashboards(
            @PathVariable Integer userId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        List<Dashboard> result = dashboardService.getAllByUserId(userInfo.getUserIdentifier());
        return new ProAPISuccessCountResponse(result, result.size());
    }

    /**
     * This method get a dashboard for dashboard id
     * 
     * @param userId
     * @param dashboardId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{dashboardId}")
    @ResponseBody
    public ProAPIResponse getDashboard(
            @PathVariable Integer userId,
            @PathVariable Integer dashboardId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        Dashboard result = dashboardService.getDashboardById(userInfo.getUserIdentifier(), dashboardId);
        return new ProAPISuccessResponse(result);
    }

    /**
     * Creates a dashboard
     * 
     * @param userId
     * @param dashboardDto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ProAPIResponse createDashboard(
            @PathVariable Integer userId,
            @RequestBody(required = true) DashboardDto dashboardDto,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        dashboardDto.setUserId(userInfo.getUserIdentifier());
        Dashboard result = dashboardService.createDashboard(dashboardDto);
        return new ProAPISuccessResponse(result);
    }

    /**
     * This method updates a dashboard, but it will update only dashboard
     * properties and not the widget mapping part. To update widget's
     * positioning mapped with a dashboard use method
     * 
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
            @RequestBody(required = true) DashboardDto dashboardDto,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        dashboardDto.setId(dashboardId);
        dashboardDto.setUserId(userInfo.getUserIdentifier());
        Dashboard dashboard = dashboardService.updateDashboard(dashboardDto);
        return new ProAPISuccessResponse(dashboard);
    }

    /**
     * Get all widgets associated with dashboard id
     * 
     * @param userId
     * @param dashboardId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{dashboardId}/widget")
    @ResponseBody
    public ProAPIResponse getWidgetMappingWithDashboard(
            @PathVariable Integer userId,
            @PathVariable Integer dashboardId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        Dashboard dashboard = dashboardService.getDashboardById(userInfo.getUserIdentifier(), dashboardId);
        return new ProAPISuccessCountResponse(dashboard.getWidgets(), dashboard.getWidgets() == null ? 0 : dashboard
                .getWidgets().size());
    }

    /**
     * Get a widget association object for widget id and dashboard id
     * 
     * @param userId
     * @param dashboardId
     * @param widgetId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{dashboardId}/widget/{widgetId}")
    @ResponseBody
    public ProAPIResponse getSingleWidgetMappingWithDashboard(
            @PathVariable Integer userId,
            @PathVariable Integer dashboardId,
            @PathVariable Integer widgetId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        Dashboard dashboard = dashboardService.getDashboardById(userInfo.getUserIdentifier(), dashboardId);
        return new ProAPISuccessCountResponse(dashboard.getWidgets(), dashboard.getWidgets() == null ? 0 : dashboard
                .getWidgets().size());
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
            @RequestBody(required = true) DashboardWidgetMapping dashboardWidgetMapping,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        Dashboard dashboard = dashboardService.createSingleWidget(
                userInfo.getUserIdentifier(),
                dashboardId,
                dashboardWidgetMapping);
        return new ProAPISuccessResponse(dashboard);
    }

    /**
     * This method updates a provided widget id mapping with provided dashboard
     * id
     * 
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
            @RequestBody(required = true) DashboardWidgetMapping dashboardWidgetMapping,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        Dashboard dashboard = dashboardService.updateWidgetMappingWithDashboard(
                userInfo.getUserIdentifier(),
                dashboardId,
                widgetId,
                dashboardWidgetMapping);
        return new ProAPISuccessResponse(dashboard);
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
            @RequestBody(required = true) DashboardWidgetMapping dashboardWidgetMapping,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        Dashboard dashboard = dashboardService.deleteWidgetMappingWithDashboard(
                userInfo.getUserIdentifier(),
                dashboardId,
                widgetId,
                dashboardWidgetMapping);
        return new ProAPISuccessResponse(dashboard);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{dashboardId}")
    @ResponseBody
    public ProAPIResponse deleteDashboard(
            @PathVariable Integer userId,
            @PathVariable Integer dashboardId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        Dashboard deleted = dashboardService.deleteDashboard(userInfo.getUserIdentifier(), dashboardId);
        return new ProAPISuccessResponse(deleted);
    }
}
