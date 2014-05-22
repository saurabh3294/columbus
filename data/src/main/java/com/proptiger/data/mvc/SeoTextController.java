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

    /*@RequestMapping("data/v1/seo-text")
    @ResponseBody
    public APIResponse get(@RequestParam String url) {
        return new APIResponse(new Gson().fromJson(
                restTemplate.getForObject(
                        websiteHost + "getSeoTags.php?url={URL}",
                        String.class,
                        Collections.singletonMap("URL", url)),
                Object.class));
    }*/

    @RequestMapping("data/v1/seo-text")
    @ResponseBody
    @DisableCaching
    // to be removed.
    public APIResponse getSeo(@RequestParam String url) throws FileNotFoundException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        return new APIResponse(seoPageService.getSeoContentForPage(url));

    }
}
