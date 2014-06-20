package com.proptiger.data.service;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.proptiger.data.enums.resource.ResourceType;
import com.proptiger.data.enums.resource.ResourceTypeAction;
import com.proptiger.data.model.Builder;
import com.proptiger.data.model.City;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.SeoFooter;
import com.proptiger.data.model.SeoPage;
import com.proptiger.data.model.SeoPage.Tokens;
import com.proptiger.data.model.Suburb;
import com.proptiger.data.model.URLDetail;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.repo.SeoFooterDao;
import com.proptiger.data.repo.SeoPageDao;
import com.proptiger.data.util.Constants;
import com.proptiger.exception.ProAPIException;
import com.proptiger.exception.ResourceNotAvailableException;

@Service
public class SeoPageService {

    @Autowired
    private ProjectService     projectService;

    @Autowired
    private CityService        cityService;

    @Autowired
    private LocalityService    localityService;

    @Autowired
    private SuburbService      suburbService;

    @Autowired
    private BuilderService     builderService;

    @Autowired
    private PropertyService    propertyService;

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private SeoFooterDao       seoFooterDao;

    @Autowired
    private SeoPageDao         seoPageDao;

    @Autowired
    private URLService         urlService;

    private RestTemplate       restTemplate = new RestTemplate();

    @Value("${proptiger.url}")
    private String             websiteHost;

    @Autowired
    private ApplicationContext applicationContext;

    private static Logger      logger       = LoggerFactory.getLogger(SeoPageService.class);

    private Pattern            pattern      = Pattern.compile("(<.+?>)");

    public Map<String, Object> getSeoContentForPage(URLDetail urlDetail) throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException, FileNotFoundException {

        String templateId = urlDetail.getTemplateId();
        Map<String, Object> seoResponse = null;
        try {
            seoResponse = new Gson().fromJson(
                    restTemplate.getForObject(
                            websiteHost + "getSeoTags.php?url={URL}",
                            String.class,
                            Collections.singletonMap("URL", urlDetail.getUrl())),
                    HashMap.class);
        }
        catch (JsonSyntaxException e) {

        }

        if (seoResponse == null) {
            seoResponse = new HashMap<String, Object>();
        }

        SeoPage seoPage = getSeoMetaContentForPage(urlDetail, templateId);
        BeanUtilsBean beanUtilsBean = new BeanUtilsBean();
        Map<String, Object> seoMetaData = beanUtilsBean.describe(seoPage);
        Map<String, Object> metaData = (Map<String, Object>) seoResponse.get("meta");
        if (metaData == null) {
            metaData = new HashMap<String, Object>();
        }
        metaData.putAll(seoMetaData);
        seoResponse.put("meta", metaData);

        String url = getFooterUrl(urlDetail);
        // RTRIM the urls with extra slashes.
        url = url.replaceAll("[/]*$", "");
        seoResponse.put("footer", applicationContext.getBean(SeoPageService.class).getSeoFooterUrlsByPage(url)
                .getFooterUrls());
        return seoResponse;
    }

    public SeoPage getSeoMetaContentForPage(URLDetail urlDetail, String templateId) {
        SeoPage seoPage = applicationContext.getBean(SeoPageService.class).getSeoPageByTemplateId(
                templateId,
                urlDetail.getUrl());
        CompositeSeoTokenData compositeSeoTokenData = buildTokensValuesObject(urlDetail);
        Map<String, String> mappings = null;
        try {
            mappings = buildTokensMap(compositeSeoTokenData);
        }
        catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException
                | InstantiationException e) {
            // TODO Auto-generated catch block
            throw new ProAPIException(e);
        }
        setSeoTemplate(seoPage, mappings, urlDetail);

        return seoPage;
    }

    @Cacheable(value = Constants.CacheName.SEO_FOOTER)
    public SeoFooter getSeoFooterUrlsByPage(String url) {
        SeoFooter seoFooter = seoFooterDao.findOne(url);
        if (seoFooter == null) {
            seoFooter = new SeoFooter();
        }
        return seoFooter;
    }

    @Cacheable(value = Constants.CacheName.SEO_TEMPLATE)
    public SeoPage getSeoPageByTemplateId(String templateId, String url) {
        SeoPage seoPage = seoPageDao.findOne(templateId);
        if (seoPage == null) {
            logger.error(" SEO CONTENT NOT FOUND For templateId, url : " + templateId + "," + url);
            seoPage = new SeoPage();
        }
        return seoPage;
    }

    private String getFooterUrl(URLDetail urlDetail) {
        if (urlDetail.getFallBackUrl() != null)
            return urlDetail.getFallBackUrl();
        else
            return urlDetail.getUrl();
    }

    private void setSeoTemplate(SeoPage seopage, Map<String, String> mappings, URLDetail urlDetail) {
        seopage.setTitle(replace(seopage.getTitle(), mappings, urlDetail));
        seopage.setDescription(replace(seopage.getDescription(), mappings, urlDetail));
        seopage.setKeywords(replace(seopage.getKeywords(), mappings, urlDetail));
        seopage.setH1(replace(seopage.getH1(), mappings, urlDetail));
        seopage.setH2(replace(seopage.getH2(), mappings, urlDetail));
        seopage.setH3(replace(seopage.getH3(), mappings, urlDetail));
        seopage.setH4(replace(seopage.getH4(), mappings, urlDetail));

    }

    private Map<String, String> buildTokensMap(CompositeSeoTokenData compositeSeoTokenData)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException,
            InstantiationException {
        Map<String, String> mappingTokenValues = new HashMap<String, String>();
        Tokens tokens[] = Tokens.values();

        Class<?> classObject = compositeSeoTokenData.getClass();
        Object nestedObject = null;
        Field field = null;
        Object valueObject = null;
        for (int i = 0; i < tokens.length; i++) {
            nestedObject = compositeSeoTokenData;
            if (tokens[i].getFieldName1() != null) {

                field = classObject.getDeclaredField(tokens[i].getFieldName1());
                field.setAccessible(true);
                nestedObject = field.get(compositeSeoTokenData);

            }
            if (nestedObject == null) {
                continue;
            }

            field = nestedObject.getClass().getDeclaredField(tokens[i].getFieldName2());
            field.setAccessible(true);
            valueObject = field.get(nestedObject);
            if (valueObject == null) {
                continue;
            }
            mappingTokenValues.put(
                    tokens[i].getValue(),
                    (String) String.format(tokens[i].getReplaceString(), valueObject));
        }
        return mappingTokenValues;
    }

    private CompositeSeoTokenData buildTokensValuesObject(URLDetail urlDetail) {
        Property property = null;
        Project project = null;
        Locality locality = null;
        Suburb suburb = null;
        City city = null;
        Builder builder = null;
        String bedroomStr = null;
        String priceRangeStr = null;
        Integer bathrooms = null;
        Integer minBudget = urlDetail.getMinBudget();
        Integer maxBudget = urlDetail.getMaxBudget();
        Gson gson = new Gson();

        if (urlDetail.getPropertyId() != null) {
            property = propertyService.getProperty(urlDetail.getPropertyId());
            if (property == null) {
                throw new ResourceNotAvailableException(ResourceType.PROPERTY, ResourceTypeAction.GET);
            }
            project = property.getProject();
            locality = project.getLocality();
            suburb = locality.getSuburb();
            city = suburb.getCity();
            builder = project.getBuilder();
            if (property.getBathrooms() > 0) {
                bathrooms = property.getBathrooms();
            }
        }
        if (urlDetail.getProjectId() != null) {
            String json = "{\"fields\":[\"distinctBedrooms\"]}";
            Selector selector = gson.fromJson(json, Selector.class);
            project = projectService.getProjectInfoDetails(selector, urlDetail.getProjectId());
            if (project == null) {
                throw new ResourceNotAvailableException(ResourceType.PROJECT, ResourceTypeAction.GET);
            }
            locality = project.getLocality();
            suburb = locality.getSuburb();
            city = suburb.getCity();
            builder = project.getBuilder();
            Set<Integer> bedrooms = project.getDistinctBedrooms();
            bedrooms.remove(0);
            if (bedrooms.size() > 0) {
                bedroomStr = project.getDistinctBedrooms().toString().replaceAll("[\\[\\]]", "");
            }
        }
        if (urlDetail.getLocalityId() != null) {
            locality = localityService.getLocality(urlDetail.getLocalityId());
            if (locality == null) {
                throw new ResourceNotAvailableException(ResourceType.LOCALITY, ResourceTypeAction.GET);
            }
            suburb = locality.getSuburb();
            city = suburb.getCity();
        }
        if (urlDetail.getSuburbId() != null) {
            suburb = suburbService.getSuburbById(urlDetail.getSuburbId());
            if (suburb == null) {
                throw new ResourceNotAvailableException(ResourceType.SUBURB, ResourceTypeAction.GET);
            }
            city = suburb.getCity();
        }
        if (urlDetail.getCityName() != null) {
            city = cityService.getCityByName(urlDetail.getCityName());
            if (city == null) {
                throw new ResourceNotAvailableException(ResourceType.CITY, ResourceTypeAction.GET);
            }
        }
        if (urlDetail.getBuilderId() != null) {
            builder = builderService.getBuilderById(urlDetail.getBuilderId());
            if (builder == null) {
                throw new ResourceNotAvailableException(ResourceType.BUILDER, ResourceTypeAction.GET);
            }
        }
        if (urlDetail.getBedrooms() != null && urlDetail.getBedrooms() > 0) {
            bedroomStr = urlDetail.getBedrooms().toString();
        }
        // Conversion of price in Lacs.
        if (minBudget != null) {
            minBudget = minBudget / 100000;
            maxBudget = maxBudget / 100000;
            priceRangeStr = minBudget + "-" + maxBudget;
        }

        return new CompositeSeoTokenData(
                property,
                project,
                locality,
                suburb,
                city,
                builder,
                bedroomStr,
                priceRangeStr,
                bathrooms);
    }

    /*
     * This function(replace()) replaces the tokens in the TEXT with their
     * values using the MAPPING
     */
    private String replace(String text, Map<String, String> mappings, URLDetail urlDetail) {
        if (text == null) {
            return null;
        }

        Matcher matcher = this.pattern.matcher(text);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String replacement = mappings.get(matcher.group(1).toLowerCase());
            if (replacement == null) {
                replacement = "";
                logger.error(matcher.group(1) + " Token Not Found At constructing SEO template with request details: "
                        + urlDetail);
            }
            matcher.appendReplacement(buffer, replacement);
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    public static class CompositeSeoTokenData {
        private Property property;
        private Project  project;
        private Locality locality;
        private Suburb   suburb;
        private City     city;
        private Builder  builder;
        private String   bedroomsStr;
        private String   priceRangeStr;
        private Integer  bathrooms;

        public CompositeSeoTokenData(
                Property property,
                Project project,
                Locality locality,
                Suburb suburb,
                City city,
                Builder builder,
                String bedrooms,
                String priceRange,
                Integer bathrooms) {
            this.property = property;
            this.project = project;
            this.locality = locality;
            this.suburb = suburb;
            this.city = city;
            this.builder = builder;
            this.bedroomsStr = bedrooms;
            this.priceRangeStr = priceRange;
            this.bathrooms = bathrooms;
        }

        public Property getProperty() {
            return property;
        }

        public void setProperty(Property property) {
            this.property = property;
        }

        public Project getProject() {
            return project;
        }

        public void setProject(Project project) {
            this.project = project;
        }

        public Locality getLocality() {
            return locality;
        }

        public void setLocality(Locality locality) {
            this.locality = locality;
        }

        public Suburb getSuburb() {
            return suburb;
        }

        public void setSuburb(Suburb suburb) {
            this.suburb = suburb;
        }

        public City getCity() {
            return city;
        }

        public void setCity(City city) {
            this.city = city;
        }

        public String getBedroomsStr() {
            return bedroomsStr;
        }

        public void setBedroomsStr(String bedroomsStr) {
            this.bedroomsStr = bedroomsStr;
        }

        public String getPriceRangeStr() {
            return priceRangeStr;
        }

        public void setPriceRangeStr(String priceRangeStr) {
            this.priceRangeStr = priceRangeStr;
        }

        public Builder getBuilder() {
            return builder;
        }

        public void setBuilder(Builder builder) {
            this.builder = builder;
        }

        public Integer getBathrooms() {
            return bathrooms;
        }

        public void setBathrooms(Integer bathrooms) {
            this.bathrooms = bathrooms;
        }

    }
}
