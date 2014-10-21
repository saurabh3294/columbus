package com.proptiger.data.repo.trend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proptiger.data.enums.filter.Operator;
import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.model.Catchment;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.trend.CatchmentTrendReportElement;
import com.proptiger.data.model.trend.Trend;
import com.proptiger.data.model.trend.CatchmentTrendReportElement.TypeOfData;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.PropertyService;
import com.proptiger.data.service.trend.TrendService;
import com.proptiger.data.service.user.CatchmentService;
import com.proptiger.data.util.UtilityClass;
import com.proptiger.exception.ProAPIException;

@Repository
public class TrendReportDao {

    @Autowired
    private TrendService     trendService;

    @Autowired
    private CatchmentService catchmentService;

    @Autowired
    private PropertyService  propertyService;

    @SuppressWarnings("unchecked")
    public List<CatchmentTrendReportElement> getCatchmentTrendReport(
            Integer catchmentId,
            FIQLSelector selector,
            ActiveUser userInfo) {

        List<Catchment> catchmentList = catchmentService.getCatchment(new FIQLSelector()
                .addAndConditionToFilter("id==" + catchmentId));
        if (catchmentList.isEmpty()) {
            throw new ProAPIException("Invalid Catchment ID");
        }

        /** Step 1 : Fetch Information from Other APIs */

        Catchment catchment = catchmentList.get(0);
        List<Integer> projectIdList = catchment.getProjectIds();
        Map<Integer, AdditionalInfo> mapPidToAdditionInfo = getAdditionalInfo(projectIdList);

        /** Step 2 : Fetch Information from TREND APIs **/

        selector.addAndConditionToFilter(catchmentService.getCatchmentFIQLFilter(catchmentId, userInfo));
        List<Trend> trendList = trendService.getTrend(selector);
        List<CatchmentTrendReportElement> ctrElemList = new ArrayList<CatchmentTrendReportElement>();

        /* Get trend list as grouped map */

        String[] groupFields = { "projectId", "phaseId", "bedrooms" };
        Map<Integer, Object> projectGroupedTrend = null;
        Map<Integer, Object> phaseGroupedTrend = null;
        Map<Integer, Object> groupedTrendList = (Map<Integer, Object>) UtilityClass.groupFieldsAsPerKeys(
                trendList,
                Arrays.asList(groupFields));

        /* Get Report Element List */

        CatchmentTrendReportElement ctrElem;
        for (int projectId : groupedTrendList.keySet()) {
            projectGroupedTrend = (Map<Integer, Object>) groupedTrendList.get(projectId);
            for (int phaseId : projectGroupedTrend.keySet()) {
                phaseGroupedTrend = (Map<Integer, Object>) projectGroupedTrend.get(phaseId);
                for (int bedrooms : phaseGroupedTrend.keySet()) {
                    ctrElem = getCatchmentTrendReportElement(
                            projectId,
                            phaseId,
                            bedrooms,
                            (List<Trend>) phaseGroupedTrend.get(bedrooms),
                            mapPidToAdditionInfo);
                    ctrElemList.add(ctrElem);
                }
            }
        }

        return ctrElemList;
    }

    private Map<Integer, AdditionalInfo> getAdditionalInfo(List<Integer> projectIdList) {

        Selector selector = new Selector();

        /* Adding filter for project IDs */
        Map<String, List<Map<String, Map<String, Object>>>> filter = new HashMap<String, List<Map<String, Map<String, Object>>>>();
        List<Map<String, Map<String, Object>>> list = new ArrayList<>();
        Map<String, Map<String, Object>> searchType = new HashMap<>();
        Map<String, Object> filterCriteria = new HashMap<>();

        filterCriteria.put("projectId", projectIdList);
        searchType.put(Operator.equal.name(), filterCriteria);
        list.add(searchType);
        filter.put(Operator.and.name(), list);
        selector.setFilters(filter);

        /* Adding Fields */

        Set<String> fields = new HashSet<String>();
        fields.add("projectId");
        fields.add("projectName");
        fields.add("latitude");
        fields.add("longitude");
        fields.add("bedrooms");
        fields.add("size");
        selector.setFields(fields);

        List<Project> projectList = propertyService.getPropertiesGroupedToProjects(selector).getResults();

        Map<Integer, AdditionalInfo> mapPidToAdditionInfo = new HashMap<Integer, AdditionalInfo>();
        AdditionalInfo additionalInfo;
        for (Project project : projectList) {
            additionalInfo = new AdditionalInfo();
            additionalInfo.laitude = project.getLatitude();
            additionalInfo.longitude = project.getLongitude();
            additionalInfo.mapPidToBhkRange = getProjectBhkSizeRangeMap(project);
            mapPidToAdditionInfo.put(project.getProjectId(), additionalInfo);
        }

        return mapPidToAdditionInfo;
    }

    /* This methods assumes that property list is sorted size-wise */
    private Map<Integer, List<Double>> getProjectBhkSizeRangeMap(Project project) {
        Map<Integer, List<Double>> map = new HashMap<Integer, List<Double>>();
        List<Property> propertyList = project.getProperties();
        int bedrooms;
        for (Property property : propertyList) {
            bedrooms = property.getBedrooms();
            if (!map.containsKey(bedrooms)) {
                map.put(bedrooms, new ArrayList<Double>());
            }
            map.get(bedrooms).add(property.getSize());
        }
        return map;
    }

    private CatchmentTrendReportElement getCatchmentTrendReportElement(
            int projectId,
            int phaseId,
            int bedrooms,
            List<Trend> trendList,
            Map<Integer, AdditionalInfo> mapPidToAdditionInfo) {

        CatchmentTrendReportElement ctrElem = new CatchmentTrendReportElement();

        Trend trend = trendList.get(0);
        ctrElem.setProjectName(trend.getProjectName());
        ctrElem.setBuilderName(trend.getBuilderName());
        ctrElem.setPhaseName(trend.getPhaseName());
        ctrElem.setLaunchDate(trend.getLaunchDate().toString());
        ctrElem.setCompletionDate(trend.getCompletionDate().toString());
        ctrElem.setLocality(trend.getLocalityName());
        ctrElem.setProjectStatus(trend.getConstructionStatus());
        ctrElem.setTotalUnits(trend.getLtdSupply());
        ctrElem.setLaunchedUnits(trend.getLtdLaunchedUnit());
        ctrElem.setBhk(trend.getBedrooms());

        /* Filling BHK Grouped Map */

        String month = trend.getMonth().toString();
        Integer sales = trend.getUnitsSold();
        Integer availableInventory = trend.getInventory();
        Integer price = trend.getPricePerUnitArea();

        Map<TypeOfData, Map<String, Object>> bhkGroupedMap = ctrElem.getBhkGroupedMap();

        if (bhkGroupedMap.isEmpty()) {
            bhkGroupedMap.put(TypeOfData.Sales, new HashMap<String, Object>());
            bhkGroupedMap.put(TypeOfData.AvailableInventory, new HashMap<String, Object>());
            bhkGroupedMap.put(TypeOfData.Price, new HashMap<String, Object>());
        }

        bhkGroupedMap.get(TypeOfData.Sales).put(month, sales);
        bhkGroupedMap.get(TypeOfData.AvailableInventory).put(month, availableInventory);
        bhkGroupedMap.get(TypeOfData.Price).put(month, price);

        /* Populating additional info */

        AdditionalInfo additionalInfo = mapPidToAdditionInfo.get(projectId);
        ctrElem.setBhkSizeRange(additionalInfo.getBhkSizeRangeString(bedrooms));
        ctrElem.setLatitude(additionalInfo.laitude);
        ctrElem.setLongitude(additionalInfo.longitude);
        ctrElem.setProjectArea(additionalInfo.projectArea);
        ctrElem.setLaunchPrice(0);

        return ctrElem;
    }

    @SuppressWarnings("unused")
    private class AdditionalInfo {
        int                        projectId;
        Double                     laitude;
        Double                     longitude;
        Double                     projectArea = 0d;
        Double                     launchPrice = 0d;
        Map<Integer, List<Double>> mapPidToBhkRange;

        public String getBhkSizeRangeString(int bedrooms) {
            List<Double> bhkSizeList = this.mapPidToBhkRange.get(bedrooms);
            String bhkSizeRange;
            if (bhkSizeList == null || bhkSizeList.isEmpty()) {
                bhkSizeRange = "-NA-";
            }
            else {
                Collections.sort(bhkSizeList);
                return (bhkSizeList.get(0) + "-" + bhkSizeList.get(bhkSizeList.size() - 1));
            }
            return bhkSizeRange;
        }
    }

}
