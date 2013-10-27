package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.external.dto.ProjectPriceHistoryDetail;
import com.proptiger.data.external.dto.ProjectPriceHistoryDetail.ProjectPriceDetail;
import com.proptiger.data.internal.dto.PriceDetail;
import com.proptiger.data.internal.dto.ProjectPriceTrend;
import com.proptiger.data.repo.CMSDao;

/**
 * This class is responsible to get price trend for project
 * @author Rajeev Pandey
 *
 */
@Service
public class ProjectPriceTrendService {

	@Autowired
	private CMSDao cmsDao;
	
	/**
	 * Getting price trend using cms api, and converting that to internal DTO representation 
	 * @param projectIdTypeIdMap
	 * @param typeId
	 * @param noOfMonths
	 * @return
	 */
	public List<ProjectPriceTrend> getProjectPriceHistory(Map<Integer, Integer> projectIdTypeIdMap, Integer noOfMonths){
		if(projectIdTypeIdMap == null || projectIdTypeIdMap.isEmpty()){
			throw new IllegalArgumentException("Illegal values for project ids");
		}
		ProjectPriceHistoryDetail response = cmsDao.getProjectPriceHistory(projectIdTypeIdMap.keySet(), noOfMonths);
		return convertToInternalPriceHistory(response, projectIdTypeIdMap);
	}

	/**
	 * Converting external price trend object to internal price trend object
	 * @param response
	 * @param projectIdTypeIdMap
	 * @param typeId
	 * @return
	 */
	private List<ProjectPriceTrend> convertToInternalPriceHistory(
			ProjectPriceHistoryDetail response, Map<Integer, Integer> projectIdTypeIdMap) {
		List<ProjectPriceTrend> projectHistoryList = new ArrayList<ProjectPriceTrend>();
		
		if(response != null){
			//Map<Date-String, Map<ProjectId, Map<PhaseId_TypeId, ProjectPriceDetail>>>
			Map<String, Map<String, Map<String, ProjectPriceDetail>>> prices = response.getPrices();
			if(prices != null){
				Iterator<String> priceDateItr = prices.keySet().iterator();
				Map<String, ProjectPriceTrend> projectHistoryMap = new HashMap<String, ProjectPriceTrend>();
				while(priceDateItr.hasNext()){
					String dateKey = priceDateItr.next();
					//exclude current and latest time stamp
					if(!dateKey.equals("current") && !dateKey.equals("latest")){
						Map<String, Map<String, ProjectPriceDetail>> projectsPriceMap = prices.get(dateKey);
						if(projectsPriceMap != null && !projectsPriceMap.isEmpty()){
							
							Iterator<String> projectIdItr = projectsPriceMap.keySet().iterator();
							
							while(projectIdItr.hasNext()){
								String projectId = projectIdItr.next();
								Integer typeId = projectIdTypeIdMap.get(Integer.parseInt(projectId));
								ProjectPriceTrend projectPriceHistory = new ProjectPriceTrend();
								/*
								 * Check if this project id is already present in map
								 */
								if(projectHistoryMap.get(projectId) != null){
									projectPriceHistory = projectHistoryMap.get(projectId);
								}
								else{
									projectPriceHistory.setProjectId(Integer.parseInt(projectId));
									projectPriceHistory.setTypeId(typeId);
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
		sortPricesByDateAsc(projectHistoryList);
		return projectHistoryList;
	}

	private void sortPricesByDateAsc(List<ProjectPriceTrend> projectHistoryList) {
		for(ProjectPriceTrend priceTrend: projectHistoryList){
			Collections.sort(priceTrend.getPrices(), new Comparator<PriceDetail>() {
				@Override
				public int compare(PriceDetail p1, PriceDetail p2) {
					// TODO Auto-generated method stub
					return p1.getEffectiveDate().compareTo(p2.getEffectiveDate());
				}
			});
		}
		
	}

	/**
	 * Creating PriceDetail object, if type id present then actual value will be used otherwise 
	 * average of all will be used
	 * @param typeId
	 * @param priceDetailsForTypeIdIdMap
	 * @return
	 */
	private PriceDetail getPriceDetailObj(Integer typeId,
			Map<String, ProjectPriceDetail> priceDetailsForTypeIdIdMap) {
		PriceDetail priceDetail = new PriceDetail();
		double price = 0.0D;
		Date date = null;
		boolean found = false;
		if(typeId != null){
			String phaseIdTypeIdKey = typeId.toString();
			Iterator<String> keyItr = priceDetailsForTypeIdIdMap.keySet().iterator();
			while(keyItr.hasNext()){
				String key = keyItr.next();
				if(key.endsWith(phaseIdTypeIdKey)){
					ProjectPriceDetail priceObj = priceDetailsForTypeIdIdMap.get(key);
					price = priceObj.getPrice();
					date = priceObj.getEffective_date();
					found = true;
					break;
				}
			}
			
		}
		/*
		 * If type id is null then found will be false and this block of code
		 * will take average of price of all phase id and type id combination
		 */
		if(!found){
			for(ProjectPriceDetail priceDetailObj: priceDetailsForTypeIdIdMap.values()){
				price = price + priceDetailObj.getPrice();
				if(date == null){
					date = priceDetailObj.getEffective_date();
				}
			}
			price = (price/priceDetailsForTypeIdIdMap.values().size());
		}
		priceDetail.setEffectiveDate(date);
		priceDetail.setPrice((int)price);
		return priceDetail;
	}
}