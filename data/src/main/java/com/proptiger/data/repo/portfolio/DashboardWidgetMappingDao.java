package com.proptiger.data.repo.portfolio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.portfolio.DashboardWidgetMapping;

/**
 * @author Rajeev Pandey
 *
 */
public interface DashboardWidgetMappingDao extends JpaRepository<DashboardWidgetMapping, Integer>{
	public DashboardWidgetMapping findByDashboardIdAndWidgetId(Integer dashboardId, Integer widgetId);
	public List<DashboardWidgetMapping> findByDashboardId(Integer dashboardId);
}
