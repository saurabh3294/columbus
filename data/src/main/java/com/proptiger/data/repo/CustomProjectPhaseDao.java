package com.proptiger.data.repo;

import java.util.List;

import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.data.model.ProjectPhase;

public interface CustomProjectPhaseDao {
    public List<ProjectPhase> getFilteredPhases(FIQLSelector fiqlSelector);
}
