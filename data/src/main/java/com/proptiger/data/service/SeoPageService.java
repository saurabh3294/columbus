package com.proptiger.data.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.BeanUtilsBean2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import com.proptiger.data.enums.resource.ResourceType;
import com.proptiger.data.enums.resource.ResourceTypeAction;
import com.proptiger.data.init.ExclusionAwareBeanUtilsBean;
import com.proptiger.data.model.City;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.SeoFooter;
import com.proptiger.data.model.SeoPage;
import com.proptiger.data.model.SeoPage.SeoPageJsonFiles;
import com.proptiger.data.model.SeoPage.Tokens;
import com.proptiger.data.model.Suburb;
import com.proptiger.data.model.Builder;
import com.proptiger.data.model.URLDetail;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.response.APIResponse;
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
    private SeoPageDao seoPageDao;

    @Autowired
    private URLService         urlService;

    private RestTemplate       restTemplate = new RestTemplate();

    @Value("${proptiger.url}")
    private String             websiteHost;
    
    
    public SeoPage getSeoMetaContentForPage(URLDetail urlDetail, String templateId){
        SeoPage seoPage = getSeoPageByTemplateId(templateId);
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
        setSeoTemplate(seoPage, mappings);
        
        return seoPage;
    }
    
    private void setSeoTemplate(SeoPage seopage, Map<String, String> mappings) {
        seopage.setTitle(replace(seopage.getTitle(), mappings));
        seopage.setDescription(replace(seopage.getDescription(), mappings));
        seopage.setKeywords(replace(seopage.getKeywords(), mappings));
        seopage.setH1(replace(seopage.getH1(), mappings));
        seopage.setH2(replace(seopage.getH2(), mappings));
        seopage.setH3(replace(seopage.getH3(), mappings));
        seopage.setH4(replace(seopage.getH4(), mappings));

    }

    private Map<String, String> buildTokensMap(CompositeSeoTokenData compositeSeoTokenData) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InstantiationException{
        Map<String, String> mappingTokenValues = new HashMap<String, String>();
        Tokens tokens[] = Tokens.values();
        System.out.println(new Gson().toJson(compositeSeoTokenData));
        Class<?> classObject= compositeSeoTokenData.getClass();
        Object nestedObject = null;
        Field field;
        System.out.println(classObject.getName());
        for(int i=0; i<tokens.length; i++){
            nestedObject = compositeSeoTokenData;
            if(tokens[i].getFieldName1() != null){
                System.out.println(tokens[i]);
                field = classObject.getDeclaredField(tokens[i].getFieldName1());
                field.setAccessible(true);
                nestedObject = field.get(compositeSeoTokenData);
                
            }
            if(nestedObject == null){
                continue;
            }
            System.out.println(nestedObject.getClass().getName());
            field = nestedObject.getClass().getDeclaredField(tokens[i].getFieldName2());
            field.setAccessible(true);
            mappingTokenValues.put(tokens[i].getValue(), (String)field.get(nestedObject));
        }
        return mappingTokenValues;
    }
    private CompositeSeoTokenData buildTokensValuesObject(URLDetail urlDetail){
         Property property = null;
         Project project = null;
         Locality locality = null;
         Suburb suburb = null;
         City city = null;
         Builder builder = null;
         String bedroomStr = null;
         String priceRangeStr = null;
         Gson gson = new Gson();
         
         if(urlDetail.getPropertyId() != null){
             property = propertyService.getProperty(urlDetail.getPropertyId());
             if(property == null){
                 throw new ResourceNotAvailableException(ResourceType.PROPERTY, ResourceTypeAction.GET);
             }
             project = property.getProject();
             locality = project.getLocality();
             suburb = locality.getSuburb();
             city = suburb.getCity();
             builder = project.getBuilder();
         }
         if(urlDetail.getProjectId() != null){
             String json = "{\"fields\":[\"distinctBedrooms\"]}";
             Selector selector = gson.fromJson(json, Selector.class);
             project = projectService.getProjectInfoDetails(selector, urlDetail.getProjectId());
             if(project == null){
                 throw new ResourceNotAvailableException(ResourceType.PROJECT, ResourceTypeAction.GET);
             }
             locality = project.getLocality();
             suburb = locality.getSuburb();
             city = suburb.getCity();
             builder = project.getBuilder();
             bedroomStr = project.getDistinctBedrooms().toString().replaceAll("[\\[\\]]","") + " BHK";
         }
         if(urlDetail.getLocalityId() != null){
             locality = localityService.getLocality(urlDetail.getLocalityId());
             if(locality == null){
                 throw new ResourceNotAvailableException(ResourceType.LOCALITY, ResourceTypeAction.GET);
             }
             suburb = locality.getSuburb();
             city = suburb.getCity();
         }
         if(urlDetail.getSuburbId() != null){
             suburb = suburbService.getSuburbById(urlDetail.getSuburbId());
             if(suburb == null){
                 throw new ResourceNotAvailableException(ResourceType.SUBURB, ResourceTypeAction.GET);
             }
             city = suburb.getCity();
         }
         if(urlDetail.getCityName() != null ){
             city = cityService.getCity(urlDetail.getCityName());
             if(city == null){
                 throw new ResourceNotAvailableException(ResourceType.CITY, ResourceTypeAction.GET);
             }
         }
         if(urlDetail.getBuilderId() != null){
             builder = builderService.getBuilderById(urlDetail.getBuilderId());
             if(builder == null){
                 throw new ResourceNotAvailableException(ResourceType.BUILDER, ResourceTypeAction.GET);
             }
         }
         if(urlDetail.getBedrooms() != null){
             bedroomStr = urlDetail.getBedrooms() + " BHK";
         }
         if(urlDetail.getMinPriceRange() != null){
             priceRangeStr = urlDetail.getMinPriceRange() +" - "+ urlDetail.getMaxPriceRange() + " " + urlDetail.getPriceUnitName();
         }
             
         return new CompositeSeoTokenData(property, project, locality, suburb, city, builder,   bedroomStr, priceRangeStr);
    }
    
    private SeoPage getSeoPageObjectFromJson(InputStream in) throws FileNotFoundException {
        Gson gson = new Gson();
        BufferedReader buf = new BufferedReader(new InputStreamReader(in));
        return gson.fromJson(buf, SeoPage.class);
    }

    @Cacheable(value = Constants.CacheName.SEO_FOOTER, key = "#url")
    public SeoFooter getSeoFooterUrlsByPage(String url) {
        SeoFooter seoFooter = seoFooterDao.findOne(url);
        if (seoFooter == null) {
            seoFooter = new SeoFooter();
        }
        return seoFooter;
    }
    
    public SeoPage getSeoPageByTemplateId(String templateId){
        SeoPage seoPage = seoPageDao.findOne(templateId);
        if(seoPage == null){
            seoPage = new SeoPage();
        }
        return seoPage;
    }

    public Map<String, Object> getSeoContentForPage(String url) throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException, FileNotFoundException {

        Map<String, Object> seoResponse = null;
        try{
             seoResponse = new Gson().fromJson(        
                restTemplate.getForObject(
                        websiteHost + "getSeoTags.php?url={URL}",
                        String.class,
                        Collections.singletonMap("URL", url)),
                HashMap.class);
        }catch(JsonSyntaxException e){
            
        }
        
        if (seoResponse == null) {
            seoResponse = new HashMap<String, Object>();
        }

        SeoPage seoPage = getSeoMetaContent(url);

        BeanUtilsBean beanUtilsBean = new BeanUtilsBean();
        Map<String, Object> seoMetaData = beanUtilsBean.describe(seoPage);
        Map<String, Object> metaData = (Map<String, Object>) seoResponse.get("meta");
        if (metaData == null) {
            metaData = new HashMap<String, Object>();
        }
        metaData.putAll(seoMetaData);
        seoResponse.put("meta", metaData);

        seoResponse.put("footer", getSeoFooterUrlsByPage(url).getFooterUrls());
        return seoResponse;
    }

    /*
     * This function (choosePage) chooses the function to be called depending
     * upon the PAGETYPE of the given URLDETAIL
     */
    public SeoPage getSeoMetaContent(String url) throws FileNotFoundException, IllegalAccessException,
            InvocationTargetException {
        URLDetail urlDetail = urlService.parse(url);
        SeoPage seoPage = null;
        switch (urlDetail.getPageType()) {
            case PROJECT:
                seoPage = getProjectPageSeo(urlDetail.getProjectId());
                break;
            case CITY_LISTING:
            case CITY_LISTING_BHK:
            case CITY_LISTING_BHK_BUDGET:
                seoPage = getCityListingPageSeo(urlDetail.getCityName());
                break;
            case CITY_OVERVIEW:
                seoPage = getCityOverviewPageSeo(urlDetail.getCityName());
                break;
            case LOCALITY_LISTING:
                seoPage = getLocalityListingPageSeo(urlDetail.getLocalityId());
                break;
            case LOCALITY_OVERVIEW:
                seoPage = getLocalityOverviewPageSeo(urlDetail.getLocalityId());
                break;
            case SUBURB_LISTING:
                seoPage = getSuburbListingPageSeo(urlDetail.getSuburbId());
                break;
            case BUILDER:
                seoPage = getBuilderNamePageSeo(urlDetail.getBuilderId());
                break;
            case ALL_BUILDERS_IN_A_CITY:
                seoPage = getAllBuildersInCityPageSeo(urlDetail.getCityName());
                break;
            case CITY_BUILDER_PAGE:
                seoPage = getCityBuilderPageSeo(urlDetail.getCityName(), urlDetail.getBuilderId());
                break;
            default:
                return null;
        }

        return seoPage;
    }

    /*
     * This function(getCityOverviewPageSeo()) gets the CITY object using
     * CITY Id, creates a SEOPAGE object from the json file of the page and
     * passes them to mapCityTemplate()
     */
    public SeoPage getCityOverviewPageSeo(String cityName) throws FileNotFoundException {
        City city = cityService.getCity(cityName);
        SeoPageJsonFiles jfile = SeoPageJsonFiles.CityOverviewPage;
        SeoPage seoPage = readFile(jfile);
        mapCityTemplate(seoPage, city);

        return seoPage;
    }

    public SeoPage getCityListingPageSeo(String cityName) throws FileNotFoundException {
        City city = cityService.getCity(cityName);
        SeoPageJsonFiles jfile = SeoPageJsonFiles.CityListingPage;
        SeoPage seoPage = readFile(jfile);
        mapCityTemplate(seoPage, city);

        return seoPage;
    }

    public SeoPage getLocalityOverviewPageSeo(int localityId) throws FileNotFoundException {
        Locality locality = localityService.getLocality(localityId);
        int suburbId = locality.getSuburbId();
        Suburb suburb = suburbService.getSuburb(suburbId);
        int cityId = suburb.getCityId();
        City city = cityService.getCity(cityId);
        SeoPageJsonFiles jfile = SeoPageJsonFiles.LocalityOverviewPage;

        SeoPage seoPage = readFile(jfile);
        mapLocalityTemplate(seoPage, locality, city);

        return seoPage;
    }

    public SeoPage getLocalityListingPageSeo(int localityId) throws FileNotFoundException {
        Locality locality = localityService.getLocality(localityId);
        int suburbId = locality.getSuburbId();
        Suburb suburb = suburbService.getSuburb(suburbId);
        int cityId = suburb.getCityId();
        City city = cityService.getCity(cityId);
        SeoPageJsonFiles jfile = SeoPageJsonFiles.LocalityListingPage;

        SeoPage seoPage = readFile(jfile);
        mapLocalityTemplate(seoPage, locality, city);

        return seoPage;
    }

    public SeoPage getSuburbListingPageSeo(int suburbId) throws FileNotFoundException {
        Suburb suburb = suburbService.getSuburb(suburbId);
        int cityId = suburb.getCityId();
        City city = cityService.getCity(cityId);
        SeoPageJsonFiles jfile = SeoPageJsonFiles.SuburbListingPage;

        SeoPage seoPage = readFile(jfile);
        mapSuburbTemplate(seoPage, suburb, city);

        return seoPage;
    }

    public SeoPage getProjectPageSeo(int projectId) throws FileNotFoundException {
        String j = "{\"fields\":[\"distinctBedrooms\"]}";
        Gson gson = new Gson();
        Selector selector = gson.fromJson(j, Selector.class);

        Project project = projectService.getProjectInfoDetails(selector, projectId);
        Set<Integer> distinctBedrooms = new HashSet<>();
        distinctBedrooms = project.getDistinctBedrooms();
        String bedrooms = distinctBedrooms.toString() + " BHK";
        if (bedrooms.contains("0") || bedrooms == " BHK")
            bedrooms = "";
        bedrooms = bedrooms.replace("[", "");
        bedrooms = bedrooms.replace("]", "");

        Locality locality = project.getLocality();
        Suburb suburb = locality.getSuburb();
        City city = suburb.getCity();
        SeoPageJsonFiles jfile = SeoPageJsonFiles.ProjectPage;

        SeoPage seoPage = readFile(jfile);
        mapProjectTemplate(seoPage, project, locality, city, bedrooms);

        return seoPage;
    }

    public SeoPage getBuilderNamePageSeo(int builderId) throws FileNotFoundException {
        Builder builder = builderService.getBuilderById(builderId);
        SeoPageJsonFiles jfile = SeoPageJsonFiles.BuilderPage;
        SeoPage seoPage = readFile(jfile);
        mapBuilderTemplate(seoPage, builder);

        return seoPage;
    }

    public SeoPage getAllBuildersInCityPageSeo(String cityName) throws FileNotFoundException {
        City city = cityService.getCity(cityName);
        SeoPageJsonFiles jfile = SeoPageJsonFiles.AllBuildersInCityPage;
        SeoPage seoPage = readFile(jfile);
        mapCityTemplate(seoPage, city);

        return seoPage;
    }

    public SeoPage getCityBuilderPageSeo(String cityName, int builderId) {
        City city = cityService.getCity(cityName);
        Builder builder = builderService.getBuilderById(builderId);
        SeoPageJsonFiles jfile = SeoPageJsonFiles.CityBuilderPage;
        SeoPage seoPage = readFile(jfile);
        mapCityBuilderTemplate(seoPage, city, builder);

        return seoPage;
    }

    /*
     * This function(mapCityTemplate()) takes SEOPAGE and CITY objects , creates
     * a MAPPING of the tokens with theirvalues. Then replace the tokens with
     * their values using replace() function and sets the fields of SEOPAGE.
     */
    private void mapCityTemplate(SeoPage seopage, City city) {
        Tokens token = Tokens.City;

        HashMap<String, String> mappings = new HashMap<String, String>();
        mappings.put(token.getToken(token), city.getLabel());

        seopage.setTitle(replace(seopage.getTitle(), mappings));
        seopage.setDescription(replace(seopage.getDescription(), mappings));
        seopage.setKeywords(replace(seopage.getKeywords(), mappings));
        seopage.setH1(replace(seopage.getH1(), mappings));
        seopage.setH2(replace(seopage.getH2(), mappings));
        seopage.setH3(replace(seopage.getH3(), mappings));
        seopage.setH4(replace(seopage.getH4(), mappings));

    }

    private void mapLocalityTemplate(SeoPage seopage, Locality locality, City city) {
        Tokens token1 = Tokens.Locality;
        Tokens token2 = Tokens.City;

        HashMap<String, String> mappings = new HashMap<String, String>();
        mappings.put(token1.getToken(token1), locality.getLabel());
        mappings.put(token2.getToken(token2), city.getLabel());

        seopage.setTitle(replace(seopage.getTitle(), mappings));
        seopage.setDescription(replace(seopage.getDescription(), mappings));
        seopage.setKeywords(replace(seopage.getKeywords(), mappings));
        seopage.setH1(replace(seopage.getH1(), mappings));
        seopage.setH2(replace(seopage.getH2(), mappings));
        seopage.setH3(replace(seopage.getH3(), mappings));
        seopage.setH4(replace(seopage.getH4(), mappings));

    }

    private void mapSuburbTemplate(SeoPage seopage, Suburb suburb, City city) {

        Tokens token1 = Tokens.Suburb;
        Tokens token2 = Tokens.City;

        HashMap<String, String> mappings = new HashMap<String, String>();
        mappings.put(token1.getToken(token1), suburb.getLabel());
        mappings.put(token2.getToken(token2), city.getLabel());

        seopage.setTitle(replace(seopage.getTitle(), mappings));
        seopage.setDescription(replace(seopage.getDescription(), mappings));
        seopage.setKeywords(replace(seopage.getKeywords(), mappings));
        seopage.setH1(replace(seopage.getH1(), mappings));
        seopage.setH2(replace(seopage.getH2(), mappings));
        seopage.setH3(replace(seopage.getH3(), mappings));
        seopage.setH4(replace(seopage.getH4(), mappings));

    }

    private void mapProjectTemplate(SeoPage seopage, Project project, Locality locality, City city, String bedrooms) {
        Tokens token1 = Tokens.ProjectName;
        Tokens token2 = Tokens.Locality;
        Tokens token3 = Tokens.City;
        Tokens token4 = Tokens.BHK;

        HashMap<String, String> mappings = new HashMap<String, String>();
        mappings.put(token1.getToken(token1), project.getName());
        mappings.put(token2.getToken(token2), locality.getLabel());
        mappings.put(token3.getToken(token3), city.getLabel());
        mappings.put(token4.getToken(token4), bedrooms);

        seopage.setTitle(replace(seopage.getTitle(), mappings));
        seopage.setDescription(replace(seopage.getDescription(), mappings));
        seopage.setKeywords(replace(seopage.getKeywords(), mappings));
        seopage.setH1(replace(seopage.getH1(), mappings));
        seopage.setH2(replace(seopage.getH2(), mappings));
        seopage.setH3(replace(seopage.getH3(), mappings));
        seopage.setH4(replace(seopage.getH4(), mappings));

    }

    private void mapBuilderTemplate(SeoPage seopage, Builder builder) {
        Tokens token = Tokens.BuiderName;

        HashMap<String, String> mappings = new HashMap<String, String>();
        mappings.put(token.getToken(token), builder.getName());

        seopage.setTitle(replace(seopage.getTitle(), mappings));
        seopage.setDescription(replace(seopage.getDescription(), mappings));
        seopage.setKeywords(replace(seopage.getKeywords(), mappings));
        seopage.setH1(replace(seopage.getH1(), mappings));
        seopage.setH2(replace(seopage.getH2(), mappings));
        seopage.setH3(replace(seopage.getH3(), mappings));
        seopage.setH4(replace(seopage.getH4(), mappings));
    }

    private void mapCityBuilderTemplate(SeoPage seopage, City city, Builder builder) {
        Tokens token1 = Tokens.City;
        Tokens token2 = Tokens.BuiderName;

        HashMap<String, String> mappings = new HashMap<String, String>();
        mappings.put(token1.getToken(token1), city.getLabel());
        mappings.put(token2.getToken(token2), builder.getName());

        seopage.setTitle(replace(seopage.getTitle(), mappings));
        seopage.setDescription(replace(seopage.getDescription(), mappings));
        seopage.setKeywords(replace(seopage.getKeywords(), mappings));
        seopage.setH1(replace(seopage.getH1(), mappings));
        seopage.setH2(replace(seopage.getH2(), mappings));
        seopage.setH3(replace(seopage.getH3(), mappings));
        seopage.setH4(replace(seopage.getH4(), mappings));

    }

    /*
     * This function(replace()) replaces the tokens in the TEXT with their
     * values using the MAPPING
     */
    private String replace(String text, Map<String, String> mappings) {
        Pattern pattern = Pattern.compile("(<.+?>)");
        Matcher matcher = pattern.matcher(text);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String replacement = mappings.get(matcher.group(1));
            if (replacement != null) {
                matcher.appendReplacement(buffer, replacement);
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private SeoPage readFile(SeoPageJsonFiles jfile) {
        String filepath = jfile.getFilePath(jfile);
        Resource resource = appContext.getResource("classpath:/" + filepath);
        SeoPage seoPage;
        try {
            seoPage = getSeoPageObjectFromJson(resource.getInputStream());
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            throw new ProAPIException(e);
        }
        return seoPage;
    }
    
    public static class CompositeSeoTokenData{
        private Property property;
        private Project project;
        private Locality locality;
        private Suburb suburb;
        private City city;
        private Builder builder;
        private String bedroomsStr;
        private String priceRangeStr;
        
        public CompositeSeoTokenData(Property property, Project project, Locality locality, Suburb suburb, City city, Builder builder, String bedrooms, String priceRange){
            this.property = property;
            this.project = project;
            this.locality = locality;
            this.suburb = suburb;
            this.city = city;
            this.builder = builder;
            this.bedroomsStr = bedrooms;
            this.priceRangeStr = priceRange;
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
        
    }
}
