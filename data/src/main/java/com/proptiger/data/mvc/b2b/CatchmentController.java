package com.proptiger.data.mvc.b2b;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.service.b2b.CatchmentService;

@Controller
@RequestMapping
public class CatchmentController extends BaseController {
    @Autowired
    private CatchmentService catchmentService;
    
    @RequestMapping(value="/data/v1/entity/catchment", method = RequestMethod.GET)
    public @ResponseBody
    ProAPIResponse getcatchment(){
        return (ProAPIResponse) catchmentService.test();
    }
}
