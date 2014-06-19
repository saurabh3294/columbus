package com.proptiger.data.mvc;

import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.Builder;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.repo.BuilderDao;
import com.proptiger.data.service.BuilderService;

/**
 * 
 * @author Rajeev Pandey
 * 
 */
@Controller
public class BuilderController extends BaseController {

    @Autowired
    private BuilderService builderService;

    @Autowired
    private BuilderDao     builderDao;

    /**
     * Returns popular builders as per any selector
     * 
     * @param selector
     * @return
     */
    @RequestMapping(value = "data/v1/entity/builder/top", method = RequestMethod.GET)
    @ResponseBody
    public APIResponse getTopBuilders(@RequestParam(required = false) String selector) {
        Selector builderSelector = new Selector();
        if (selector != null) {
            builderSelector = super.parseJsonToObject(selector, Selector.class);
        }
        PaginatedResponse<List<Builder>> paginatedResponse = builderService.getTopBuilders(builderSelector);

        return new APIResponse(
                super.filterFields(paginatedResponse.getResults(), builderSelector.getFields()),
                paginatedResponse.getTotalCount());
    }

    @RequestMapping(value = { "data/v2/entity/builder/{builderId}" })
    @ResponseBody
    public APIResponse getBuilderDetails(
            @PathVariable Integer builderId,
            @RequestParam(required = false) String selector) {
        Selector builderSelector = new Selector();
        if (selector != null) {
            builderSelector = super.parseJsonToObject(selector, Selector.class);
        }
        Builder builder = builderService.getBuilderDetails(builderId, builderSelector);

        Set<String> fields = builderSelector.getFields();
        return new APIResponse(super.filterFields(builder, fields));
    }
}
