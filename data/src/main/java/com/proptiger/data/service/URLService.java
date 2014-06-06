/**
 * 
 */
package com.proptiger.data.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.ws.soap.AddressingFeature.Responses;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.proptiger.data.enums.DomainObject;
import com.proptiger.data.model.Builder;
import com.proptiger.data.model.City;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.Suburb;
import com.proptiger.data.model.URLDetail;
import com.proptiger.data.util.PageType;
import com.proptiger.exception.ProAPIException;

/**
 * 
 *
 */
@Service
public class URLService {
	@Autowired
	private CityService cityService;
	
	@Autowired
	private LocalityService localityService;
	
	@Autowired
	private SuburbService suburbService;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private PropertyService propertyService;
	
	@Autowired
	private BuilderService builderService;
	
	
	public ValidURLResponse getURLStatus(String url){
	    URLDetail urlDetail = null;
	    try{
	        urlDetail = parse(url);
	    }
	    catch(Exception e){
	        throw new ProAPIException(e);
	    }
	    PageType pageType = urlDetail.getPageType();
	    int responseStatus = HttpStatus.SC_OK;
	    String redirectUrl = null, domainUrl = null;
	    switch(pageType){
	        case PROPERTY_URLS :
	            Property property = propertyService.getProperty(urlDetail.getPropertyId());
	            if( property == null ){
	                responseStatus = HttpStatus.SC_NOT_FOUND;
	            }
	            else if( property.getURL() != urlDetail.getUrl() ){
	                responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
	                redirectUrl = property.getURL();
	            }
	            break;
	        case PROJECT_URLS:
	            Project project = projectService.getProjectData(urlDetail.getProjectId());
	            if( project == null ){
	                responseStatus = HttpStatus.SC_NOT_FOUND;
	            }
	            else if( project.getURL() != urlDetail.getUrl() ){
	                responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
	                redirectUrl = project.getURL();
	            }
	            break;
	        case BUILDER_URLS:
	            Builder builder = builderService.getBuilderById(urlDetail.getBuilderId());
	            if(builder == null){
	                responseStatus = HttpStatus.SC_NOT_FOUND;
	            }
	            else{
	                domainUrl = urlDetail.getCityName() + builder.getUrl() + urlDetail.getBedroomString();
	                if( !domainUrl.equals(urlDetail.getUrl()) ){
	                    responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
	                    redirectUrl = domainUrl;
	                }
	            }
	            break;
	        case LOCALITY_SUBURB_LISTING:
	            // localitySuburbListingUrl, cityName, response status
	            Object[] localitySuburbData = getLocalitySuburbListingUrl(urlDetail);
	            domainUrl = (String)localitySuburbData[0];
	            responseStatus = (Integer)localitySuburbData[2];
	            if(domainUrl == null){
	                break;
	            }
	            domainUrl = domainUrl.replaceFirst("property-sale", urlDetail.getPropertyType() ) + urlDetail.getBedroomString() + urlDetail.getPriceString(); 
	            if( !domainUrl.equals(urlDetail.getUrl()) ){
	                responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
	                redirectUrl = domainUrl;
	            }
	            break;
	        case LOCALITY_SUBURB_OVERVIEW:
	            // localitySuburbListingUrl, cityName, response status
	            Object[] localitySuburbUrlData = getLocalitySuburbListingUrl(urlDetail);
	            domainUrl = (String)localitySuburbUrlData[0];
	            responseStatus = (Integer)localitySuburbUrlData[2];
	            String cityName = (String)localitySuburbUrlData[1];
	            if(domainUrl == null){
                    break;
                }
	            domainUrl = domainUrl.replaceFirst( cityName, cityName+"-real-estate") + "/overview"; 
                if( !domainUrl.equals(urlDetail.getUrl()) ){
                    responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
                    redirectUrl = domainUrl;
                }
                break;
	        case CITY_URLS:
	           if( cityService.getCityByName( urlDetail.getCityName() ) == null ){
	               responseStatus = HttpStatus.SC_NOT_FOUND;
	           }
	           break;
	        default:
	            responseStatus = HttpStatus.SC_NOT_FOUND;
	            break;
	    }
	    
	    return new ValidURLResponse(responseStatus, redirectUrl) ;
	}
	
	private Object[] getLocalitySuburbListingUrl(URLDetail urlDetail){
	    DomainObject domainObject = DomainObject.getDomainInstance(urlDetail.getLocalityId().longValue());
        String newUrl = null, cityName = null;
        int responseStatus = HttpStatus.SC_OK;
        switch(domainObject){
            case locality:
                Locality locality = localityService.getLocality(urlDetail.getLocalityId());
                if(locality == null){
                    responseStatus = HttpStatus.SC_NOT_FOUND;
                }
                else{
                    newUrl = locality.getUrl();
                    cityName = locality.getLabel();
                }
                break;
            case suburb:
                Suburb suburb = suburbService.getSuburbById(urlDetail.getLocalityId());
                if(suburb == null){
                    responseStatus = HttpStatus.SC_NOT_FOUND;
                }
                else{
                    newUrl = suburb.getUrl();
                    cityName = suburb.getLabel();
                }
                break;
            default:
                responseStatus = HttpStatus.SC_NOT_FOUND;
        }
        
        return new Object[] {newUrl, cityName, responseStatus};
	}
	
    /* This function(parse) parses the url to determine the pageType and sets the required fields in urlDetail */
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
		
		urlDetail.setUrl(URL);
		return urlDetail;
	}
	
	public static class ValidURLResponse {
	    private int httpStatus;
	    private String redirectUrl;
	    
	    public ValidURLResponse(int httpStatus, String redirectUrl){
	        this.httpStatus = httpStatus;
	        this.redirectUrl = redirectUrl;
	    }

        public int getHttpStatus() {
            return httpStatus;
        }

        public String getRedirectUrl() {
            return redirectUrl;
        }
	}
}
