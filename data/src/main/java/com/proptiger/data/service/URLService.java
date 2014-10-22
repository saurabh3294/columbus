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
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.proptiger.core.enums.DomainObject;
import com.proptiger.core.model.cms.Builder;
import com.proptiger.core.model.cms.City;
import com.proptiger.core.model.cms.Locality;
import com.proptiger.core.model.cms.Project;
import com.proptiger.core.model.cms.Property;
import com.proptiger.core.model.cms.Suburb;
import com.proptiger.data.init.NullAwareBeanUtilsBean;
import com.proptiger.data.model.RedirectUrlMap;
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
    private String            EMPTY_URL = "";
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
        
        //Removing trailing slace if any and maitaining a boolean flag
        // to update the HttpStatus for 301.
        boolean hasTrailingSlace = false;
        if (url.endsWith("/")) {
            url = url.substring(0, url.length()-1);
            hasTrailingSlace = true;
        }
        
        /*
         * Removing the URL request params and saving them in variable.
         */
        String[] URLSplit = url.split("\\?");

        String filterString = "/filters";
        String URLRequestParamString = "";
        if (URLSplit.length > 1) {
            if (StringUtils.endsWith(URLSplit[0], filterString)) {
                URLSplit[0] = StringUtils.replace(URLSplit[0], filterString, "");
                URLRequestParamString = filterString + "?" + URLSplit[1];
            }
            else {
                URLRequestParamString = "?" + URLSplit[1];
            }
        }
        url = URLSplit[0];

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
                responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
                Property property = null;
                try {
                    property = propertyService.getProperty(urlDetail.getPropertyId());
                }
                catch (ResourceNotAvailableException e) {
                    property = null;
                }

                if (property == null) {
                    redirectUrl = getHigherHierarchyUrl(urlDetail.getPropertyId(), DomainObject.property.getText());
                }
                else if (!property.getURL().equals(urlDetail.getUrl()) || hasTrailingSlace) {
                    redirectUrl = property.getURL();
                }
                else {
                    responseStatus = HttpStatus.SC_OK;
                }
                break;
            case PROJECT_URLS:
                responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
                Project project = null;
                try {
                    project = projectService.getProjectData(urlDetail.getProjectId());
                }
                catch (ResourceNotAvailableException e) {
                    project = null;
                }

                if (project == null) {
                    redirectUrl = getHigherHierarchyUrl(urlDetail.getProjectId(), DomainObject.project.getText());
                }
                else if (!project.getURL().equals(urlDetail.getUrl()) || hasTrailingSlace) {
                    redirectUrl = project.getURL();
                }
                else {
                    responseStatus = HttpStatus.SC_OK;
                }
                break;
            case BUILDER_URLS:
            case BUILDER_URLS_SEO:
                responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
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
                    if (builderCity != null) {
                        if (urlDetail.getCityName() != null && !urlDetail.getCityName().isEmpty()) {
                            redirectUrl = builderCity.getUrl();
                        }
                        else {
                            redirectUrl = EMPTY_URL;
                        }
                    }
                    else if (builder != null) {
                        redirectUrl = builder.getUrl();
                    }
                    else {
                        redirectUrl = EMPTY_URL;
                    }
                }
                else {
                    domainUrl = urlDetail.getPropertyType()
                            + builder.getUrl()
                            + urlDetail.getBedroomString();
                    if (builder.getBuilderCities() != null && builder.getBuilderCities().size() > 1) {
                        domainUrl = urlDetail.getCityName() + domainUrl;
                    }
                    if (!domainUrl.equals(urlDetail.getUrl()) || hasTrailingSlace) {
                        redirectUrl = domainUrl;
                    }
                    else {
                        responseStatus = HttpStatus.SC_OK;
                    }
                }
                break;
            case LOCALITY_SUBURB_LISTING:
            case LOCALITY_SUBURB_LISTING_SEO:
                // localitySuburbListingUrl, cityName, response status
                responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
                Object[] localitySuburbData = getLocalitySuburbListingUrl(urlDetail);

                domainUrl = (String) localitySuburbData[0];
                responseStatus = (Integer) localitySuburbData[2];
                boolean is404FallbackSet = (boolean) localitySuburbData[4];

                if (is404FallbackSet) {
                    redirectUrl = domainUrl;
                }
                else {
                    domainUrl = domainUrl.replaceFirst("property-sale", urlDetail.getPropertyType()) + urlDetail
                            .getBedroomString() + urlDetail.getPriceString();

                    if (!domainUrl.equals(urlDetail.getUrl()) || hasTrailingSlace) {
                        redirectUrl = domainUrl;
                        responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
                    }
                    else {
                        responseStatus = HttpStatus.SC_OK;
                    }
                }
                break;
            case LOCALITY_SUBURB_OVERVIEW:
                // localitySuburbListingUrl, cityName, response status
                responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
                Object[] localitySuburbUrlData = getLocalitySuburbListingUrl(urlDetail);

                domainUrl = (String) localitySuburbUrlData[0];
                responseStatus = (Integer) localitySuburbUrlData[2];
                String cityName = (String) localitySuburbUrlData[1];
                is404FallbackSet = (boolean) localitySuburbUrlData[4];

                if (is404FallbackSet) {
                    redirectUrl = domainUrl;
                    if (domainUrl != null && !domainUrl.isEmpty()) {
                        redirectUrl = domainUrl;
                        if (urlDetail.getOverviewType() != null) {
                            redirectUrl = domainUrl;
                        }
                    }
                }
                else {
                    domainUrl = domainUrl.replaceFirst("property-sale-", "");
                    domainUrl = domainUrl.replaceFirst(cityName, cityName + "-real-estate");
                    if (urlDetail.getOverviewType() != null) {
                        domainUrl = domainUrl.replaceFirst(
                                urlDetail.getLocalityId() + "",
                                urlDetail.getOverviewType()) + "-" + urlDetail.getLocalityId();
                    }
                    else {
                        domainUrl = domainUrl + urlDetail.getAppendingString();
                    }

                    if (!domainUrl.equals(urlDetail.getUrl()) || hasTrailingSlace) {
                        redirectUrl = domainUrl;
                        responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
                    }
                    else {
                        responseStatus = HttpStatus.SC_OK;
                    }
                }
                break;
            case LOCALITY_SUBURB_LANDMARK:
                responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
                Object[] localitySuburbLandMarkUrlData = getLocalitySuburbListingUrl(urlDetail);

                domainUrl = (String) localitySuburbLandMarkUrlData[0];
                responseStatus = (Integer) localitySuburbLandMarkUrlData[2];
                is404FallbackSet = (boolean) localitySuburbLandMarkUrlData[4];

                if (is404FallbackSet) {
                    redirectUrl = domainUrl;
                    if (domainUrl != null && !domainUrl.isEmpty()) {
                        redirectUrl = domainUrl;
                        if (urlDetail.getOverviewType() != null) {
                            redirectUrl = domainUrl;
                        }
                    }
                }
                else {
                    domainUrl = domainUrl.replaceFirst("property-sale-", "");
                    domainUrl = domainUrl + urlDetail.getAppendingString();
                    if (!domainUrl.equals(urlDetail.getUrl()) || hasTrailingSlace) {
                        redirectUrl = domainUrl;
                        responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
                    }
                    else {
                        responseStatus = HttpStatus.SC_OK;
                    }
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
                if (city == null || hasTrailingSlace) {
                    redirectUrl = EMPTY_URL;
                    responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
                }
                break;
            case STATIC_URLS:
            case DIWALI_MELA_URL:
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
            case NEWS_URLS:
                if (urlDetail.getCityName() != null && !urlDetail.getCityName().isEmpty()) {
                    city = null;
                    try {
                        city = cityService.getCityByName(urlDetail.getCityName());
                    }
                    catch (ResourceNotAvailableException e) {
                        city = null;
                    }
                    if (city == null) {
                        redirectUrl = EMPTY_URL;
                        responseStatus = HttpStatus.SC_MOVED_PERMANENTLY;
                    }
                }
                break;
            default:
                responseStatus = HttpStatus.SC_NOT_FOUND;
                break;
        }
        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            redirectUrl += URLRequestParamString;
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
                    return project.getURL();
                }
                catch (ResourceNotAvailableException | NullPointerException e) {
                    project = null;
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
                    return locality.getUrl();
                }
                catch (ResourceNotAvailableException | NullPointerException e) {
                    locality = null;
                }
            }
            else {
                locality = localityService.getActiveOrInactiveLocalityById(id);
                if (locality == null) {
                    return EMPTY_URL;
                }
                else {
                    suburbId = locality.getSuburbId();
                    cityId = locality.getSuburb().getCityId();
                }
            }
            id = suburbId;
            domainType = DomainObject.city.getText();
        }
        if (domainType.equals(DomainObject.suburb.getText())) {
            Suburb suburb = null;
            if (suburbId != null) {
                try {
                    suburb = suburbService.getSuburb(suburbId);
                    return suburb.getUrl();
                }
                catch (ResourceNotAvailableException | NullPointerException e) {
                    suburb = null;
                }
            }
            else {
                suburb = suburbService.getActiveOrInactiveSuburbById(id);
                if (suburb == null) {
                    return EMPTY_URL;
                }
                else {
                    cityId = suburb.getCityId();
                }
            }
            id = cityId;
            domainType = DomainObject.city.getText();
        }
        if (domainType.equals(DomainObject.city.getText())) {
            City city = null;
            if (cityId != null) {
                try {
                    city = cityService.getCity(cityId);
                    return city.getUrl();
                }
                catch (ResourceNotAvailableException | NullPointerException e) {
                    city = null;
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

        return new Object[] {
                newUrl,
                cityName.toLowerCase(),
                responseStatus,
                domainName.toLowerCase(),
                is404FallbackSet };
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
