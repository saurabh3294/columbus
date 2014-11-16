package com.proptiger.data.seo.processor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.enums.DomainObject;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.model.URLDetail;
import com.proptiger.data.model.seo.URLCategories;
import com.proptiger.data.service.URLCreaterService;
import com.proptiger.data.service.seo.SeoURLService;

@Service
public class SeoEventUrlCreator {
    @Autowired
    private URLCreaterService urlCreaterService;
    
    @Autowired
    private SeoURLService seoURLService;
    
    public int generateUrls(DomainObject domainObject, List<EventGenerated> events, List<URLCategories> urlCategories){
        
        switch(domainObject){
            case property:
                generatePropertyURLS(events, urlCategories);
                break;
            case project:
                generateProjectUrls(events, urlCategories);
                break;
            case builder:
                generateBuilderUrls(events, urlCategories);
                break;
            case locality:
                generateLocalityUrls(events, urlCategories);
                break;
            case suburb:
                generateSuburbUrls(events, urlCategories);
                break;
            case city:
                generateCityUrls(events, urlCategories);
                break;
            
        }
        
        return 0;
    }
    
    public int generatePropertyURLS(List<EventGenerated> events, List<URLCategories> urlCategories){
        URLDetail urlDetail = new URLDetail();
        String url = null;
        int totalUrls = 0;
        int objectId;
        for(EventGenerated eventGenerated:events){
            for(URLCategories urlCategory:urlCategories){
                objectId = Integer.parseInt(eventGenerated.getEventTypeUniqueKey());
                urlDetail.setUrlCategory(urlCategory);
                urlDetail.setPropertyId(objectId);
                url = urlCreaterService.urlLibPropertyUrl(urlDetail);
                seoURLService.saveUrls(url, objectId, urlCategory);
            }
        }
        
        return totalUrls;
    }
    
    public int generateProjectUrls(List<EventGenerated> events, List<URLCategories> urlCategories){
        URLDetail urlDetail = new URLDetail();
        String url = null;
        int totalUrls = 0;
        int objectId;

        for(EventGenerated eventGenerated:events){
            for(URLCategories urlCategory:urlCategories){
                objectId = Integer.parseInt(eventGenerated.getEventTypeUniqueKey());
                urlDetail.setUrlCategory(urlCategory);
                urlDetail.setProjectId(Integer.parseInt(eventGenerated.getEventTypeUniqueKey()));
                url = urlCreaterService.urlLibProjectUrl(urlDetail);
                seoURLService.saveUrls(url, objectId, urlCategory);

            }
        }
        
        return totalUrls;
    }
    
    public int generateLocalityUrls(List<EventGenerated> events, List<URLCategories> urlCategories){
        URLDetail urlDetail = new URLDetail();
        String url = null;
        int totalUrls = 0;
        int objectId;

        for(EventGenerated eventGenerated:events){
            for(URLCategories urlCategory:urlCategories){
                objectId = Integer.parseInt(eventGenerated.getEventTypeUniqueKey());
                urlDetail.setUrlCategory(urlCategory);
                urlDetail.setLocalityId(Integer.parseInt(eventGenerated.getEventTypeUniqueKey()));
                url = urlCreaterService.getLocalityUrl(urlDetail);
                seoURLService.saveUrls(url, objectId, urlCategory);

            }
        }
        
        return totalUrls;
    }
    
    public int generateSuburbUrls(List<EventGenerated> events, List<URLCategories> urlCategories){
        URLDetail urlDetail = new URLDetail();
        String url = null;
        int totalUrls = 0;
        int objectId;

        for(EventGenerated eventGenerated:events){
            for(URLCategories urlCategory:urlCategories){
                objectId = Integer.parseInt(eventGenerated.getEventTypeUniqueKey());
                urlDetail.setUrlCategory(urlCategory);
                urlDetail.setSuburbId(Integer.parseInt(eventGenerated.getEventTypeUniqueKey()));
                url = urlCreaterService.getSuburbUrl(urlDetail);
                seoURLService.saveUrls(url, objectId, urlCategory);

            }
        }
        
        return totalUrls;
    }
    
    public int generateCityUrls(List<EventGenerated> events, List<URLCategories> urlCategories){
        URLDetail urlDetail = new URLDetail();
        String url = null;
        int totalUrls = 0;
        int objectId;

        for(EventGenerated eventGenerated:events){
            for(URLCategories urlCategory:urlCategories){
                objectId = Integer.parseInt(eventGenerated.getEventTypeUniqueKey());
                urlDetail.setUrlCategory(urlCategory);
                urlDetail.setCityId(Integer.parseInt(eventGenerated.getEventTypeUniqueKey()));
                url = urlCreaterService.getCityUrl(urlDetail);
                seoURLService.saveUrls(url, objectId, urlCategory);

            }
        }
        
        return totalUrls;
    }
    
    public int generateBuilderUrls(List<EventGenerated> events, List<URLCategories> urlCategories){
        URLDetail urlDetail = new URLDetail();
        String url = null;
        int totalUrls = 0;
        int objectId;

        for(EventGenerated eventGenerated:events){
            for(URLCategories urlCategory:urlCategories){
                objectId = Integer.parseInt(eventGenerated.getEventTypeUniqueKey());
                urlDetail.setUrlCategory(urlCategory);
                urlDetail.setBuilderId(Integer.parseInt(eventGenerated.getEventTypeUniqueKey()));
                url = urlCreaterService.getBuilderUrl(urlDetail);
                seoURLService.saveUrls(url, objectId, urlCategory);

            }
        }
        
        return totalUrls;
    }
}
