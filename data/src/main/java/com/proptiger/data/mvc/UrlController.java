package com.proptiger.data.mvc;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.gson.Gson;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.URLService;
import com.proptiger.data.service.URLService.ValidURLResponse;

@Controller
@RequestMapping
public class UrlController extends BaseController {
    @Autowired
    private URLService urlService;

    @RequestMapping("data/v1/url")
    @ResponseBody
    public APIResponse getProjects(@RequestParam String url, HttpServletResponse response) throws IOException {

        ValidURLResponse validURLResponse = urlService.getURLStatus(url);
        System.out.println(new Gson().toJson(validURLResponse));
        response.setStatus(validURLResponse.getHttpStatus());
        if (validURLResponse.getRedirectUrl() != null) {
            response.sendRedirect("/" + validURLResponse.getRedirectUrl());
        }
        return new APIResponse();
    }

}
