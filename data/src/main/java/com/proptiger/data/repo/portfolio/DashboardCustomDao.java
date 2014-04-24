package com.proptiger.data.repo.portfolio;

import java.util.List;

import com.proptiger.data.model.portfolio.Dashboard;
import com.proptiger.data.pojo.FIQLSelector;

public interface DashboardCustomDao {
    
    public List<Dashboard> getDashboards(FIQLSelector fiqlSelector);

}
