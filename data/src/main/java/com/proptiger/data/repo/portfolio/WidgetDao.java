package com.proptiger.data.repo.portfolio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.portfolio.Widget;

/**
 * Widget repository class to provide CRUD operations for Widget resource
 * @author Rajeev Pandey
 *
 */
public interface WidgetDao extends JpaRepository<Widget, Integer>{
	
}
