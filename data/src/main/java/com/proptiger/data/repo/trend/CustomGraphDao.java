package com.proptiger.data.repo.trend;

import java.util.List;

import com.proptiger.data.model.trend.Graph;
import com.proptiger.data.pojo.FIQLSelector;

public interface CustomGraphDao {
    public List<Graph> getFilteredGraphs(FIQLSelector fiqlSelector);
}
