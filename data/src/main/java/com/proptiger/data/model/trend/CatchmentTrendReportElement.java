package com.proptiger.data.model.trend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.proptiger.data.annotations.TrendReportColumn;

public class CatchmentTrendReportElement {

    public static enum TypeOfData {
        Sales, AvailableInventory, Price
    }

    private String                                             projectName;

    private String                                             builderName;

    private String                                             phaseName;

    private String                                             launchDate;

    private String                                             completionDate;

    @TrendReportColumn(name = "micromarket")
    private String                                             locality;

    private Double                                             latitude;

    private Double                                             longitude;

    private String                                             projectStatus;

    private int                                                projectArea;

    private int                                                launchPrice;

    private int                                                bhk;

    private String                                             bhkSizeRange;

    private int                                                totalUnits;

    private int                                                launchedUnits;

    private Map<TypeOfData, Map<String, Object>> bhkGroupedMap = new HashMap<TypeOfData, Map<String, Object>>();

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getBuilderName() {
        return builderName;
    }

    public void setBuilderName(String builderName) {
        this.builderName = builderName;
    }

    public String getPhaseName() {
        return phaseName;
    }

    public void setPhaseName(String phaseName) {
        this.phaseName = phaseName;
    }

    public String getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(String launchDate) {
        this.launchDate = launchDate;
    }

    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    public int getProjectArea() {
        return projectArea;
    }

    public void setProjectArea(int projectArea) {
        this.projectArea = projectArea;
    }

    public int getLaunchPrice() {
        return launchPrice;
    }

    public void setLaunchPrice(int launchPrice) {
        this.launchPrice = launchPrice;
    }

    public int getBhk() {
        return bhk;
    }

    public void setBhk(int bhk) {
        this.bhk = bhk;
    }

    public String getBhkSizeRange() {
        return bhkSizeRange;
    }

    public void setBhkSizeRange(String bhkSizeRange) {
        this.bhkSizeRange = bhkSizeRange;
    }

    public int getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(int totalUnits) {
        this.totalUnits = totalUnits;
    }

    public int getLaunchedUnits() {
        return launchedUnits;
    }

    public void setLaunchedUnits(int launchedUnits) {
        this.launchedUnits = launchedUnits;
    }

    public Map<TypeOfData, Map<String, Object>> getBhkGroupedMap() {
        return bhkGroupedMap;
    }

    public List<List<Object>> getReportRows(List<String> monthlist) {
        List<List<Object>> reportRows = new ArrayList<List<Object>>();
        List<Object> commonData = Arrays.asList(new Object[]{projectName,
        builderName,
        phaseName,
        launchDate,
        completionDate,
        locality,
        latitude,
        longitude,
        projectStatus,
        projectArea,
        launchPrice,
        bhk,
        bhkSizeRange,
        totalUnits,
        launchedUnits,
        bhk});
        
        reportRows = Collections.nCopies(TypeOfData.values().length, commonData);
        
        for(List<Object> reportRow : reportRows){
            reportRow = new ArrayList<Object>(reportRow);
            for(TypeOfData tod : TypeOfData.values()){
                reportRow.add(tod.name());
                for(String month : monthlist){
                    reportRow.add(bhkGroupedMap.get(tod).get(month));
                }
            }
        }
        
        return reportRows;
    }

    public static List<Object[]> getReportColumns(List<String> monthList) {
        Object[][] columns = {{"Project Name", String.class},
                {"Builder Name", String.class},
                {"Phase Name", String.class},
                {"Launch Date", String.class},
                {"Completion Date", String.class},
                {"Locality", String.class},
                {"Latitude", String.class},
                {"Longitude", String.class},
                {"Project Status", String.class},
                {"Project Area", String.class},
                {"Launch Price", String.class},
                {"BHK", String.class},
                {"BHK Size Range", String.class},
                {"Total Units", String.class},
                {"Type Of Data", String.class},
        };
        List<Object[]> columnList = new ArrayList<Object[]>(Arrays.asList(columns));
        for(String month : monthList){
            columnList.add(new Object[]{month, Integer.class});
        }
        
        return columnList;
    }

}
