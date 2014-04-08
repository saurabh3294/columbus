package com.proptiger.data.service.b2b;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proptiger.data.b2b.external.dto.BuilderTrend;
import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.exception.ResourceNotFoundException;

@Service
public class BuilderTrendService {
    public BuilderTrend getBuilderTrendForSingleBuilder(Integer builderId, UserInfo userInfo) {
        FIQLSelector selector = new FIQLSelector();
        selector.addAndConditionToFilter("builderId=" + builderId);
        List<BuilderTrend> builderTrends = getBuilderTrend(selector, userInfo);
        if (builderTrends == null) {
            throw new ResourceNotFoundException("BuilderId " + builderId + " doesn't exist");
        }
        return getBuilderTrend(selector, userInfo).get(0);
    }

    public List<BuilderTrend> getBuilderTrend(FIQLSelector selector, UserInfo userInfo) {
        return null;
    }
}
