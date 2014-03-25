package com.proptiger.data.repo.b2b;

import java.util.List;

import com.proptiger.data.model.b2b.Graph;
import com.proptiger.data.pojo.FIQLSelector;

public interface CustomGraphDao {
    public List<Graph> getFilteredGraphs(FIQLSelector fiqlSelector);
}
