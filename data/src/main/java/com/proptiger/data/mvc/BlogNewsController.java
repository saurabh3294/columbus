package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.WordpressPost;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.BlogNewsService;

/**
 * @author Rajeev Pandey
 * 
 */
@Controller
public class BlogNewsController extends BaseController {

    @Autowired
    private BlogNewsService blogNewsService;

    @RequestMapping(value = "data/v1/entity/blog-news", method = RequestMethod.GET)
    @ResponseBody
    @Deprecated
    public APIResponse getBlogForCity(
            @RequestParam(required = true, value = "cityName") String cityName,
            @RequestParam(required = false, defaultValue = "200", value = "contentLimit") int contentLimit,
            @RequestParam(required = false, value = "selector") String selector) {

        Selector blogSelector = super.parseJsonToObject(selector, Selector.class);
        if (blogSelector == null) {
            blogSelector = new Selector();
        }
        List<WordpressPost> newsList = blogNewsService.getBlogPostsByCity(cityName, contentLimit, blogSelector);
        return new APIResponse(newsList);
    }
    
    @RequestMapping(value = "data/v1/entity/city/{cityId}/blog", method = RequestMethod.GET)
    @ResponseBody
    public APIResponse getBlogForCity(
            @PathVariable Integer cityId,
            @RequestParam(required = false, defaultValue = "200", value = "contentLimit") int contentLimit,
            @RequestParam(required = false, value = "selector") String selector) {

        Selector blogSelector = super.parseJsonToObject(selector, Selector.class);
        if (blogSelector == null) {
            blogSelector = new Selector();
        }
        List<WordpressPost> newsList = blogNewsService.getBlogPostsByCityId(cityId, contentLimit, blogSelector);
        return new APIResponse(newsList);
    }
    
    @RequestMapping(value = "data/v1/entity/city/{cityId}/news", method = RequestMethod.GET)
    @ResponseBody
    public APIResponse getNewsForCity(
            @PathVariable Integer cityId,
            @RequestParam(required = false, defaultValue = "200", value = "contentLimit") int contentLimit,
            @RequestParam(required = false, value = "selector") String selector) {

        Selector blogSelector = super.parseJsonToObject(selector, Selector.class);
        if (blogSelector == null) {
            blogSelector = new Selector();
        }
        List<WordpressPost> newsList = blogNewsService.getNewsByCity(cityId, contentLimit, blogSelector);
        return new APIResponse(newsList);
    }
}
