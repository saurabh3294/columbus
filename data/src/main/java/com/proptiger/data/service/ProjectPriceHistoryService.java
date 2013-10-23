package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.external.dto.ProjectPriceHistoryDetail;
import com.proptiger.data.external.dto.ProjectPriceHistoryDetail.ProjectPriceDetail;
import com.proptiger.data.internal.dto.ProjectPriceHistory;
import com.proptiger.data.internal.dto.ProjectPriceHistory.PriceDetail;
import com.proptiger.data.repo.CMSDao;

/**
 * @author Rajeev Pandey
 *
 */
@Service
public class ProjectPriceHistoryService {

	@Autowired
	private CMSDao cmsDao;
	
	public List<ProjectPriceHistory> getProjectPriceHistory(List<Integer> projectIdList, Integer typeId, Integer noOfMonths){
		if(projectIdList == null || projectIdList.size() == 0){
			throw new IllegalArgumentException("Illegal values for {ids}");
		}
		ProjectPriceHistoryDetail response = cmsDao.getProjectPriceHistory(projectIdList, typeId, noOfMonths);
		return convertToInternalPriceHistory(response, projectIdList, typeId);
	}

	private List<ProjectPriceHistory> convertToInternalPriceHistory(
			ProjectPriceHistoryDetail response, List<Integer> projectIdList, Integer typeId) {
		List<ProjectPriceHistory> projectHistoryList = new ArrayList<ProjectPriceHistory>();
		
		if(response != null){
			//Map<Date-String, Map<ProjectId, Map<PhaseId_TypeId, ProjectPriceDetail>>>
			Map<String, Map<String, Map<String, ProjectPriceDetail>>> prices = response.getPrices();
			if(prices != null){
				Iterator<String> priceDateItr = prices.keySet().iterator();
				Map<String, ProjectPriceHistory> projectHistoryMap = new HashMap<String, ProjectPriceHistory>();
				while(priceDateItr.hasNext()){
					String dateKey = priceDateItr.next();
					//exclude current and latest time stamp
					if(!dateKey.equals("current") && !dateKey.equals("latest")){
						Map<String, Map<String, ProjectPriceDetail>> projectsPriceMap = prices.get(dateKey);
						if(projectsPriceMap != null && !projectsPriceMap.isEmpty()){
							
							Iterator<String> projectIdItr = projectsPriceMap.keySet().iterator();
							
							while(projectIdItr.hasNext()){
								String projectId = projectIdItr.next();
								ProjectPriceHistory projectPriceHistory = new ProjectPriceHistory();
								/*
								 * Check if this project id is already present in map
								 */
								if(projectHistoryMap.get(projectId) != null){
									projectPriceHistory = projectHistoryMap.get(projectId);
								}
								else{
									projectPriceHistory.setProjectId(Integer.parseInt(projectId));
								}
								
								Map<String, ProjectPriceDetail> priceDetailsForTypeIdIdMap = projectsPriceMap.get(projectId.toString());
								if(priceDetailsForTypeIdIdMap != null && !priceDetailsForTypeIdIdMap.isEmpty()){
									PriceDetail priceDetail = getPriceDetailObj(typeId, priceDetailsForTypeIdIdMap);
									projectPriceHistory.addPrice(priceDetail);
								
								}
								
								if(projectHistoryMap.get(projectId) == null){
									projectHistoryMap.put(projectId, projectPriceHistory);
								}
							}
						}
					}
				}
				projectHistoryList.addAll(projectHistoryMap.values());
				
			}
		}
		return projectHistoryList;
	}

	private PriceDetail getPriceDetailObj(Integer typeId,
			Map<String, ProjectPriceDetail> priceDetailsForTypeIdIdMap) {
		PriceDetail priceDetail = new PriceDetail();
		if(typeId == null){
			// take average of price of all phase id and type id combination
			double total = 0.0D;
			Date date = null;
			for(ProjectPriceDetail price: priceDetailsForTypeIdIdMap.values()){
				total = total + price.getPrice();
				if(date == null){
					date = price.getEffective_date();
				}
			}
			priceDetail.setEffectiveDate(date);
			Integer avgPrice = (int) (total/priceDetailsForTypeIdIdMap.values().size());
			priceDetail.setPrice(avgPrice);
		}
		else{
			// get price for particular type id
		}
		return priceDetail;
	}
}
