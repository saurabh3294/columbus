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

    @Value("${BASE_URL}")
    private String          BASE_URL;

    @Value("${CITY_API_URL}")
    private String          CITY_API_URL;

    @Value("${LOCALITY_API_URL}")
    private String          LOCALITY_API_URL;

    @Value("${PROJECT_API_URL}")
    private String          PROJECT_API_URL;

    @Value("${SUBURB_API_URL}")
    private String          SUBURB_API_URL;

    @Value("${BUILDER_API_URL}")
    private String          BUILDER_API_URL;

    @Value("${DefaultPageFetchSize}")
    private int             DefaultPageFetchSize;

    @Autowired
    private HttpRequestUtil httpRequestUtil;

    private static Logger   logger              = LoggerFactory.getLogger(TaTestGenerator.class);

    public static String    selectorAllCity     = "selector={\"fields\":[\"id\",\"label\"],\"paging\":{\"start\":%s,\"rows\":%s}}";
    public static String    selectorAllLocality = "selector={\"fields\":[\"localityId\",\"label\"],\"paging\":{\"start\":%s,\"rows\":%s}}";
    public static String    selectorAllProject  = "selector={\"fields\":[\"projectId\",\"name\",\"locality\",\"suburb\",\"city\",\"label\",\"builder\"],\"paging\":{\"start\":%s,\"rows\":%s}}";
    public static String    selectorAllSuburb   = "selector={\"fields\":[\"id\",\"label\"],\"paging\":{\"start\":%s,\"rows\":%s}}";
    public static String    selectorAllBuilder  = "selector={\"fields\":[\"id\",\"name\"],\"paging\":{\"start\":%s,\"rows\":%s}}";

    public List<TaTestCase> getTestCasesByType(TaTestCaseType ttcType) {
        switch (ttcType) {
            case City:
                return getCityTestCases();
            case Locality:
                return getLocalityTestCases();
            case Project:
                return getProjectTestCases();
            case Suburb:
                return getSuburbTestCases();
            case Builder:
                return getBuilderTestCases();
            default:
                break;
        }
        return new ArrayList<TaTestCase>();
    }

    private List<TaTestCase> getCityTestCases() {
        List<TaTestCase> testList = new ArrayList<TaTestCase>();
        List<City> entitylist = getEntityList(selectorAllCity, CITY_API_URL, City.class, DefaultPageFetchSize);
        for (City x : entitylist) {
            testList.add(new TaTestCase(x.getLabel(), TaTestCaseType.City, 1, 1, "TYPEAHEAD-CITY-" + x.getId()));
        }
        return testList;
    }

    private List<TaTestCase> getLocalityTestCases() {
        List<TaTestCase> testList = new ArrayList<TaTestCase>();
        List<Locality> entitylist = getEntityList(
                selectorAllLocality,
                LOCALITY_API_URL,
                Locality.class,
                DefaultPageFetchSize);
        for (Locality x : entitylist) {
            testList.add(new TaTestCase(x.getLabel(), TaTestCaseType.Locality, 1, 3, "TYPEAHEAD-LOCALITY-" + x
                    .getLocalityId()));
        }
        return testList;
    }

    private List<TaTestCase> getProjectTestCases() {
        List<TaTestCase> testList = new ArrayList<TaTestCase>();
        List<Project> entitylist = getEntityList(
                selectorAllProject,
                PROJECT_API_URL,
                Project.class,
                DefaultPageFetchSize);
        for (Project x : entitylist) {
            testList.add(new TaTestCase(x.getName(), TaTestCaseType.Project, 1, 5, "TYPEAHEAD-PROJECT-" + x
                    .getProjectId()));
        }
        return testList;
    }

    private List<TaTestCase> getSuburbTestCases() {
        List<TaTestCase> testList = new ArrayList<TaTestCase>();
        List<Suburb> entitylist = getEntityList(selectorAllSuburb, SUBURB_API_URL, Suburb.class, DefaultPageFetchSize);
        for (Suburb x : entitylist) {
            testList.add(new TaTestCase(x.getLabel(), TaTestCaseType.Suburb, 1, 1, "TYPEAHEAD-SUBURB-" + x.getId()));
        }
        return testList;
    }

    private List<TaTestCase> getBuilderTestCases() {
        List<Builder> entityList = getEntityList(
                selectorAllBuilder,
                BUILDER_API_URL,
                Builder.class,
                DefaultPageFetchSize);
        List<TaTestCase> testList = new ArrayList<TaTestCase>();
        for (Builder x : entityList) {
            testList.add(new TaTestCase(x.getName(), TaTestCaseType.Builder, 1, 1, "TYPEAHEAD-BUILDER-" + x.getId()));
        }
        return testList;
    }

    private <T> List<T> getEntityList(String selector, String API_URL, Class<T> clazz, int pageSize) {
        String entityName = clazz.getSimpleName();
        logger.debug("Fetching entity list for : " + entityName);
        int start = 0, count = pageSize;
        String url = "";
        URI uri = null;
        List<T> entitylist = new ArrayList<T>();
        List<T> templist = null;
        while (true) {
            url = BASE_URL + API_URL + "?" + String.format(selector, start, count);
            logger.debug("Api Url for fetching entity " + entityName + " : [ " + url + "]");
            uri = URI.create(UriComponentsBuilder.fromUriString(url).build().encode().toString());
            templist = httpRequestUtil.getInternalApiResultAsTypeListFromCache(uri, clazz);
            if (templist == null || templist.isEmpty()) {
                break;
            }
            entitylist.addAll(templist);
            start += count;
        }
        logger.debug(entitylist.size() + " Entities recieved for : " + entityName);
        return entitylist;
    }

}
