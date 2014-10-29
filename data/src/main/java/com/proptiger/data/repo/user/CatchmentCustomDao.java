package com.proptiger.data.repo.user;

import java.util.List;

import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.data.model.Catchment;

public interface CatchmentCustomDao {
    public List<Catchment> getFilteredCatchments(FIQLSelector fiqlSelector);
}