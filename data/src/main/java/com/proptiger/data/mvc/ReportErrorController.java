package com.proptiger.data.mvc;

import java.security.InvalidParameterException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.ProjectError;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.ReportErrorService;

@Controller
@RequestMapping(value = "data/v1/report")
public class ReportErrorController extends BaseController {

    @Autowired
    private ReportErrorService reportErrorService;

    @RequestMapping(method = RequestMethod.POST, value = "/error")
    @ResponseBody
    public ProAPIResponse reportError(@Valid @RequestBody ProjectError projectError) {
        return new ProAPISuccessResponse(reportErrorService.saveReportError(projectError));
    }

}
