package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.Builder;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessCountResponse;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.BuilderService;
import com.proptiger.data.service.pojo.PaginatedResponse;

/**
 * 
 * @author Rajeev Pandey
 * 
 */
@RequestMapping("data/v1/entity/builder")
@Controller
public class BuilderController extends BaseController {

    @Autowired
    private BuilderService builderService;

    /**
     * Returns popular builders as per any selector
     * 
     * @param selector
     * @return
     */
    @RequestMapping(value = "/top", method = RequestMethod.GET)
    @ResponseBody
    public ProAPIResponse getTopBuilders(@RequestParam(required = false) String selector) {
        Selector builderSelector = new Selector();
        if (selector != null) {
            builderSelector = super.parseJsonToObject(selector, Selector.class);
        }
        PaginatedResponse<List<Builder>> paginatedResponse = builderService.getTopBuilders(builderSelector);

        return new ProAPISuccessCountResponse(
                super.filterFields(paginatedResponse.getResults(), builderSelector.getFields()),
                paginatedResponse.getTotalCount());
    }
}
