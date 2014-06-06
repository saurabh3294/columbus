/**
 * 
 */
package com.proptiger.data.mvc;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.ProjectError;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.user.portfolio.PortfolioListing;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.service.ErrorReportingService;
import com.proptiger.data.service.ImageService;
import com.proptiger.data.service.PropertyService;
import com.proptiger.data.service.user.portfolio.PortfolioService;
import com.proptiger.data.util.Constants;

/**
 * @author mandeep
 * 
 */
@Controller
@RequestMapping
public class PropertyController extends BaseController {
    @Autowired
    private PropertyService       propertyService;

    @Autowired
    private PortfolioService      portfolioService;

    @Autowired
    private ImageService          imageService;

    @Autowired
    private ErrorReportingService errorReportingService;

    private static Logger         logger = LoggerFactory.getLogger(PropertyController.class);

    @RequestMapping(value = "data/v1/entity/property")
    public @ResponseBody
    APIResponse getProperties(@RequestParam(required = false, value = "selector") String selector) throws Exception {

        Selector propRequestParam = super.parseJsonToObject(selector, Selector.class);
        if (propRequestParam == null) {
            propRequestParam = new Selector();
        }
        List<Property> properties = propertyService.getProperties(propRequestParam);
        Set<String> fieldsSet = propRequestParam.getFields();

        return new APIResponse(super.filterFields(properties, fieldsSet));
    }

    @RequestMapping(value = "data/v2/entity/property")
    public @ResponseBody
    APIResponse getV2Properties(@ModelAttribute FIQLSelector selector) throws Exception {
        PaginatedResponse<List<Property>> response = propertyService.getProperties(selector);
        return new APIResponse(response.getResults(), response.getTotalCount());
    }

    @RequestMapping(method = RequestMethod.POST, value = "data/v1/entity/property/{propertyId}/report-error")
    @ResponseBody
    @DisableCaching
    public APIResponse reportPropertyError(
            @Valid @RequestBody ProjectError projectError,
            @PathVariable int propertyId) {
        if (projectError.getPropertyId() != null && projectError.getPropertyId() > 0)
            throw new IllegalArgumentException("Property Id should not be present in the request body");
        if (projectError.getProjectId() != null)
            throw new IllegalArgumentException(
                    "Project Id should not be present in the request body as it is for project error.");

        projectError.setPropertyId(propertyId);
        return new APIResponse(errorReportingService.saveReportError(projectError));
    }

    @RequestMapping(method = RequestMethod.POST, value = "data/v1/entity/property/sell-property")
    @ResponseBody
    @DisableCaching
    public APIResponse sellYourProperty(@RequestBody PortfolioListing portfolioListing) {
        return new APIResponse(portfolioService.sellYourProperty(portfolioListing));
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "data/v1/entity/user/property/sell-property")
    @ResponseBody
    @DisableCaching
    public APIResponse userSellYourProperty(@RequestBody PortfolioListing portfolioListing, @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        portfolioListing.setUserId(userInfo.getUserIdentifier());
        return new APIResponse(portfolioService.sellYourProperty(portfolioListing));
    }
}
