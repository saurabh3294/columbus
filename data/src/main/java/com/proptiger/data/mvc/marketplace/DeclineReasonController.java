package com.proptiger.data.mvc.marketplace;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.data.model.marketplace.DeclineReason;
import com.proptiger.data.service.marketplace.DeclineReasonService;

@Controller
public class DeclineReasonController  extends BaseController{

    @Autowired
    DeclineReasonService declineReasonService;
    
    @RequestMapping(value = "data/v1/entity/decline-reason")
    @ResponseBody
    public APIResponse get() {        
        List<DeclineReason> declineReasons =  declineReasonService.getAllReasons();
        return new APIResponse(declineReasons);
    }
}
