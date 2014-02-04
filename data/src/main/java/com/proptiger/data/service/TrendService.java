package com.proptiger.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.InventoryPriceTrend;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.TrendDao;

/** 
 * @author Azitabh Ajit
 */

@Service
public class TrendService {
	@Autowired
    private TrendDao trendDao;
	
	public List<InventoryPriceTrend> getTrend(FIQLSelector selector) {
        return trendDao.getTrend(selector);
    }
}