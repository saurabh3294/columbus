package com.proptiger.data.repo;

import java.util.List;

import com.proptiger.data.model.ProjectPhase;
import com.proptiger.data.pojo.FIQLSelector;

public interface CustomProjectPhaseDao {
    public List<ProjectPhase> getFilteredPhases(FIQLSelector fiqlSelector);
}
