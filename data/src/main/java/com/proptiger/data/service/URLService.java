/**
 * 
 */
package com.proptiger.data.service;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.proptiger.data.enums.DomainObject;
import com.proptiger.data.model.Builder;
import com.proptiger.data.model.City;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.RedirectUrlMap;
import com.proptiger.data.model.Suburb;
import com.proptiger.data.model.URLDetail;
import com.proptiger.data.repo.RedirectUrlMapDao;
import com.proptiger.data.util.Constants;
import com.proptiger.data.util.PageType;
import com.proptiger.exception.ProAPIException;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 * 
 *
 */
@Service
public class URLService {
    @Autowired
    private CityService       cityService;

    @Autowired
    private LocalityService   localityService;

    @Autowired
    private SuburbService     suburbService;

    @Autowired
    private ProjectService    projectService;

    @Autowired
    private PropertyService   propertyService;

    @Autowired
    private BuilderService    builderService;

    @Autowired
    private RedirectUrlMapDao redirectUrlMapDao;

    public ValidURLResponse getURLStatus(String url) {
        URLDetail urlDetail = null;
        try {
            urlDetail = parse(url);
        }
        catch (Exception e) {
            throw new ProAPIException(e);
        }
        PageType pageType = urlDetail.getPageType();
        int responseStatus = HttpStatus.SC_OK;
        String redirectUrl = null, domainUrl = null;

        if (urlDetail.getBedroomString() == null) {
            urlDetail.setBedroomString("");
        }
        if (urlDetail.getPriceString() == null) {
            urlDetail.setPriceString("");
        }
        if (urlDetail.getCityName() == null) {
            urlDetail.setCityName("");
        }
        if (urlDetail.getPropertyType() == null) {
            urlDetail.setPropertyType("");
        }

        switch (pageType) {
            case PROPERTY_URLS:
                Property property = null;
                try {
                    property = propertyService.getProperty(urlDetail.getPropertyId());
                }
                catch (ResourceNotAvailableException e) {
                    property = null;
                }

                if (property == null) {
                    responseStatus = HttpStatus.SC_NOT_FOUND;
                }
                else if (!property.getURL().equals(urlDetail.getUrl())) {
                    responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
                    redirectUrl = property.getURL();
                }
                break;
            case PROJECT_URLS:
                Project project = null;
                try {
                    project = projectService.getProjectData(urlDetail.getProjectId());
                }
                catch (ResourceNotAvailableException e) {
                    project = null;
                }

                if (project == null) {
                    responseStatus = HttpStatus.SC_NOT_FOUND;
                }
                else if (!project.getURL().equals(urlDetail.getUrl())) {
                    responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
                    redirectUrl = project.getURL();
                }
                break;
            case BUILDER_URLS:
            case BUILDER_URLS_SEO:
                Builder builder = null;
                try {
                    builder = builderService.getBuilderById(urlDetail.getBuilderId());
                }
                catch (ResourceNotAvailableException e) {
                    builder = null;
                }

                if (builder == null) {
                    responseStatus = HttpStatus.SC_NOT_FOUND;
                }
                else {
                    domainUrl = urlDetail.getCityName() + urlDetail.getPropertyType()
                            + builder.getUrl()
                            + urlDetail.getBedroomString();
                    if (!domainUrl.equals(urlDetail.getUrl())) {
                        responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
                        redirectUrl = domainUrl;
                    }
                }
                break;
            case LOCALITY_SUBURB_LISTING:
            case LOCALITY_SUBURB_LISTING_SEO:
                // localitySuburbListingUrl, cityName, response status
                Object[] localitySuburbData = getLocalitySuburbListingUrl(urlDetail);

                domainUrl = (String) localitySuburbData[0];
                responseStatus = (Integer) localitySuburbData[2];
                if (domainUrl.length() < 1) {
                    break;
                }

                domainUrl = domainUrl.replaceFirst("property-sale", urlDetail.getPropertyType()) + urlDetail
                        .getBedroomString() + urlDetail.getPriceString();
                if (!domainUrl.equals(urlDetail.getUrl())) {
                    responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
                    redirectUrl = domainUrl;
                }
                break;
            case LOCALITY_SUBURB_OVERVIEW:
                // localitySuburbListingUrl, cityName, response status
                Object[] localitySuburbUrlData = getLocalitySuburbListingUrl(urlDetail);

                domainUrl = (String) localitySuburbUrlData[0];
                responseStatus = (Integer) localitySuburbUrlData[2];
                String cityName = (String) localitySuburbUrlData[1];
                if (domainUrl.length() < 1) {
                    break;
                }

                domainUrl = domainUrl.replaceFirst("property-sale-", "");
                domainUrl = domainUrl.replaceFirst(cityName, cityName + "-real-estate") + "/overview";

                if (!domainUrl.equals(urlDetail.getUrl())) {
                    responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
                    redirectUrl = domainUrl;
                }
                break;
            case CITY_URLS:
                City city = null;
                try {
                    city = cityService.getCityByName(urlDetail.getCityName());
                }
                catch (ResourceNotAvailableException e) {
                    city = null;
                }
                if (city == null) {
                    responseStatus = HttpStatus.SC_NOT_FOUND;
                }
                break;
            case STATIC_URLS:
                responseStatus = HttpStatus.SC_OK;
                break;
            default:
                responseStatus = HttpStatus.SC_NOT_FOUND;
                break;
        }

        return new ValidURLResponse(responseStatus, redirectUrl);
    }

    private Object[] getLocalitySuburbListingUrl(URLDetail urlDetail) {
        DomainObject domainObject = DomainObject.getDomainInstance(urlDetail.getLocalityId().longValue());
        String newUrl = "", cityName = "", domainName = "";
        int responseStatus = HttpStatus.SC_OK;
        switch (domainObject) {
            case locality:
                Locality locality = null;
                try {
                    locality = localityService.getLocality(urlDetail.getLocalityId());
                }
                catch (ResourceNotAvailableException e) {
                    locality = null;
                }

                if (locality == null) {
                    responseStatus = HttpStatus.SC_NOT_FOUND;
                }
                else {
                    newUrl = locality.getUrl();
                    cityName = locality.getSuburb().getCity().getLabel();
                    domainName = locality.getLabel();
                }
                break;
            case suburb:
                Suburb suburb = null;
                try {
                    suburb = suburbService.getSuburbById(urlDetail.getLocalityId());
                }
                catch (ResourceNotAvailableException e) {
                    suburb = null;
                }

                if (suburb == null) {
                    responseStatus = HttpStatus.SC_NOT_FOUND;
                }
                else {
                    newUrl = suburb.getUrl();
                    cityName = suburb.getCity().getLabel();
                    domainName = suburb.getLabel();
                }
                break;
            default:
                responseStatus = HttpStatus.SC_NOT_FOUND;
        }

        return new Object[] { newUrl, cityName.toLowerCase(), responseStatus, domainName.toLowerCase() };
    }

    /*
     * This function(parse) parses the url to determine the pageType and sets
     * the required fields in urlDetail
     */
    public URLDetail parse(String URL) throws IllegalAccessException, InvocationTargetException {
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
    
    @Deprecated
    @Cacheable(value = Constants.CacheName.REDIRECT_URL_MAP)
    public RedirectUrlMap getRedirectUrlForOldUrl(String fromUrl) {
        return redirectUrlMapDao.findOne(fromUrl);
    }

    public static class ValidURLResponse implements Serializable {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        private int               httpStatus;
        private String            redirectUrl;

        public ValidURLResponse(int httpStatus, String redirectUrl) {
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
