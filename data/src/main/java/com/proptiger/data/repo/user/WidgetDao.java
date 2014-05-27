package com.proptiger.data.repo.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.user.Widget;

/**
 * Widget repository class to provide CRUD operations for Widget resource
 * 
 * @author Rajeev Pandey
 * 
 */
public interface WidgetDao extends JpaRepository<Widget, Integer> {
    public List<Widget> findByType(String type);

    @Query("select W from Widget W where W.type='DEFAULT'")
    public List<Widget> findDefaultWidgets();
}
