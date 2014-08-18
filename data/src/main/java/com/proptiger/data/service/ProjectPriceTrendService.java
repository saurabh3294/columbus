package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.dto.internal.trend.HithertoDurationSelector;
import com.proptiger.data.internal.dto.PriceDetail;
import com.proptiger.data.internal.dto.ProjectPriceTrend;
import com.proptiger.data.internal.dto.ProjectPriceTrendInput;
import com.proptiger.data.model.trend.InventoryPriceTrend;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.service.trend.TrendService;
import com.proptiger.data.util.Constants;
import com.proptiger.data.util.UtilityClass;
import com.sun.tools.classfile.ConstantPool.CONSTANT_String_info;

/**
 * This class is responsible to get price trend for project
 * 
 * @author Rajeev Pandey
 * 
 */
@Service
public class ProjectPriceTrendService {

    @Autowired
    private TrendService trendService;

    /**
     * Getting price trend using Trend api, and converting that to internal DTO
     * representation
     * 
     * @param inputs
     * @param typeId
     * @param noOfMonths
     * @return
     */
    public List<ProjectPriceTrend> getProjectPriceHistory(List<ProjectPriceTrendInput> inputs, Integer noOfMonths) {
        if (inputs == null || inputs.isEmpty()) {
            throw new IllegalArgumentException("Illegal values for project ids");
        }
        Set<Integer> projectIdSet = new HashSet<>();
        for (ProjectPriceTrendInput input : inputs) {
            projectIdSet.add(input.getProjectId());
        }
        FIQLSelector fiqlSelector = new FIQLSelector();
        fiqlSelector.setGroup("projectId,month,bedrooms");
        fiqlSelector.setFields("wavgPricePerUnitAreaOnLtdSupply,projectName");
        for (Integer projectId : projectIdSet) {
            fiqlSelector.addOrConditionToFilter("projectId==" + projectId);
        }
        HithertoDurationSelector hithertoSelector = new HithertoDurationSelector();
        hithertoSelector.setMonthDuration(noOfMonths);
        PaginatedResponse<List<InventoryPriceTrend>> projectPriceTrends = trendService.getHithertoPaginatedTrend(
                fiqlSelector,
                null,
                null,
                hithertoSelector);
        return getMappedResults(projectPriceTrends, fiqlSelector, inputs);
    }

    private List<ProjectPriceTrend> getMappedResults(
            PaginatedResponse<List<InventoryPriceTrend>> inventoryPriceTrends,
            FIQLSelector fiqlSelector,
            List<ProjectPriceTrendInput> inputs) {

        PaginatedResponse<Map<Integer, Map<Long, Map<Integer, List<InventoryPriceTrend>>>>> result = new PaginatedResponse<>();
        List<String> groupKeys = Arrays.asList(fiqlSelector.getGroup().split(","));
        result.setTotalCount(inventoryPriceTrends.getTotalCount());

        if (!groupKeys.isEmpty()) {
            Map<Integer, Map<Long, Map<Integer, List<InventoryPriceTrend>>>> serviceResponse = (Map<Integer, Map<Long, Map<Integer, List<InventoryPriceTrend>>>>) UtilityClass
                    .groupFieldsAsPerKeys(inventoryPriceTrends.getResults(), groupKeys);
            result.setResults(serviceResponse);
        }
        return convertToInternalPriceTrend(result, inputs);

    }

    /**
     * Converting Trend API object to internal ProjectPriceTrend object
     * 
     * @param response
     * @param inputs
     * @return
     */
    private List<ProjectPriceTrend> convertToInternalPriceTrend(
            PaginatedResponse<Map<Integer, Map<Long, Map<Integer, List<InventoryPriceTrend>>>>> response,
            List<ProjectPriceTrendInput> inputs) {
        List<ProjectPriceTrend> projectPriceTrends = new ArrayList<>();
        for (ProjectPriceTrendInput priceTrendInput : inputs) {
            ProjectPriceTrend projectPriceTrend = new ProjectPriceTrend();
            projectPriceTrend.setProjectId(priceTrendInput.getProjectId());
            projectPriceTrend.setTypeId(priceTrendInput.getTypeId());
            projectPriceTrend.setListingName(priceTrendInput.getListingName());
            projectPriceTrend.setProjectName(priceTrendInput.getProjectName());
            Map<Long, Map<Integer, List<InventoryPriceTrend>>> projectPrices = response.getResults().get(
                    projectPriceTrend.getProjectId());
            List<PriceDetail> priceDetails = new ArrayList<PriceDetail>();

            Iterator<Long> priceDateItr = projectPrices.keySet().iterator();
            while (priceDateItr.hasNext()) {
                Long dateKey = priceDateItr.next();
                Date effectiveDate = new Date(dateKey);

                Object price = projectPrices.get(dateKey).get(priceTrendInput.getBedrooms()).get(0).getExtraAttributes()
                        .get("wavgPricePerUnitAreaOnLtdSupply");
                // Set price if price is not null, otherwise left null and populated in PortfolioPriceTrendService
                if (price != null) {
                    PriceDetail priceDetail = new PriceDetail();
                    priceDetail.setPrice((double) price);
                    priceDetail.setEffectiveDate(effectiveDate);
                    priceDetails.add(priceDetail);
                }
            }
            if (priceDetails.size() > 0) {
                projectPriceTrend.setPrices(priceDetails);
            }
            projectPriceTrends.add(projectPriceTrend);
        }
        sortPricesByDateAsc(projectPriceTrends);
        return projectPriceTrends;
    }

    /**
     * Sorting ProjectPriceTrend list by date
     * 
     * @param projectHistoryList
     */
    private void sortPricesByDateAsc(List<ProjectPriceTrend> projectHistoryList) {
        for (ProjectPriceTrend priceTrend : projectHistoryList) {
            if (priceTrend.getPrices() != null) {
                Collections.sort(priceTrend.getPrices(), new Comparator<PriceDetail>() {
                    @Override
                    public int compare(PriceDetail p1, PriceDetail p2) {
                        return p1.getEffectiveDate().compareTo(p2.getEffectiveDate());
                    }
                });
            }
        }
    }
 }
