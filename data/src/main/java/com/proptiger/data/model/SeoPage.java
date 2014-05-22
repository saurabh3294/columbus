package com.proptiger.data.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonFilter("fieldFilter")
@JsonInclude(Include.NON_NULL)
public class SeoPage extends BaseModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6065883078087404621L;
	
	private String title;
	private String description;
	private String 	keywords;
	private String 	H1;
	private String 	H2;
	private String 	H3;
	private String 	H4;
	
	public enum SeoPageJsonFiles{
		CityOverviewPage("seo-page/city-overview-page.json"),
		CityListingPage("seo-page/city-listing-page.json"),
		LocalityOverviewPage("seo-page/locality-overview-page.json"),
		LocalityListingPage("seo-page/locality-listing-page.json"),
		SuburbListingPage("seo-page/suburb-listing-page.json"),
		ProjectPage("seo-page/project-page.json"),
		BuilderPage("seo-page/builder-page.json"),
		AllBuildersInCityPage("seo-page/all-builders-in-city-page.json"),
		CityBuilderPage("seo-page/city-builder-page.json");
		
		private String filePath;

		SeoPageJsonFiles(String filePath){
			this.filePath=filePath;
		}
		
		public String getFilePath(SeoPageJsonFiles js){
			return js.filePath;
		}
	}
	
	public enum tokens{
		Locality("<Locality>"),
		City("<City>"),
		Suburb("<Suburb>"),
		BuiderName("<Builder Name>"),
		ProjectName("<Project Name>"),
		BHK("<BHK>");
		
		private String value;
		
		tokens( String value){
			this.value=value;
		}
		
		public String getToken(tokens token){
			return token.value;
		}
	}
	
	
	
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getH1() {
		return H1;
	}
	public void setH1(String h1) {
		H1 = h1;
	}
	public String getH2() {
		return H2;
	}
	public void setH2(String h2) {
		H2 = h2;
	}
	public String getH3() {
		return H3;
	}
	public void setH3(String h3) {
		H3 = h3;
	}
	public String getH4() {
		return H4;
	}
	public void setH4(String h4) {
		H4 = h4;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	

}
