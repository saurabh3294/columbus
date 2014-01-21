package com.proptiger.data.model;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.meta.ResourceMetaInfo;

@ResourceMetaInfo
@JsonFilter("fieldFilter")
@JsonInclude(Include.NON_NULL)
public class B2b implements BaseModel  {
    private String id;
	
    @Field("PROJECT_ID")
    private int projectId;
    
    @Field("PROJECT_NAME")
    private String projectName;
    
    @Field("COUNTRY_ID")
    private int countryId;
    
    @Field("COUNTRY_NAME")
    private String countryName;
    
    @Field("PHASE_ID")
    private int phaseId;
    
    @Field("PHASE_NAME")
    private String phaseName;
    
    @Field("PHASE_Type")
    private String phaseType;
    
    @Field("LOCALITY_ID")
    private int localityId;
    
    @Field("LOCALITY_NAME")
    private String localityName;
    
    @Field("SUBURB_ID")
    private int suburbId;
    
    @Field("SUBURB_NAME")
    private String suburbName;
    
    @Field("CITY_ID")
    private int cityId;
    
    @Field("CITY_NAME")
    private String cityName;

    @Field("BUILDER_ID")
    private int builderId;
    
    @Field("BUILDER_NAME")
    private String builderName;

    @Field("EFFECTIVE_MONTH")
    private Date effectiveMonth;
    
    @Field("PROMISED_COMPLETION_DATE")
    private Date promisedCompletionDate;
    
    @Field("LAUNCH_DATE")
    private Date launchDate;
    
    @Field("UNIT_TYPE")
    private String unitType;
    
    @Field("AVERAGE_PRICE_PER_UNIT_AREA")
    private int averagePricePerUnitAres;
    
    @Field("AVERAGE_SIZE")
    private int averageSize;
    
//    @Field("ALL_SIZE")
//    private int[] allSize;
    
    @Field("AVERAGE_TOTAL_PRICE")
    private long averageTotalPrice;
    
    @Field("SUPPLY")
    private int supply;
    
    @Field("LAUNCHED_UNIT")
    private int launchedUnit;
    
    @Field("INVENTORY")
    private int inventory;
    
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
}
