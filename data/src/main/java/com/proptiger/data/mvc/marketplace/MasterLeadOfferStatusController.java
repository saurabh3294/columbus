package com.proptiger.data.mvc.marketplace;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.core.pojo.response.PaginatedResponse;
import com.proptiger.data.model.MasterLeadOfferStatus;
import com.proptiger.data.service.marketplace.MasterLeadOfferStatusService;

@Controller
public class MasterLeadOfferStatusController extends BaseController {

    @Autowired
    private MasterLeadOfferStatusService masterLeadOfferStatusService;

    @RequestMapping(value = "data/v1/entity/lead-offer-statuses")
    @ResponseBody
    public APIResponse get(@ModelAttribute FIQLSelector selector) {
        PaginatedResponse<List<MasterLeadOfferStatus>> paginatedStatuses = masterLeadOfferStatusService.get(selector);
        return new APIResponse(
                super.filterFieldsFromSelector(paginatedStatuses.getResults(), selector),
                paginatedStatuses.getTotalCount());
    }
}
