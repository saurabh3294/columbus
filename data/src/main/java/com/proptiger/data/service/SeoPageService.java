package com.proptiger.data.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.proptiger.data.model.City;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.SeoPage;
import com.proptiger.data.model.SeoPage.SeoPageJsonFiles;
import com.proptiger.data.model.SeoPage.tokens;
import com.proptiger.data.model.Suburb;
import com.proptiger.data.model.Builder;
import com.proptiger.data.model.URLDetail;
import com.proptiger.data.pojo.Selector;
import com.proptiger.exception.ProAPIException;

@Service
public class SeoPageService {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private CityService cityService;

	@Autowired
	private LocalityService localityService;

	@Autowired
	private SuburbService suburbService;

	@Autowired
	private BuilderService builderService;

	@Autowired
	private PropertyService propertyService;
	
	@Autowired 
	private ApplicationContext appContext;

	private SeoPage getSeoPageObjectFromJson(InputStream in)
			throws FileNotFoundException {
		Gson gson = new Gson();
		BufferedReader buf = new BufferedReader(new InputStreamReader(in));
		return gson.fromJson(buf, SeoPage.class);
	}

	
	/* This function (choosePage) chooses the function to be called depending upon 
	 * the PAGETYPE of the given URLDETAIL*/
	public SeoPage choosePage(URLDetail urlDetail) throws FileNotFoundException {

		switch (urlDetail.getPageType()) {
		case PROJECT:
			return getProjectPageSeo(urlDetail.getProjectId());
		case CITY_LISTING:
			return getCityListingPageSeo(urlDetail.getCityName());
		case CITY_OVERVIEW:
			return getCityOverviewPageSeo(urlDetail.getCityName());
		case LOCALITY_LISTING:
			return getLocalityListingPageSeo(urlDetail.getLocalityId());
		case LOCALITY_OVERVIEW:
			return getLocalityOverviewPageSeo(urlDetail.getLocalityId());
		case SUBURB_LISTING:
			return getSuburbListingPageSeo(urlDetail.getSuburbId());
		case BUILDER:
			return getBuilderNamePageSeo(urlDetail.getBuilderId());
		case ALL_BUILDERS_IN_A_CITY:
			return getAllBuildersInCityPageSeo(urlDetail.getCityName());
		case CITY_BUILDER_PAGE:
			return getCityBuilderPageSeo(urlDetail.getCityName(),urlDetail.getBuilderId());
		default:
			return null;
		}

	}

	
	/* This function(getCityOverviewPageSeo()) gets the CITY object using CITYID, creates a SEOPAGE object from
	 *  the json file of the page and passes them to mapCityTemplate()*/
	public SeoPage getCityOverviewPageSeo(String cityName)
			throws FileNotFoundException {
		City city = cityService.getCity(cityName);
		SeoPageJsonFiles jfile = SeoPageJsonFiles.CityOverviewPage;
		SeoPage seoPage = readFile(jfile);
		mapCityTemplate(seoPage, city);
		System.out.println(new Gson().toJson(seoPage));
		return seoPage;
	}

	
	public SeoPage getCityListingPageSeo(String cityName)
			throws FileNotFoundException {
		City city = cityService.getCity(cityName);
		SeoPageJsonFiles jfile = SeoPageJsonFiles.CityListingPage;
		SeoPage seoPage = readFile(jfile);
		mapCityTemplate(seoPage, city);
		System.out.println(new Gson().toJson(seoPage));
		return seoPage;
	}

	public SeoPage getLocalityOverviewPageSeo(int localityId)
			throws FileNotFoundException {
		Locality locality = localityService.getLocality(localityId);
		int suburbId = locality.getSuburbId();
		Suburb suburb = suburbService.getSuburb(suburbId);
		int cityId = suburb.getCityId();
		City city = cityService.getCity(cityId);
		SeoPageJsonFiles jfile = SeoPageJsonFiles.LocalityOverviewPage;
		
		SeoPage seoPage = readFile(jfile);
		mapLocalityTemplate(seoPage, locality, city);
		System.out.println(new Gson().toJson(seoPage));
		return seoPage;
	}

	public SeoPage getLocalityListingPageSeo(int localityId)
			throws FileNotFoundException {
		Locality locality = localityService.getLocality(localityId);
		int suburbId = locality.getSuburbId();
		Suburb suburb = suburbService.getSuburb(suburbId);
		int cityId = suburb.getCityId();
		City city = cityService.getCity(cityId);
		SeoPageJsonFiles jfile = SeoPageJsonFiles.LocalityListingPage;
		
		SeoPage seoPage = readFile(jfile);
		mapLocalityTemplate(seoPage, locality, city);
		System.out.println(new Gson().toJson(seoPage));
		return seoPage;
	}

	public SeoPage getSuburbListingPageSeo(int suburbId)
			throws FileNotFoundException {
		Suburb suburb = suburbService.getSuburb(suburbId);
		int cityId = suburb.getCityId();
		City city = cityService.getCity(cityId);
		SeoPageJsonFiles jfile = SeoPageJsonFiles.SuburbListingPage;
		
		SeoPage seoPage = readFile(jfile);
		mapSuburbTemplate(seoPage, suburb, city);
		System.out.println(new Gson().toJson(seoPage));
		return seoPage;
	}

	public SeoPage getProjectPageSeo(int projectId)
			throws FileNotFoundException {
		String j = "{\"fields\":[\"distinctBedrooms\"]}";
		Gson gson = new Gson();
		Selector selector = gson.fromJson(j, Selector.class);

		Project project = projectService.getProjectInfoDetails(selector,
				projectId);
		Set<Integer> distinctBedrooms = new HashSet<>();
		distinctBedrooms = project.getDistinctBedrooms();
		String bedrooms = distinctBedrooms.toString() + " BHK";
		if (bedrooms.contains("0"))//add condition
			bedrooms = "";
		bedrooms = bedrooms.replace("[", "");
		bedrooms = bedrooms.replace("]", "");
		System.out.println(bedrooms);
		Locality locality = project.getLocality();
		Suburb suburb = locality.getSuburb();
		City city = suburb.getCity();
		SeoPageJsonFiles jfile = SeoPageJsonFiles.ProjectPage;
		
		SeoPage seoPage = readFile(jfile);
		mapProjectTemplate(seoPage, project, locality, city, bedrooms);
		System.out.println(new Gson().toJson(seoPage));
		return seoPage;
	}

	public SeoPage getBuilderNamePageSeo(int builderId)
			throws FileNotFoundException {
		Builder builder = builderService.getBuilderById(builderId);
		SeoPageJsonFiles jfile = SeoPageJsonFiles.BuilderPage;
		SeoPage seoPage = readFile(jfile);
		mapBuilderTemplate(seoPage, builder);
		System.out.println(new Gson().toJson(seoPage));
		return seoPage;
	}

	public SeoPage getAllBuildersInCityPageSeo(String cityName)
			throws FileNotFoundException {
		City city = cityService.getCity(cityName);
		SeoPageJsonFiles jfile = SeoPageJsonFiles.AllBuildersInCityPage;
		SeoPage seoPage = readFile(jfile);
		mapCityTemplate(seoPage, city);
		System.out.println(new Gson().toJson(seoPage));
		return seoPage;
	}
	
	public SeoPage getCityBuilderPageSeo(String cityName, int builderId){
		City city = cityService.getCity(cityName);
		Builder builder = builderService.getBuilderById(builderId);
		SeoPageJsonFiles jfile = SeoPageJsonFiles.CityBuilderPage;
		SeoPage seoPage = readFile(jfile);
		mapCityBuilderTemplate(seoPage,city,builder);
		System.out.println(new Gson().toJson(seoPage));
		return seoPage;
	}

	/*This function(mapCityTemplate()) takes SEOPAGE and CITY objects , creates a MAPPING of the tokens with their
	 *values. Then replace the tokens with their values using replace() function and sets the fields of SEOPAGE. */
	private void mapCityTemplate(SeoPage seopage, City city) {
		tokens token = tokens.City;

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

	private void mapLocalityTemplate(SeoPage seopage, Locality locality,
			City city) {
		tokens token1 = tokens.Locality;
		tokens token2 = tokens.City;

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

		tokens token1 = tokens.Suburb;
		tokens token2 = tokens.City;

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

	private void mapProjectTemplate(SeoPage seopage, Project project,
			Locality locality, City city, String bedrooms) {
		tokens token1 = tokens.ProjectName;
		tokens token2 = tokens.Locality;
		tokens token3 = tokens.City;
		tokens token4 = tokens.BHK;

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
		tokens token = tokens.BuiderName;

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

	
	private void mapCityBuilderTemplate(SeoPage seopage,City city,Builder builder){
		tokens token1 = tokens.City;
		tokens token2 =tokens.BuiderName;
		
		HashMap<String, String> mappings = new HashMap<String, String>();
		mappings.put(token1.getToken(token1),city.getLabel());
		mappings.put(token2.getToken(token2), builder.getName());
		
		seopage.setTitle(replace(seopage.getTitle(), mappings));
		seopage.setDescription(replace(seopage.getDescription(), mappings));
		seopage.setKeywords(replace(seopage.getKeywords(), mappings));
		seopage.setH1(replace(seopage.getH1(), mappings));
		seopage.setH2(replace(seopage.getH2(), mappings));
		seopage.setH3(replace(seopage.getH3(), mappings));
		seopage.setH4(replace(seopage.getH4(), mappings));
		
	}
	
	/*This function(replace()) replaces the tokens in the TEXT with their values using the MAPPING*/
	private String replace(String text, HashMap<String, String> mappings) {
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

	private SeoPage readFile(SeoPageJsonFiles jfile){
		String filepath = jfile.getFilePath(jfile);
		Resource resource = appContext.getResource("classpath:/"+filepath);
		SeoPage seoPage;
		try {
			seoPage = getSeoPageObjectFromJson(resource.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new ProAPIException(e);
		}
		return seoPage;
	}
}
