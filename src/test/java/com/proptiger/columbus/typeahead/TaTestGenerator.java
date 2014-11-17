package com.proptiger.columbus.typeahead;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.proptiger.core.model.cms.Builder;
import com.proptiger.core.model.cms.City;
import com.proptiger.core.model.cms.Locality;
import com.proptiger.core.model.cms.Project;
import com.proptiger.core.model.cms.Suburb;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;

@Component
public class TaTestGenerator {

    private String          BASE_URL             = "";
    private String          CITY_API_URL         = "";
    private String          LOCALITY_API_URL     = "";
    private String          PROJECT_API_URL      = "";
    private String          SUBURB_API_URL       = "";
    private String          BUILDER_API_URL      = "";

    private static int      DefaultPageFetchSize = 999;
    
    @Autowired
    private HttpRequestUtil httpRequestUtil;

    public static String    selectorAllCity      = "selector={\"fields\":[\"cityId\",\"label\"],\"paging\":{\"start\":%s,\"rows\":%s}}";
    public static String    selectorAllLocality  = "selector={\"fields\":[\"localityId\",\"label\"]},\"paging\":{\"start\":%s,\"rows\":%s}}";
    public static String    selectorAllProject   = "selector={\"fields\":[\"projectId\",\"name\",\"locality\",\"suburb\",\"city\",\"label\",\"builder\"],\"paging\":{\"start\":%s,\"rows\":%s}}";
    public static String    selectorAllSuburb    = "selector={\"fields\":[\"id\",\"label\"],\"paging\":{\"start\":%s,\"rows\":%s}}";
    public static String    selectorAllBuilder   = "selector={\"fields\":[\"cityId\",\"label\"],\"paging\":{\"start\":%,\"rows\":%s}}";

    @PostConstruct
    public void initialize() {
        BASE_URL = PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL);
        CITY_API_URL = PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL);
        LOCALITY_API_URL = PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL);
        PROJECT_API_URL = PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL);
        SUBURB_API_URL = PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL);
        BUILDER_API_URL = PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL);
    }

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
        List<Locality> entitylist = getEntityList(selectorAllLocality, LOCALITY_API_URL, Locality.class, DefaultPageFetchSize);
        for (Locality x : entitylist) {
            testList.add(new TaTestCase(x.getLabel(), TaTestCaseType.Locality, 1, 1, "TYPEAHEAD-LOCALITY-" + x
                    .getLocalityId()));
        }
        return testList;
    }

    private List<TaTestCase> getProjectTestCases() {
        List<TaTestCase> testList = new ArrayList<TaTestCase>();
        List<Project> entitylist = getEntityList(selectorAllProject, PROJECT_API_URL, Project.class, DefaultPageFetchSize);
        for (Project x : entitylist) {
            testList.add(new TaTestCase(x.getName(), TaTestCaseType.Project, 1, 1, "TYPEAHEAD-PROJECT-" + x
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
        List<Builder> entityList = getEntityList(selectorAllBuilder, BUILDER_API_URL, Builder.class, DefaultPageFetchSize);
        List<TaTestCase> testList = new ArrayList<TaTestCase>();
        for (Builder x : entityList) {
            testList.add(new TaTestCase(x.getName(), TaTestCaseType.Builder, 1, 1, "TYPEAHEAD-BUILDER-" + x.getId()));
        }
        return testList;
    }

    private <T> List<T> getEntityList(String selector, String API_URL, Class<T> clazz, int pageSize) {
        int start = 0, count = pageSize;
        String url = "";
        URI uri = null;
        List<T> entitylist;
        while (true) {
            url = BASE_URL + String.format(selector, start, count);
            uri = URI.create(UriComponentsBuilder.fromUriString(url).build().encode().toString());
            entitylist = httpRequestUtil.getInternalApiResultAsTypeListFromCache(uri, clazz);
            if (entitylist == null || entitylist.isEmpty()) {
                break;
            }
            start += count;
        }
        return entitylist;
    }

}
