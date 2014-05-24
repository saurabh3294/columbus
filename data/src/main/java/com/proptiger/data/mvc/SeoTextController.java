/**
 * 
 */
package com.proptiger.data.mvc;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.URLDetail;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.SeoPageService;
import com.proptiger.data.service.URLService;

/**
 * @author mandeep
 * 
 */
@Controller
@RequestMapping()
public class SeoTextController {
    private RestTemplate   restTemplate = new RestTemplate();

    @Value("${proptiger.url}")
    private String         websiteHost;

    @Autowired
    private SeoPageService seoPageService;

    @Autowired
    private URLService     urlService;


    @RequestMapping("data/v1/seo-text")
    @ResponseBody
    public APIResponse getSeo(@ModelAttribute URLDetail urlDetail, @RequestParam String templateId) throws FileNotFoundException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
    	
        return new APIResponse(seoPageService.getSeoMetaContentForPage(urlDetail, templateId));
        //return new APIResponse(seoPageService.getSeoContentForPage());

    }
}
