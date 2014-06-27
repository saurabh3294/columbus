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

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.proptiger.data.enums.DomainObject;
import com.proptiger.data.init.NullAwareBeanUtilsBean;
import com.proptiger.data.model.Builder;
import com.proptiger.data.model.City;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.RedirectUrlMap;
import com.proptiger.data.model.Suburb;
import com.proptiger.data.model.URLDetail;
import com.proptiger.data.model.user.portfolio.PortfolioListing;
import com.proptiger.data.repo.RedirectUrlMapDao;
import com.proptiger.data.service.user.portfolio.PortfolioService;
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
    private String EMPTY_URL = "";
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

    @Autowired
    private PortfolioService  portfolioService;

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
            case HOME_PAGE:
                responseStatus = HttpStatus.SC_OK;
                break;
            case PROPERTY_URLS:
                Property property = null;
                try {
                    property = propertyService.getProperty(urlDetail.getPropertyId());
                }
                catch (ResourceNotAvailableException e) {
                    property = null;
                }

                if (property == null) {
                    redirectUrl = getHigherHierarchyUrl(urlDetail.getPropertyId(), DomainObject.property.getText());
                    responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
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
                    redirectUrl = getHigherHierarchyUrl(urlDetail.getProjectId(), DomainObject.project.getText()); 
                    responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
                }
                else if (!project.getURL().equals(urlDetail.getUrl())) {
                    responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
                    redirectUrl = project.getURL();
                }
                break;
            case BUILDER_URLS:
            case BUILDER_URLS_SEO:
                Builder builder = null;
                City builderCity = new City();
                try {
                    builder = builderService.getBuilderById(urlDetail.getBuilderId());
                }
                catch (ResourceNotAvailableException e) {
                    builder = null;
                }
                try {
                    if (urlDetail.getCityName() != null && !urlDetail.getCityName().isEmpty()) {
                        builderCity = cityService.getCityByName(urlDetail.getCityName().replace("/", ""));
                    }
                }
                catch (ResourceNotAvailableException e) {
                    builderCity = null;
                }
                
                if (builder == null || builderCity == null) {
                    if (builder == null && builderCity != null) {
                        if (urlDetail.getCityName() != null && !urlDetail.getCityName().isEmpty()) {
                            redirectUrl = builderCity.getUrl();
                            responseStatus = HttpStatus.SC_MOVED_PERMANENTLY; 
                        }
                        else {
                            redirectUrl = EMPTY_URL;
                            responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
                        }
                    }
                    else if (builder != null && builderCity == null) {
                        redirectUrl = builder.getUrl();
                        responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
                    }
                    else {
                        redirectUrl = builder.getUrl();
                        responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
                    }
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
                boolean is404FallbackSet = (boolean) localitySuburbData[4];
                
                if (is404FallbackSet) {
                    redirectUrl = domainUrl;
                    if (!domainUrl.isEmpty()) {
                        domainUrl = domainUrl.replaceFirst("-real-estate", "");
                        redirectUrl = domainUrl + "/" + urlDetail.getPropertyType();
                    }
                }
                else {
                    domainUrl = domainUrl.replaceFirst("property-sale", urlDetail.getPropertyType()) + urlDetail
                            .getBedroomString() + urlDetail.getPriceString();
                    
                    if (!domainUrl.equals(urlDetail.getUrl())) {
                        responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
                        redirectUrl = domainUrl;
                    }
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
            case PORTFOLIO_URLS:
                if (urlDetail.getPortfolioId() != null) {
                    PortfolioListing portfolioListing = portfolioService.getActivePortfolioOnId(urlDetail
                            .getPortfolioId());
                    if (portfolioListing == null) {
                        responseStatus = HttpStatus.SC_NOT_FOUND;
                    }
                }
                break;
            default:
                responseStatus = HttpStatus.SC_NOT_FOUND;
                break;
        }

        return new ValidURLResponse(responseStatus, redirectUrl);
    }

    private String getHigherHierarchyUrl(Integer id, String domainType) {
        Integer projectId = null;
        Integer localityId = null;
        Integer suburbId = null;
        Integer cityId = null;
        if (domainType.equals(DomainObject.property.getText())) {
            projectId = projectService.getProjectIdForPropertyId(id);
            if (projectId == null) {
                return EMPTY_URL;
            }
            domainType = DomainObject.project.getText();
            id = projectId;
        }
        if (domainType.equals(DomainObject.project.getText())) {
            Project project = null;
            if (projectId != null) {
                try {
                    project = projectService.getProjectData(projectId);
                }
                catch (ResourceNotAvailableException e) {
                    project = null;
                }
                if (project != null) {
                    return project.getURL();
                }
            }
            project = projectService.getActiveOrInactiveProjectById(id);
            if (project == null) {
                return EMPTY_URL;
            }
            localityId = project.getLocalityId();
            suburbId = project.getLocality().getSuburbId();
            cityId = project.getLocality().getSuburb().getCityId();
            id = localityId;
            domainType = DomainObject.locality.getText();
        }
        if (domainType.equals(DomainObject.locality.getText())) {
            Locality locality = null;
            if (localityId != null) {
                try {
                    locality = localityService.getLocality(localityId);
                }
                catch (ResourceNotAvailableException e) {
                    locality = null;
                }
                if (locality != null) {
                    return locality.getUrl();
                }
            }
            else {
                locality = localityService.getActiveOrInactiveLocalityById(id);
                if (locality == null) {
                      return EMPTY_URL;
                }
            }
            if (locality != null) {
                suburbId = locality.getSuburbId();
                cityId = locality.getSuburb().getCityId();
            }
            id = suburbId;
            domainType = DomainObject.city.getText();
        }
        if (domainType.equals(DomainObject.suburb.getText())) {
            Suburb suburb = null;
            if (suburbId != null) {
                try {
                    suburb = suburbService.getSuburb(suburbId);
                }
                catch (ResourceNotAvailableException e) {
                    suburb = null;
                }
                if (suburb != null) {
                    return suburb.getUrl();
                }
            }
            else {
                suburb = suburbService.getActiveOrInactiveSuburbById(id);
                if (suburb == null) {
                    return EMPTY_URL;
                }
            }
            if (suburb != null) {
                cityId = suburb.getCityId();
            }
            id = cityId;
            domainType = DomainObject.city.getText();
        }
        if (domainType.equals(DomainObject.city.getText())) {
            City city = null;
            if (cityId != null) {
                try {
                    city = cityService.getCity(cityId);
                }
                catch (ResourceNotAvailableException e) {
                    city = null;
                }
                if (city != null) {
                    return city.getUrl();
                }
            }
        }
        return EMPTY_URL;
    }

    private Object[] getLocalitySuburbListingUrl(URLDetail urlDetail) {
        DomainObject domainObject = DomainObject.getDomainInstance(urlDetail.getLocalityId().longValue());
        String newUrl = "", cityName = "", domainName = "";
        int responseStatus = HttpStatus.SC_OK;
        boolean is404FallbackSet = false;
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
                    newUrl = getHigherHierarchyUrl(urlDetail.getLocalityId(), DomainObject.locality.getText()); 
                    responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
                    is404FallbackSet = true;
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
                    newUrl = getHigherHierarchyUrl(urlDetail.getLocalityId(), DomainObject.suburb.getText()); 
                    responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
                    is404FallbackSet = true;
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

        return new Object[] { newUrl, cityName.toLowerCase(), responseStatus, domainName.toLowerCase(), is404FallbackSet };
    }

    /*
     * This function(parse) parses the url to determine the pageType and sets
     * the required fields in urlDetail
     */
    public URLDetail parse(String URL) throws IllegalAccessException, InvocationTargetException {
        URLDetail urlDetail = new URLDetail();
        List<String> groups = new ArrayList<String>();
        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();

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
                    beanUtilsBean.copyProperty(urlDetail, field, groups.get(i++));
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
