package com.proptiger.data.repo.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.user.DashboardWidgetMapping;

/**
 * @author Rajeev Pandey
 * 
 */
public interface DashboardWidgetMappingDao extends JpaRepository<DashboardWidgetMapping, Integer> {
    public DashboardWidgetMapping findByDashboardIdAndWidgetId(Integer dashboardId, Integer widgetId);

    public List<DashboardWidgetMapping> findByDashboardId(Integer dashboardId);
}
