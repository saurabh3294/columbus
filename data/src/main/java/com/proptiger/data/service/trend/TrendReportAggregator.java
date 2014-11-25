package com.proptiger.data.service.trend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.core.constants.ResponseCodes;
import com.proptiger.core.exception.ProAPIException;
import com.proptiger.core.model.cms.Project;
import com.proptiger.core.model.cms.Property;
import com.proptiger.core.model.cms.Trend;
import com.proptiger.core.pojo.Paging;
import com.proptiger.core.pojo.Selector;
import com.proptiger.core.pojo.response.PaginatedResponse;
import com.proptiger.data.enums.filter.Operator;
import com.proptiger.data.model.trend.CatchmentTrendReportElement;
import com.proptiger.data.model.trend.CatchmentTrendReportElement.TypeOfData;
import com.proptiger.data.service.PropertyService;

@Component
public class TrendReportAggregator {

    @Autowired
    private PropertyService propertyService;

    private int             maxPageSizeForPropertyFetch = 150;

    private static Logger   logger                      = LoggerFactory.getLogger(TrendReportAggregator.class);

    @SuppressWarnings("unchecked")
    public List<CatchmentTrendReportElement> getCatchmentTrendReport(
            Map<Integer, Object> groupedTrendList,
            List<Date> sortedMonthList) {

        /** Fetch Information from Other APIs **/

        List<Integer> projectIdList = new ArrayList<Integer>(groupedTrendList.keySet());
        Map<Integer, AdditionalInfo> mapPidToAdditionInfo = getAdditionalInfo(projectIdList);

        /**
         * Generate a list of CatchmentReportElement objects from the above
         * grouped map
         **/

        List<CatchmentTrendReportElement> ctrElemList = new ArrayList<CatchmentTrendReportElement>();
        List<CatchmentTrendReportElement> ctrElemListTemp = new ArrayList<CatchmentTrendReportElement>();

        Map<Integer, Object> projectGroupedTrend = null;
        Map<Integer, Object> phaseGroupedTrend = null;
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
                    ctrElemListTemp.add(ctrElem);
                }
                addElementsForBhkWiseTotals(ctrElemListTemp, sortedMonthList);
                ctrElemList.addAll(ctrElemListTemp);
                ctrElemListTemp.clear();
            }
        }

        return ctrElemList;
    }

    private void addElementsForBhkWiseTotals(List<CatchmentTrendReportElement> ctrElemList, List<Date> sortedMonthList) {

        CatchmentTrendReportElement ctrElem = (CatchmentTrendReportElement) SerializationUtils
                .clone(ctrElemList.get(0));

        Map<TypeOfData, Map<Date, Object>> bhkGroupedMap = ctrElem.getBhkGroupedMap();

        if (bhkGroupedMap.isEmpty()) {
            for (TypeOfData tod : TypeOfData.values()) {
                bhkGroupedMap.put(tod, new HashMap<Date, Object>());
            }
        }

        /* Summing Month wise data for each type of data. */

        int monthWiseSum = 0;
        Integer temp;
        for (TypeOfData tod : TypeOfData.values()) {
            for (Date month : sortedMonthList) {
                monthWiseSum = 0;
                for (CatchmentTrendReportElement ctre : ctrElemList) {
                    temp = (Integer) (ctre.getBhkGroupedMap().get(tod).get(month));
                    monthWiseSum += (temp != null ? temp.intValue() : 0);
                }
                bhkGroupedMap.get(tod).put(month, monthWiseSum);
            }
        }

        /* Special weighted average for price */

        Integer price, ltdSupply;
        int wavgPriceOnLtdSupply = 0, sumLtdSupply = 0;
        for (Date month : sortedMonthList) {
            for (CatchmentTrendReportElement ctre : ctrElemList) {
                price = (Integer) (ctre.getBhkGroupedMap().get(TypeOfData.Price).get(month));
                ltdSupply = (Integer) (ctre.getBhkGroupedMap().get(TypeOfData.LtdSupply).get(month));
                if (price != null && ltdSupply != null && price > 0 && ltdSupply > 0) {
                    wavgPriceOnLtdSupply += price * ltdSupply;
                    sumLtdSupply += ltdSupply;
                }
            }
            wavgPriceOnLtdSupply = ((sumLtdSupply == 0) ? 0 : (wavgPriceOnLtdSupply / sumLtdSupply));
            bhkGroupedMap.get(TypeOfData.Price).put(month, wavgPriceOnLtdSupply);
            wavgPriceOnLtdSupply = 0;
            sumLtdSupply = 0;
        }

        /* Combining BHK-Range */

        ctrElem.setBhkSizeRange(ctrElem.getProjectBhkSizeRange());

        /* Summing other common data */

        ctrElem.setBhk(-1);
        int sumTotalUnits = 0;
        int sumLaunchedUnits = 0;
        for (CatchmentTrendReportElement ctre : ctrElemList) {
            sumTotalUnits += (ctre.getTotalUnits() == null ? 0 : ctre.getTotalUnits());
            sumLaunchedUnits += (ctre.getLaunchedUnits() == null ? 0 : ctre.getLaunchedUnits());
        }
        ctrElem.setLaunchedUnits(sumLaunchedUnits);
        ctrElem.setTotalUnits(sumTotalUnits);

        ctrElemList.add(ctrElem);
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

        Collections.sort(trendList, new Comparator<Trend>() {
            @Override
            public int compare(Trend o1, Trend o2) {
                return o2.getMonth().compareTo(o1.getMonth());
            }
        });

        Trend trend = trendList.get(0);
        ctrElem.setProjectName(trend.getProjectName());
        ctrElem.setBuilderName(trend.getBuilderName());
        ctrElem.setPhaseName(trend.getPhaseName());
        ctrElem.setLaunchDate(trend.getLaunchDate());
        ctrElem.setCompletionDate(trend.getCompletionDate());
        ctrElem.setLocality(trend.getLocalityName());
        ctrElem.setProjectStatus(trend.getConstructionStatus());
        ctrElem.setTotalUnits(trend.getLtdSupply());
        ctrElem.setLaunchedUnits(trend.getLtdLaunchedUnit());
        ctrElem.setBhk(trend.getBedrooms());

        /* Filling BHK Grouped Map */

        Map<TypeOfData, Map<Date, Object>> bhkGroupedMap = ctrElem.getBhkGroupedMap();

        if (bhkGroupedMap.isEmpty()) {
            for (TypeOfData tod : TypeOfData.values()) {
                bhkGroupedMap.put(tod, new HashMap<Date, Object>());
            }
        }

        /* Filling Month-Wise data in a BHK-grouped-map. */

        Date month;
        for (Trend t : trendList) {
            month = t.getMonth();
            bhkGroupedMap.get(TypeOfData.Sales).put(month, t.getUnitsSold());
            bhkGroupedMap.get(TypeOfData.AvailableInventory).put(month, t.getInventory());
            bhkGroupedMap.get(TypeOfData.Price).put(month, t.getPricePerUnitArea());
            bhkGroupedMap.get(TypeOfData.LtdSupply).put(month, t.getLtdSupply());
        }

        /* Populating additional info */

        AdditionalInfo additionalInfo = mapPidToAdditionInfo.get(projectId);
        if (additionalInfo != null) {
            ctrElem.setBhkSizeRange(additionalInfo.getBhkSizeRangeString(bedrooms));
            ctrElem.setLatitude(additionalInfo.laitude);
            ctrElem.setLongitude(additionalInfo.longitude);
            ctrElem.setProjectArea(additionalInfo.projectArea);
            ctrElem.setProjectBhkSizeRange(additionalInfo.getProjectBhkSizeRange());
        }

        ctrElem.setLaunchPrice(0);

        return ctrElem;
    }

    private Map<Integer, AdditionalInfo> getAdditionalInfo(List<Integer> projectIdList) {
        logger.debug("PnA_Report: Fetching additional info from Solr for " + projectIdList.size()
                + " (total) projects pagewise.");

        if (projectIdList.size() > TrendReportConstants.Limit_MaxProjectAllowed) {
            throw new ProAPIException(ResponseCodes.EMPTY_REPORT_GENERATED, String.format(
                    TrendReportConstants.ErrMsg_MaxProjectLimitExceeded,
                    TrendReportConstants.Limit_MaxProjectAllowed));
        }

        List<Project> projectList = new ArrayList<Project>();

        /** Fetch Project details page-wise **/
        Selector selector;
        List<Integer> partialProjectIdList;
        PaginatedResponse<List<Project>> paginatedResponse = null;
        List<Project> projectListTemp = null;
        int fetched = 0;
        int fetch_end;
        while (true) {
            fetch_end = Math.min(fetched + maxPageSizeForPropertyFetch, projectIdList.size());
            partialProjectIdList = projectIdList.subList(fetched, fetch_end);
            selector = getSelectorProjectIds(partialProjectIdList);
            selector.setPaging(new Paging(0, maxPageSizeForPropertyFetch));
            fetched += maxPageSizeForPropertyFetch;
            logger.debug("PnA_Report: Fetching additional info from Solr for " + partialProjectIdList.size()
                    + " projects. Paging = "
                    + selector.getPaging().toString());

            logger.debug("PnA_Report: Property service query : Start");
            paginatedResponse = propertyService.getPropertiesGroupedToProjects(selector);
            logger.debug("PnA_Report: Property service query : End");
            if (paginatedResponse == null || paginatedResponse.getResults() == null) {
                break;
            }

            logger.debug("PnA_Report: Fetching additional info from Solr. Total projects fetched = " + fetched);

            projectListTemp = paginatedResponse.getResults();
            projectList.addAll(projectListTemp);

            // if (fetched >= paginatedResponse.getTotalCount() ||
            // projectListTemp.isEmpty()) {

            if (fetched >= projectIdList.size() || projectListTemp.isEmpty()) {
                logger.debug("PnA_Report: Solr data fetch conplete.");
                break;
            }

        }

        /** make additional info map from project-details **/

        Map<Integer, AdditionalInfo> mapPidToAdditionInfo = new HashMap<Integer, AdditionalInfo>();
        AdditionalInfo additionalInfo;
        Double projectSize = null;
        for (Project project : projectList) {
            additionalInfo = new AdditionalInfo();
            additionalInfo.laitude = project.getLatitude();
            additionalInfo.longitude = project.getLongitude();
            projectSize = project.getSizeInAcres();
            if (projectSize != null) {
                additionalInfo.projectArea = project.getSizeInAcres();
            }
            additionalInfo.mapPidToBhkRange = getProjectBhkSizeRangeMap(project);
            mapPidToAdditionInfo.put(project.getProjectId(), additionalInfo);
        }

        return mapPidToAdditionInfo;
    }

    private Selector getSelectorProjectIds(List<Integer> projectIdList) {
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

        return selector;
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
            return getRangeAsString(this.mapPidToBhkRange.get(bedrooms));
        }

        public String getProjectBhkSizeRange() {
            List<Double> bhkSizeList = new ArrayList<Double>();
            for (int bedrooms : mapPidToBhkRange.keySet()) {
                bhkSizeList.addAll(mapPidToBhkRange.get(bedrooms));
            }
            return getRangeAsString(bhkSizeList);
        }

        private String getRangeAsString(List<Double> bhkSizeList) {
            if (bhkSizeList == null || bhkSizeList.isEmpty()) {
                return ("-NA-");
            }
            else {
                try {
                    Collections.sort(bhkSizeList);
                    return (bhkSizeList.get(0).intValue() + "-" + bhkSizeList.get(bhkSizeList.size() - 1).intValue());
                }
                catch (Exception ex) {
                    return ("-NA-");
                }
            }
        }
    }

}
