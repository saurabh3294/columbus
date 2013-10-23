package com.proptiger.data.service;

import java.util.Date;
import java.util.Iterator;
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
	
	public ProjectPriceHistory getProjectPriceHistory(Integer projectId, Integer typeId, Integer noOfMonths){
		ProjectPriceHistoryDetail response = cmsDao.getProjectPriceHistory(projectId, typeId, noOfMonths);
		return convertToInternalPriceHistory(response, projectId, typeId);
	}



	private ProjectPriceHistory convertToInternalPriceHistory(
			ProjectPriceHistoryDetail response, Integer projectId, Integer typeId) {
		ProjectPriceHistory projectPriceHistory = new ProjectPriceHistory();
		projectPriceHistory.setProjectId(projectId);
		projectPriceHistory.setTypeId(typeId);
		if(response != null){
			Map<String, Map<String, Map<String, ProjectPriceDetail>>> prices = response.getPrices();
			if(prices != null){
				Iterator<String> priceDateItr = prices.keySet().iterator();
				while(priceDateItr.hasNext()){
					PriceDetail priceDetail = new PriceDetail();
					String dateKey = priceDateItr.next();
					if(!dateKey.equals("current") && !dateKey.equals("latest")){
						Map<String, Map<String, ProjectPriceDetail>> projectPriceMap = prices.get(dateKey);
						if(projectPriceMap != null && !projectPriceMap.isEmpty()){
							
							Map<String, ProjectPriceDetail> priceDetailsForProjectIdMap = projectPriceMap.get(projectId.toString());
							if(priceDetailsForProjectIdMap != null && !priceDetailsForProjectIdMap.isEmpty()){
								if(typeId == null){
									// take average of price of all phase id and type id combination
									double total = 0.0D;
									Date date = null;
									for(ProjectPriceDetail price: priceDetailsForProjectIdMap.values()){
										total = total + price.getPrice();
										if(date == null){
											date = price.getEffective_date();
										}
									}
									priceDetail.setEffectiveDate(date);
									Integer avgPrice = (int) (total/priceDetailsForProjectIdMap.values().size());
									priceDetail.setPrice(avgPrice);
								}
								else{
									// get price for particular type id
								}
							}
							projectPriceHistory.addPrice(priceDetail);
						}
					}
				}
			}
		}
		return projectPriceHistory;
	}
}
