package com.proptiger.data.model.trend;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatchmentTrendReportElement implements Serializable {

    private static final long serialVersionUID = 1L;

    public static enum TypeOfData {
        Sales, AvailableInventory, Price, LtdSupply
    }

    private String                             projectName;

    private String                             builderName;

    private String                             phaseName;

    private Date                               launchDate;

    private Date                               completionDate;

    private String                             locality;

    private Double                             latitude;

    private Double                             longitude;

    private String                             projectStatus;

    private Double                             projectArea;

    private int                                launchPrice;

    private int                                bhk;

    private String                             bhkSizeRange;

    private Integer                            totalUnits;

    private Integer                            launchedUnits;

    private String                             projectBhkSizeRange;

    private Map<TypeOfData, Map<Date, Object>> bhkGroupedMap = new HashMap<TypeOfData, Map<Date, Object>>();

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

    public Date getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(Date launchDate) {
        this.launchDate = launchDate;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
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

    public Double getProjectArea() {
        return projectArea;
    }

    public void setProjectArea(Double projectArea) {
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

    public Integer getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(Integer totalUnits) {
        this.totalUnits = totalUnits;
    }

    public Integer getLaunchedUnits() {
        return launchedUnits;
    }

    public void setLaunchedUnits(Integer launchedUnits) {
        this.launchedUnits = launchedUnits;
    }

    public String getProjectBhkSizeRange() {
        return projectBhkSizeRange;
    }

    public void setProjectBhkSizeRange(String projectBhkSizeRange) {
        this.projectBhkSizeRange = projectBhkSizeRange;
    }

    public Map<TypeOfData, Map<Date, Object>> getBhkGroupedMap() {
        return bhkGroupedMap;
    }

    private String getBhkDisplay(int bhk) {
        return ((bhk == -1) ? "ALL" : String.valueOf(bhk));
    }

    public List<List<Object>> getReportRows(List<Date> monthlist) {
        List<List<Object>> reportRows = new ArrayList<List<Object>>();
        List<Object> commonData = Arrays.asList(new Object[] {
                projectName,
                builderName,
                phaseName,
                getDateDisplay(launchDate),
                getDateDisplay(completionDate),
                locality,
                latitude,
                longitude,
                projectStatus,
                projectArea,
                launchPrice,
                getBhkDisplay(bhk),
                bhkSizeRange,
                totalUnits,
                launchedUnits });

        List<Object> reportRow;
        TypeOfData[] todValues = { TypeOfData.Sales, TypeOfData.AvailableInventory, TypeOfData.Price };
        for (TypeOfData tod : todValues) {
            reportRow = new ArrayList<Object>(commonData);
            reportRow.add(tod.name());
            for (Date month : monthlist) {
                reportRow.add(bhkGroupedMap.get(tod).get(month));
            }
            reportRows.add(reportRow);
        }

        return reportRows;
    }
    
    private String getDateDisplay(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return (date == null ? "null" : sdf.format(date));
    }

    public static List<Object[]> getReportColumns(List<Date> monthList) {
        Object[][] columns = {
                { "Project Name", String.class },
                { "Builder Name", String.class },
                { "Phase Name", String.class },
                { "Launch Date", String.class },
                { "Completion Date", String.class },
                { "Locality", String.class },
                { "Latitude", Double.class },
                { "Longitude", Double.class },
                { "Project Status", String.class },
                { "Project Area", Integer.class },
                { "Launch Price", Double.class },
                { "BHK", String.class },
                { "BHK Size Range", String.class },
                { "Total Units", Integer.class },
                { "Launched Units", Integer.class },
                { "Type Of Data", String.class }, };
        List<Object[]> columnList = new ArrayList<Object[]>(Arrays.asList(columns));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Date month : monthList) {
            columnList.add(new Object[] { sdf.format(month.getTime()), Double.class });
        }

        return columnList;
    }

}
