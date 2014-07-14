/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.Locality;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.Typeahead;
import com.proptiger.data.repo.TypeaheadDao;
import com.proptiger.data.enums.Suggestions;

/**
 * @author mukand
 * @author Hemendra
 * 
 * 
 */

@Service
public class TypeaheadService {
	@Autowired
	private TypeaheadDao typeaheadDao;
	private LocalityService localityService;

	/**
	 * This method will return the list of typeahead results based on the
	 * params.
	 * 
	 * @param query
	 * @param rows
	 * @param filterQueries
	 * @return List<Typeahead>
	 */

/*	@Autowired
	private Suggestions sgstn;*/
	
	public List<Typeahead> getTypeaheads(String query, int rows,
			List<String> filterQueries) {
		return typeaheadDao.getTypeaheads(query, rows, filterQueries);
	}

	public List<Typeahead> getExactTypeaheads(String query, int rows,
			List<String> filterQueries) {
		return typeaheadDao.getExactTypeaheads(query, rows, filterQueries);
	}

	public List<Typeahead> getTypeaheadsV2(String query, int rows,
			List<String> filterQueries) {
		List<Typeahead> results = new ArrayList<Typeahead>();

		results = typeaheadDao.getTypeaheadsV2(query, rows, filterQueries);
		
		//Code block to be commented if you don't want the recommendations to surface 
		List<Typeahead> suggestions = new ArrayList<Typeahead>();
		suggestions = auxilliaryService(results);
		results.addAll(suggestions);
		
		String city = "Noida";
		matchTemplate(query,filterQueries, city);
		
		return results;
	}

	public List<Typeahead> auxilliaryService(List<Typeahead> results) {

		String text = results.get(0).getId();
		List<String> tokens = new ArrayList<String>();
		List<Typeahead> suggestions = new ArrayList<Typeahead>();

		Pattern pattern = Pattern.compile("([\\w]+)");
		Matcher matcher = pattern.matcher(text);
		String redirectUrl = results.get(0).getRedirectUrl();
		String label = results.get(0).getLabel();
		
		while (matcher.find()) {
			tokens.add(matcher.group(1).toUpperCase());//2nd element is docType, 3rd is id number
			System.out.println(matcher.group(1).toString());
		}

		if (tokens != null) {

			switch (tokens.get(1)) {
			case "PROJECT":
				suggestions = ProjectSuggestion(tokens);
				break;
			case "CITY":
				suggestions = CitySuggestion(tokens,redirectUrl,label);
				break;
			case "BUILDER":
				suggestions = BuilderSuggestion(tokens,redirectUrl,label);
			case "LOCALITY":
					suggestions = LocalitySuggestion(tokens, redirectUrl, label);
				break;
			default:
				break;
			}
		}
		
		return suggestions;
	}
	
	public List<Typeahead> ProjectSuggestion(List<String> tokens){
		
		List<Typeahead> recommendation = new ArrayList<Typeahead>();
		List<Property> properties = new ArrayList<Property>();
		List<String> filters = new ArrayList<String>();
		List<Integer> distinctBedrooms = new ArrayList<Integer>();
		List<String> redirectUrls = new ArrayList<String>();
		String projectName = new String();
		String query = new String();
		QueryResponse response = new QueryResponse();
		
		query = "PROJECT_ID:" + tokens.get(2);
		filters.add("DOCUMENT_TYPE:PROPERTY");
		
		response=typeaheadDao.auxilliary(query, 50, filters);
		properties = response.getBeans(Property.class);
		projectName = properties.get(0).getProjectName();
		
		//iterate over all the properties and pick distinct bedrooms along with their urls
		if(properties!=null){
			projectName = properties.get(0).getProjectName();
			for(Property p: properties){
				if(!distinctBedrooms.contains(p.getBedrooms())){
					distinctBedrooms.add(p.getBedrooms());
					redirectUrls.add(p.getURL());
				}
			}
		}
		
		//Insert them in the Typeahead objects
		for (int i=0;i<redirectUrls.size();i++){
			Typeahead obj = new Typeahead();
			obj.setRedirectUrl(redirectUrls.get(i));
			String displayText = distinctBedrooms.get(i).toString()+Suggestions.PROJECT.getVal1()+projectName;
			obj.setDisplayText(displayText);
			recommendation.add(obj);
		}

		
		return recommendation;
	}
	
	public List<Typeahead> BuilderSuggestion(List<String> tokens,String redirectUrl,String builderName){
		List<Typeahead> recommendation = new ArrayList<Typeahead>();
		Typeahead obj = new Typeahead();
		redirectUrl += "/filters?projectStatus=not%20launched,pre%20launch";
		String displayText = Suggestions.BUILDER.getVal1()+builderName;
		obj.setRedirectUrl(redirectUrl);
		obj.setDisplayText(displayText);
		recommendation.add(obj);
		return recommendation;
	}
	
	public List<Typeahead> CitySuggestion(List<String> tokens, String redirectUrl, String cityName){
		List<Typeahead> recommendation = new ArrayList<Typeahead>();
		String newUrl;
		Typeahead obj1 = new Typeahead();
		newUrl =redirectUrl +"/filters?projectStatus=not%20launched,pre%20launch";
		String displayText = Suggestions.CITY.getVal1()+cityName;
		obj1.setRedirectUrl(newUrl);
		obj1.setDisplayText(displayText);
		recommendation.add(obj1);
		
		Typeahead obj2 = new Typeahead();
		newUrl = redirectUrl+"/filters?budget=0,5000000";
		displayText = Suggestions.LOCALITY.getVal1()+cityName;
		obj2.setRedirectUrl(newUrl);
		obj2.setDisplayText(displayText);
		recommendation.add(obj2);

		Typeahead obj3 = new Typeahead();
		newUrl=redirectUrl+ "/filters?budget=10000000,";
		displayText = Suggestions.LOCALITY.getVal2()+cityName;
		obj3.setRedirectUrl(newUrl);
		obj3.setDisplayText(displayText);
		recommendation.add(obj3);
		
		return recommendation;
	}
	
	public List<Typeahead> LocalitySuggestion(List<String> tokens, String redirectUrl,String localityName){
		List<Typeahead> recommendation = new ArrayList<Typeahead>();
		Typeahead obj1 = new Typeahead();
		Typeahead obj2 = new Typeahead();
		String newUrl;

			newUrl=redirectUrl +"/filters?budget=0,5000000";
			String displayText = Suggestions.LOCALITY.getVal1()+localityName;
			obj1.setRedirectUrl(newUrl);
			obj1.setDisplayText(displayText);
			recommendation.add(obj1);

			newUrl =redirectUrl+"/filters?budget=10000000,";
			displayText = Suggestions.LOCALITY.getVal2()+localityName;
			obj2.setRedirectUrl(newUrl);
			obj2.setDisplayText(displayText);
			recommendation.add(obj2);			
			
		return recommendation;
	}
	
	
	public List<Typeahead> matchTemplate(String query, List<String> queryFilters, String city){
		//Generate query to fetch the template from the solr
		List<Typeahead> matched = new ArrayList<Typeahead>(); 
		List<Typeahead> results = new ArrayList<Typeahead>(); 
		List<String> displayTexts = new ArrayList<String>();
		List<String> redirectUrls = new ArrayList<String>();
		List<Locality> topLocalities = new ArrayList<Locality>();
		
		queryFilters.add("TYPEAHEAD_TYPE:TEMPLATE");
		QueryResponse response = typeaheadDao.auxilliary(query,1,queryFilters);
		matched = response.getBeans(Typeahead.class);
		//generate the displaytext and urls
		displayTexts.add(matched.get(0).getTemplateText()+city);
		redirectUrls.add(city.toLowerCase()+"-real-estate");
		
		topLocalities = localityService.getLocalities(null);
		for(int i=0;i<2;i++){
			redirectUrls.add(topLocalities.get(i).getUrl());
			displayTexts.add(matched.get(0).getTemplateText()+topLocalities.get(i).getLabel());
		}

		for (int i=0;i<3;i++){
			Typeahead obj = new Typeahead();
			obj.setDisplayText(displayTexts.get(i));
			obj.setRedirectUrl(redirectUrls.get(i));
			results.add(obj);
		}
		return matched;
	}
	
}
