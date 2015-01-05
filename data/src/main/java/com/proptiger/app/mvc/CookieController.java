package com.proptiger.app.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.data.service.CookiesService;

@Controller
@RequestMapping(value = "app/v1/cookies")
public class CookieController extends BaseController {
    
    @Autowired
    private CookiesService setCookiesService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Object setCookie(HttpServletRequest request, HttpServletResponse response) {

        setCookiesService.setCookies(request, response);
        return new APIResponse();
    }
}