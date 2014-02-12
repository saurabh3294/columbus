package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.InventoryPriceTrend;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.TrendDao;
import com.proptiger.data.util.Constants;
import com.proptiger.data.util.UtilityClass;

/**
 * @author Azitabh Ajit
 */

@Service
public class TrendService {
	@Autowired
	private TrendDao trendDao;
	private Logger logger = LoggerFactory.getLogger(TrendService.class);

	public List<InventoryPriceTrend> getTrend(FIQLSelector selector) {
		return trendDao.getTrend(selector);
	}

	public Map<String, List<InventoryPriceTrend>> getBudgetSplitTrend(final FIQLSelector selector, final String rangeField, String rangeValue) {
		Map<String, List<InventoryPriceTrend>> result = new HashMap<>();

		final List<Integer> rangeValueList = Arrays.asList(UtilityClass.getIntArrFromStringArr(rangeValue.split(",")));
		Collections.sort(rangeValueList);
		
		final int budgetRangeLength = rangeValueList.size();

		ExecutorService executor = Executors.newFixedThreadPool(Math.min(budgetRangeLength+1, 5));

		LinkedHashSet<Callable<List<InventoryPriceTrend>>> callables = new LinkedHashSet<>();

		callables.add(new Callable<List<InventoryPriceTrend>>() {
			public List<InventoryPriceTrend> call() throws Exception {
				FIQLSelector sel = selector.clone();
				sel.addAndConditionToFilter(rangeField + "=lt=" + rangeValueList.get(0));
				return trendDao.getTrend(sel);
			}
		});

		for (int i = 1; i < budgetRangeLength; i++) {
			final int j = i;
			callables.add(new Callable<List<InventoryPriceTrend>>() {
				public List<InventoryPriceTrend> call() throws Exception {
					FIQLSelector sel = selector.clone();
					sel.addAndConditionToFilter(rangeField + "=lt=" + rangeValueList.get(j)).addAndConditionToFilter(rangeField + "=ge=" + rangeValueList.get(j - 1));
					return trendDao.getTrend(sel);
				}
			});
		}

		callables.add(new Callable<List<InventoryPriceTrend>>() {
			public List<InventoryPriceTrend> call() throws Exception {
				FIQLSelector sel = selector.clone();
				sel.addAndConditionToFilter(rangeField + "=ge="	+ rangeValueList.get(budgetRangeLength - 1));
				return trendDao.getTrend(sel);
			}
		});

		List<Future<List<InventoryPriceTrend>>> futures = new ArrayList<>();

		try {
			futures = executor.invokeAll(callables);

			int i = 0;
			for (Future<List<InventoryPriceTrend>> future : futures) {
				String key = "-";
				if (i != 0) {
					key = rangeValueList.get(i-1) + key;
				}
				if (i != (budgetRangeLength)) {
					key = key + rangeValueList.get(i);
				}
				result.put(key, future.get());
				i++;
			}
		} catch (InterruptedException | ExecutionException e) {
			logger.error("Error in TrendService", e);
		}

		executor.shutdownNow();
		return result;
	}
	
	@Cacheable(value=Constants.CacheName.CACHE)
	public String getDominantSupply(FIQLSelector sel){
		FIQLSelector selector;
		String unitType = null;
		try {
			selector = sel.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		selector.setFields("sumSupply");
		selector.setGroup("unitType");
		selector.setSort("-sumSupply");
		selector.setStart(0);
		selector.setRows(1);
		
		try {
			unitType = trendDao.getTrend(selector).get(0).getUnitType();
		} catch (IndexOutOfBoundsException e) {
		}
		return unitType;
	}
}