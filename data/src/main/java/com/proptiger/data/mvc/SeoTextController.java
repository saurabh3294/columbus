/**
 * 
 */
package com.proptiger.data.mvc;

import java.util.Collections;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;

/**
 * @author mandeep
 *
 */
@Controller
@RequestMapping("data/v1/seo-text")
public class SeoTextController {
    private RestTemplate restTemplate = new RestTemplate();

    private String websiteHost = "http://proptiger.com/";

    @RequestMapping
    @ResponseBody
    public ProAPIResponse get(@RequestParam String url) {
        return new ProAPISuccessResponse(new Gson().fromJson(restTemplate.getForObject(websiteHost + "getSeoTags.php?url={URL}", String.class, Collections.singletonMap("URL", url)), Object.class));
    }
}
