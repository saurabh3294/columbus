package com.proptiger.data.repo.b2b;

import java.util.List;

import com.proptiger.data.model.b2b.Catchment;
import com.proptiger.data.pojo.FIQLSelector;

public interface CatchmentCustomDao {
    public Integer countByName(String name);

    public List<Catchment> getFilteredCatchments(FIQLSelector fiqlSelector);
}