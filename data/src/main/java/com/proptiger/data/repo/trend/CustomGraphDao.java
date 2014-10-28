package com.proptiger.data.repo.trend;

import java.util.List;

import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.data.model.trend.Graph;

public interface CustomGraphDao {
    public List<Graph> getFilteredGraphs(FIQLSelector fiqlSelector);
}
