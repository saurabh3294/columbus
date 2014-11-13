package com.proptiger.columbus.typeahead;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriComponentsBuilder;

import com.proptiger.columbus.thandlers.URLGenerationConstants;
import com.proptiger.core.model.cms.Builder;
import com.proptiger.core.model.cms.City;
import com.proptiger.core.model.cms.Locality;
import com.proptiger.core.model.cms.Project;
import com.proptiger.core.model.cms.Suburb;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;

public class TaTestGenerator {

    private String          BASE_URL            = "";
    private String          CITY_API_URL        = "";
    private String          LOCALITY_API_URL    = "";
    private String          PROJECT_API_URL     = "";
    private String          SUBURB_API_URL      = "";
    private String          BUILDER_API_URL     = "";

    private static Logger   logger              = LoggerFactory.getLogger(TaTestGenerator.class);

    @Autowired
    private HttpRequestUtil httpRequestUtil;

    public static String    selectorAllCity     = "selector={\"fields\":[\"cityId\",\"label\"],\"paging\":{\"start\":0,\"rows\":999}}";
    public static String    selectorAllLocality = "selector={\"fields\":[\"localityId\",\"label\"]},\"paging\":{\"start\":0,\"rows\":999}}";
    public static String    selectorAllProject  = "selector={\"fields\":[\"projectId\",\"name\",\"locality\",\"suburb\",\"city\",\"label\",\"builder\"],\"paging\":{\"start\":0,\"rows\":1000}}";
    public static String    selectorAllSuburb   = "selector={\"fields\":[\"id\",\"label\"],\"paging\":{\"start\":0,\"rows\":999}}";
    public static String    selectorAllBuilder  = "selector={\"fields\":[\"cityId\",\"label\"],\"paging\":{\"start\":0,\"rows\":999}}";

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

    public List<TaTestCase> getCityTestCases() {
        List<TaTestCase> testList = new ArrayList<TaTestCase>();
        String url = BASE_URL + CITY_API_URL + selectorAllCity;
        URI uri = URI.create(UriComponentsBuilder.fromUriString(url).build().encode().toString());
        List<City> entitylist = httpRequestUtil.getInternalApiResultAsTypeListFromCache(uri, City.class);
        for (City x : entitylist) {
            testList.add(new TaTestCase(x.getLabel(), TaTestCaseType.City, 1, 1, "TYPEAHEAD-CITY-" + x.getId()));
        }
        return testList;
    }

    public List<TaTestCase> getLocalityTestCases() {
        List<TaTestCase> testList = new ArrayList<TaTestCase>();
        String url = BASE_URL + LOCALITY_API_URL + selectorAllLocality;
        URI uri = URI.create(UriComponentsBuilder.fromUriString(url).build().encode().toString());
        List<Locality> entitylist = httpRequestUtil.getInternalApiResultAsTypeListFromCache(uri, Locality.class);
        for (Locality x : entitylist) {
            testList.add(new TaTestCase(x.getLabel(), TaTestCaseType.Locality, 1, 1, "TYPEAHEAD-LOCALITY-" + x
                    .getLocalityId()));
        }
        return testList;
    }

    private List<TaTestCase> getProjectTestCases() {
        List<TaTestCase> testList = new ArrayList<TaTestCase>();
        String url = BASE_URL + PROJECT_API_URL + selectorAllProject;
        URI uri = URI.create(UriComponentsBuilder.fromUriString(url).build().encode().toString());
        List<Project> entitylist = httpRequestUtil.getInternalApiResultAsTypeListFromCache(uri, Project.class);
        for (Project x : entitylist) {
            testList.add(new TaTestCase(x.getName(), TaTestCaseType.Project, 1, 1, "TYPEAHEAD-PROJECT-" + x
                    .getProjectId()));
        }
        return testList;
    }

    private List<TaTestCase> getSuburbTestCases() {
        List<TaTestCase> testList = new ArrayList<TaTestCase>();
        String url = BASE_URL + SUBURB_API_URL + selectorAllSuburb;
        URI uri = URI.create(UriComponentsBuilder.fromUriString(url).build().encode().toString());
        List<Suburb> entitylist = httpRequestUtil.getInternalApiResultAsTypeListFromCache(uri, Suburb.class);
        for (Suburb x : entitylist) {
            testList.add(new TaTestCase(x.getLabel(), TaTestCaseType.Suburb, 1, 1, "TYPEAHEAD-SUBURB-" + x.getId()));
        }
        return testList;
    }

    public List<TaTestCase> getBuilderTestCases() {
        List<TaTestCase> testList = new ArrayList<TaTestCase>();
        String url = BASE_URL + BUILDER_API_URL + selectorAllBuilder;
        URI uri = URI.create(UriComponentsBuilder.fromUriString(url).build().encode().toString());
        List<Builder> entitylist = httpRequestUtil.getInternalApiResultAsTypeListFromCache(uri, Builder.class);
        for (Builder x : entitylist) {
            testList.add(new TaTestCase(x.getName(), TaTestCaseType.Builder, 1, 1, "TYPEAHEAD-BUILDER-" + x.getId()));
        }
        return testList;
    }
}
