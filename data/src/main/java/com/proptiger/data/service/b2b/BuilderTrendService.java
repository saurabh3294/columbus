package com.proptiger.data.service.b2b;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.b2b.external.dto.BuilderTrend;
import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.model.b2b.InventoryPriceTrend;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.b2b.TrendDao;
import com.proptiger.exception.ResourceNotFoundException;

@Service
public class BuilderTrendService {
    @Autowired
    TrendDao trendDao;

    public BuilderTrend getBuilderTrendForSingleBuilder(Integer builderId, UserInfo userInfo) {
        FIQLSelector selector = new FIQLSelector();
        selector.addAndConditionToFilter("builderId==" + builderId);
        List<BuilderTrend> builderTrends = getBuilderTrend(selector, userInfo);
        if (builderTrends == null) {
            throw new ResourceNotFoundException("BuilderId " + builderId + " doesn't exist");
        }
        return getBuilderTrend(selector, userInfo).get(0);
    }

    public List<BuilderTrend> getBuilderTrend(FIQLSelector selector, UserInfo userInfo) {
        FIQLSelector fiqlSelector = new FIQLSelector();
        fiqlSelector.setFilters(selector.getFilters()).addAndConditionToFilter("isDominantProjectUnitType==True");
        fiqlSelector.setGroup("projectId");
        fiqlSelector.addField("builderId").addField("builderName").addField("countDistinctProjectId")
                .addField("minPricePerUnitArea").addField("maxPricePerUnitArea");

        List<InventoryPriceTrend> inventoryPriceTrends = trendDao.getTrend(fiqlSelector);

        return null;
    }
}