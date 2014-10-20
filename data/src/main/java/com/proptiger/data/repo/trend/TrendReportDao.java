package com.proptiger.data.repo.trend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.model.trend.CatchmentTrendReportElement;
import com.proptiger.data.model.trend.Trend;
import com.proptiger.data.model.trend.CatchmentTrendReportElement.TypeOfData;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.service.trend.TrendService;
import com.proptiger.data.service.user.CatchmentService;
import com.proptiger.data.util.UtilityClass;

@Repository
public class TrendReportDao {

    @Autowired
    private TrendService     trendService;

    @Autowired
    private CatchmentService catchmentService;

    @SuppressWarnings("unchecked")
    public List<CatchmentTrendReportElement> getCatchmentTrendReport(Integer catchmentId, FIQLSelector selector, ActiveUser userInfo) {

        selector.addAndConditionToFilter(catchmentService.getCatchmentFIQLFilter(catchmentId, userInfo));
        List<Trend> trendList = trendService.getTrend(selector);
        List<CatchmentTrendReportElement> ctrElemList = new ArrayList<CatchmentTrendReportElement>();

        /* Get trend list as grouped map */
        
        String[] groupFields = { "projectName", "phaseName", "bedrooms" };
        Map<String, Object> projectGroupedTrend = null;
        Map<Integer, Object> phaseGroupedTrend = null;
        Map<String, Object> groupedTrendList = (Map<String, Object>) UtilityClass.groupFieldsAsPerKeys(
                trendList,
                Arrays.asList(groupFields));

        /* Get Report Element List*/
        
        CatchmentTrendReportElement ctrElem;
        for (String projectName : groupedTrendList.keySet()) {
            projectGroupedTrend = (Map<String, Object>) groupedTrendList.get(projectName);
            for (String phaseName : projectGroupedTrend.keySet()) {
                phaseGroupedTrend = (Map<Integer, Object>) projectGroupedTrend.get(phaseName);
                for (int bedrooms : phaseGroupedTrend.keySet()) {
                    ctrElem = getCatchmentTrendReportElement(
                            projectName,
                            phaseName,
                            bedrooms,
                            (List<Trend>) phaseGroupedTrend.get(bedrooms));
                    ctrElemList.add(ctrElem);
                }
            }
        }
        
        return ctrElemList;
    }

    private CatchmentTrendReportElement getCatchmentTrendReportElement(
            String projectId,
            String phaseId,
            int bedrooms,
            List<Trend> trendList) {

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

        ctrElem.setLatitude(0d);
        ctrElem.setLongitude(0d);
        ctrElem.setProjectArea(0);
        ctrElem.setLaunchPrice(0);

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

        return ctrElem;
    }
    
}
