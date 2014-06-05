package com.proptiger.data.repo.user;

import java.util.List;

import com.proptiger.data.model.user.Dashboard;
import com.proptiger.data.pojo.FIQLSelector;

public interface DashboardCustomDao {
    
    public List<Dashboard> getDashboards(FIQLSelector fiqlSelector);

}
