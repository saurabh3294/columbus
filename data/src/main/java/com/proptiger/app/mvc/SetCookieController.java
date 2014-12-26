package com.proptiger.app.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.data.util.lead.SetCookiesService;

@Controller
@RequestMapping(value = "app/v1/set-cookie")
public class SetCookieController extends BaseController {
    
    @Autowired
    SetCookiesService setCookiesService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public void setCookie(HttpServletRequest request, HttpServletResponse response) {

        setCookiesService.setCookies(request, response);
    }
}