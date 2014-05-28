package com.proptiger.data.repo.user;

import java.util.List;

import com.proptiger.data.model.Catchment;
import com.proptiger.data.pojo.FIQLSelector;

public interface CatchmentCustomDao {
    public List<Catchment> getFilteredCatchments(FIQLSelector fiqlSelector);
}