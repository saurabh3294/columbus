package com.proptiger.columbus.typeahead;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.proptiger.core.model.cms.Builder;
import com.proptiger.core.model.cms.City;
import com.proptiger.core.model.cms.Locality;
import com.proptiger.core.model.cms.Project;
import com.proptiger.core.model.cms.Suburb;
import com.proptiger.core.util.HttpRequestUtil;

@Component
public class TaTestGenerator {

    @Value("${proptiger.url}")
    private String          BASE_URL;

    @Value("${test.city.api.url}")
    private String          CITY_API_URL;

    @Value("${test.locality.api.url}")
    private String          LOCALITY_API_URL;

    @Value("${test.project.api.url}")
    private String          PROJECT_API_URL;

    @Value("${test.suburb.api.url}")
    private String          SUBURB_API_URL;

    @Value("${test.builder.api.url}")
    private String          BUILDER_API_URL;

    @Value("${test.default.entity.fetch.pagesize}")
    private int             DefaultEntityFetchPageSize;
    
    @Autowired
    private HttpRequestUtil httpRequestUtil;

    private static Logger   logger              = LoggerFactory.getLogger(TaTestGenerator.class);

    public static String    selectorAllCity     = "selector={\"fields\":[\"id\",\"label\"],\"paging\":{\"start\":%s,\"rows\":%s}}";
    public static String    selectorAllLocality = "selector={\"fields\":[\"localityId\",\"label\"],\"paging\":{\"start\":%s,\"rows\":%s}}";
    public static String    selectorAllProject  = "selector={\"fields\":[\"projectId\",\"name\",\"locality\",\"suburb\",\"city\",\"label\",\"builder\"],\"paging\":{\"start\":%s,\"rows\":%s}}";
    public static String    selectorAllSuburb   = "selector={\"fields\":[\"id\",\"label\"],\"paging\":{\"start\":%s,\"rows\":%s}}";
    public static String    selectorAllBuilder  = "selector={\"fields\":[\"id\",\"name\"],\"paging\":{\"start\":%s,\"rows\":%s}}";

    public List<TaTestCase> getTestCasesByType(TaTestCaseType ttcType, int limit) {
        switch (ttcType) {
            case City:
                return getCityTestCases(limit);
            case Locality:
                return getLocalityTestCases(limit);
            case Project:
                return getProjectTestCases(limit);
            case Suburb:
                return getSuburbTestCases(limit);
            case Builder:
                return getBuilderTestCases(limit);
            default:
                break;
        }
        return new ArrayList<TaTestCase>();
    }

    private List<TaTestCase> getCityTestCases(int limit) {
        List<TaTestCase> testList = new ArrayList<TaTestCase>();
        List<City> entitylist = getEntityList(
                selectorAllCity,
                CITY_API_URL,
                City.class,
                DefaultEntityFetchPageSize,
                limit);
        for (City x : entitylist) {
            testList.add(new TaTestCase(x.getLabel(), TaTestCaseType.City, 1, 1, "TYPEAHEAD-CITY-" + x.getId()));
        }
        return testList;
    }

    private List<TaTestCase> getLocalityTestCases(int limit) {
        List<TaTestCase> testList = new ArrayList<TaTestCase>();
        List<Locality> entitylist = getEntityList(
                selectorAllLocality,
                LOCALITY_API_URL,
                Locality.class,
                DefaultEntityFetchPageSize,
                limit);
        for (Locality x : entitylist) {
            testList.add(new TaTestCase(x.getLabel(), TaTestCaseType.Locality, 1, 3, "TYPEAHEAD-LOCALITY-" + x
                    .getLocalityId()));
        }
        return testList;
    }

    private List<TaTestCase> getProjectTestCases(int limit) {
        List<TaTestCase> testList = new ArrayList<TaTestCase>();
        List<Project> entitylist = getEntityList(
                selectorAllProject,
                PROJECT_API_URL,
                Project.class,
                DefaultEntityFetchPageSize,
                limit);
        for (Project x : entitylist) {
            testList.add(new TaTestCase(x.getName(), TaTestCaseType.Project, 1, 3, "TYPEAHEAD-PROJECT-" + x
                    .getProjectId()));
        }
        return testList;
    }

    private List<TaTestCase> getSuburbTestCases(int limit) {
        List<TaTestCase> testList = new ArrayList<TaTestCase>();
        List<Suburb> entitylist = getEntityList(
                selectorAllSuburb,
                SUBURB_API_URL,
                Suburb.class,
                DefaultEntityFetchPageSize,
                limit);
        for (Suburb x : entitylist) {
            testList.add(new TaTestCase(x.getLabel(), TaTestCaseType.Suburb, 1, 3, "TYPEAHEAD-SUBURB-" + x.getId()));
        }
        return testList;
    }

    private List<TaTestCase> getBuilderTestCases(int limit) {
        int defaultEntityFetchPageSize_buidler = Math.min(DefaultEntityFetchPageSize, 150);
        List<Builder> entityList = getEntityList(
                selectorAllBuilder,
                BUILDER_API_URL,
                Builder.class,
                defaultEntityFetchPageSize_buidler,
                limit);
        List<TaTestCase> testList = new ArrayList<TaTestCase>();
        for (Builder x : entityList) {
            testList.add(new TaTestCase(x.getName(), TaTestCaseType.Builder, 1, 3, "TYPEAHEAD-BUILDER-" + x.getId()));
        }
        return testList;
    }

    private <T> List<T> getEntityList(String selector, String API_URL, Class<T> clazz, int pageSize, int limit) {
        String entityName = clazz.getSimpleName();
        logger.info("Fetching entity list : [" + entityName + ", " + pageSize + ", " + limit + "]");
        int start = 0, count = pageSize, templimit = limit;
        String url = "";
        URI uri = null;
        List<T> entitylist = new ArrayList<T>();
        List<T> templist = null;
        while (true) {
            count = Math.min(pageSize, templimit);
            if (count == 0) {
                break;
            }
            url = BASE_URL + API_URL + "?" + String.format(selector, start, count);
            logger.debug("Api Url for fetching entity " + entityName + " : [ " + url + "]");
            uri = URI.create(UriComponentsBuilder.fromUriString(url).build().encode().toString());
            try{
                templist = httpRequestUtil.getInternalApiResultAsTypeListFromCache(uri, clazz);
            }
            catch (Exception ex){
                logger.error("Error while fetching enities. URL = (" + url + ")");
                throw ex;
            }
            if (templist == null || templist.isEmpty()) {
                break;
            }
            entitylist.addAll(templist);
            templimit -= count;
            start += count;
        }
        logger.info(entitylist.size() + " Entities recieved for : " + entityName);
        return entitylist;
    }

}
