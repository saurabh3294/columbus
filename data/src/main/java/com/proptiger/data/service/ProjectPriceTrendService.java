package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.external.dto.ProjectPriceHistoryDetail;
import com.proptiger.data.external.dto.ProjectPriceHistoryDetail.ProjectPriceDetail;
import com.proptiger.data.internal.dto.PriceDetail;
import com.proptiger.data.internal.dto.ProjectPriceTrend;
import com.proptiger.data.internal.dto.ProjectPriceTrendInput;
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
	 * @param inputs
	 * @param typeId
	 * @param noOfMonths
	 * @return
	 */
	public List<ProjectPriceTrend> getProjectPriceHistory(List<ProjectPriceTrendInput> inputs, Integer noOfMonths){
		if(inputs == null || inputs.isEmpty()){
			throw new IllegalArgumentException("Illegal values for project ids");
		}
		Set<Integer> projectIdSet = new HashSet<>();
		for(ProjectPriceTrendInput input: inputs){
			projectIdSet.add(input.getProjectId());
		}
		ProjectPriceHistoryDetail response = cmsDao.getProjectPriceHistory(projectIdSet, noOfMonths);
		return convertToInternalPriceHistory_(response, inputs);
	}

	/**
	 * Converting external price trend object to internal price trend object
	 * @param response
	 * @param projectIdTypeIdMap
	 * @param typeId
	 * @return
	 */
	private List<ProjectPriceTrend> convertToInternalPriceHistory_(
			ProjectPriceHistoryDetail response, List<ProjectPriceTrendInput> inputs) {
		List<ProjectPriceTrend> projectPriceTrends = new ArrayList<>();
		if(response != null){
			Map<String, Map<String, Map<String, ProjectPriceDetail>>> prices = response.getPrices();
			if(prices != null){
				
				for(ProjectPriceTrendInput priceTrendInput: inputs){
					Iterator<String> priceDateItr = prices.keySet().iterator();
					ProjectPriceTrend projectPriceTrend = new ProjectPriceTrend();
					projectPriceTrend.setProjectId(priceTrendInput.getProjectId());
					projectPriceTrend.setTypeId(priceTrendInput.getTypeId());
					projectPriceTrend.setListingName(priceTrendInput.getListingName());
					projectPriceTrend.setProjectName(priceTrendInput.getProjectName());
					
					while(priceDateItr.hasNext()){
						String dateKey = priceDateItr.next();
						//exclude current and latest time stamp
						if(!dateKey.equals("current") && !dateKey.equals("latest")){
							Map<String, Map<String, ProjectPriceDetail>> projectsPriceMap = prices.get(dateKey);
							if (projectsPriceMap != null
									&& !projectsPriceMap.isEmpty()) {
								Map<String, ProjectPriceDetail> priceDetailsForTypeIdIdMap = projectsPriceMap
										.get(priceTrendInput.getProjectId()
												.toString());
								if (priceDetailsForTypeIdIdMap != null) {
									PriceDetail priceDetail = getPriceDetailObj(
											priceTrendInput.getTypeId(),
											priceDetailsForTypeIdIdMap);
									if (priceDetail != null) {
										projectPriceTrend.addPrice(priceDetail);
									}
								}
								
							}
						}
					}
					projectPriceTrends.add(projectPriceTrend);
				}
			}
			
		}
		sortPricesByDateAsc(projectPriceTrends);
		return projectPriceTrends;
	}

	/**
	 * Sorting ProjectPriceTrend list by date
	 * @param projectHistoryList
	 */
	private void sortPricesByDateAsc(List<ProjectPriceTrend> projectHistoryList) {
		for(ProjectPriceTrend priceTrend: projectHistoryList){
			if(priceTrend.getPrices() != null){
				Collections.sort(priceTrend.getPrices(), new Comparator<PriceDetail>() {
					@Override
					public int compare(PriceDetail p1, PriceDetail p2) {
						return p1.getEffectiveDate().compareTo(p2.getEffectiveDate());
					}
				});
			}
			
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
		
		boolean found = false;
		if(priceDetailsForTypeIdIdMap != null){
			PriceDetail priceDetail = new PriceDetail();
			double price = 0.0D;
			Date date = null;
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
		
		return null;
	}
}
