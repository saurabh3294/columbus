package com.proptiger.data.service.portfolio;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.proptiger.data.model.portfolio.OverallReturn;
import com.proptiger.data.model.portfolio.Portfolio;
import com.proptiger.data.model.portfolio.ReturnType;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 * A service class that provides CRUD operations for profile resource.
 * 
 * @author Rajeev Pandey
 *
 */
@Service
public class PortfolioService {

	private List<Portfolio> portfolioList;
	
	@PostConstruct
	public void init(){
		createDummyPortfolio(5);
	}
	
	public List<Portfolio> getPortfolio(Integer portfolioId){
		if(portfolioId == null){
			return portfolioList;
		}
		Portfolio portfolio = null;
		for(Portfolio p: portfolioList){
			if(p.getId() == portfolioId.intValue()){
				portfolio = p;
				break;
			}
		}
		if(portfolio == null){
			throw new ResourceNotAvailableException("Resource id "+portfolioId+" not available");
		}
		List<Portfolio> list = new ArrayList<>();
		list.add(portfolio);
		return list;
	}
	
	
	private void createDummyPortfolio(int count){
		int id = 1000;
		int propertyId = 10000;
		portfolioList = new ArrayList<>();
		for(int i = 1; i <= count; i++){
			Portfolio portfolio = new Portfolio();
			portfolio.setCurrentValue(50000 + Math.random() * 10000);
			portfolio.setId(id + i);
			portfolio.setOriginalVaue(50000 + Math.random() * 10000);
			OverallReturn overallReturn = createOverAllReturn(portfolio.getCurrentValue(), portfolio.getOriginalVaue());
			portfolio.setOverallReturn(overallReturn );
			List<Integer> propertiesId = new ArrayList<Integer>();
			propertiesId.add(propertyId ++);
			propertiesId.add(propertyId ++);
			portfolio.setPropertiesId(propertiesId );
			portfolioList.add(portfolio);
		}
		
	}

	private OverallReturn createOverAllReturn(double currentValue,
			double originalVaue) {
		OverallReturn overallReturn = new OverallReturn();
		double change = currentValue - originalVaue;
		overallReturn.setChangeAmount(Math.abs(change));
		if(change < 0){
			overallReturn.setReturnType(ReturnType.DECLINE);
		}
		else if(change > 0){
			overallReturn.setReturnType(ReturnType.APPRECIATION);
		}
		else{
			overallReturn.setReturnType(ReturnType.NOCHANGE);
		}
		double changePercent = (Math.abs(change)/originalVaue)*100;
		overallReturn.setChangePercent(changePercent);
		return overallReturn;
	}

}
