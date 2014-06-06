package com.proptiger.data.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.proptiger.data.model.URLDetail;
import com.proptiger.data.util.PageType;

@Controller
public class UrlService {
     
    /*@Autowired
    private LocalityService localityService;
    
    @Autowired
    private ProjectService projectService;
    
    @Autowired 
    private PropertyService propertyService;
    
    @Autowired
    private SuburbService suburbService;
    
    @Autowired
    private BuilderService builderService;
    
    @Autowired
    private CityService cityService;
    
    
    public URLDetail parse(String URL) throws IllegalAccessException,
              InvocationTargetException {
           URLDetail urlDetail = new URLDetail();
           List<String> groups = new ArrayList<String>();
    
           for (PageType pageType : PageType.values()) {
               Pattern pattern = Pattern.compile(pageType.getRegex());
               Matcher matcher = pattern.matcher(URL);
               if (matcher.matches()) {
                   int c = matcher.groupCount();
                   for (int j = 0; j < c; j++) {
                       groups.add(matcher.group(j + 1));
                   }
    
                   urlDetail.setPageType(pageType);
                   int i = 0;
    
                   for (String field : pageType.getURLDetailFields()) {
                       BeanUtils.copyProperty(urlDetail, field, groups.get(i++));
                   }
                   break;
               }
           }
    
           return urlDetail;
       }
    }*/
    
    

}
