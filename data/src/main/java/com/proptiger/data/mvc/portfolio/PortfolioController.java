package com.proptiger.data.mvc.portfolio;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.portfolio.OverallReturn;
import com.proptiger.data.model.portfolio.Portfolio;
import com.proptiger.data.model.portfolio.ReturnType;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;

/**
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "data/v1/entity/{userId}/portfolio")
public class PortfolioController {

	private List<Portfolio> portfolioList;
	@PostConstruct
	public void init(){
		createDummyPortfolio(5);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ProAPIResponse getPortfolio(@PathVariable String userId){
		return new ProAPISuccessResponse(portfolioList, portfolioList.size());
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/{portfolioId}")
	@ResponseBody
	public ProAPIResponse getPortfolioById(@PathVariable String userId, @PathVariable Integer portfolioId){
		Portfolio portfolio = null;
		for(Portfolio p: portfolioList){
			if(p.getId() == portfolioId){
				portfolio = p;
				break;
			}
		}
		
		return new ProAPISuccessResponse(portfolio);
	}
	
	private void createDummyPortfolio(int count){
		portfolioList = new ArrayList<>();
		for(int i = 1; i <= count; i++){
			Portfolio portfolio = new Portfolio();
			portfolio.setCurrentValue(50000 + Math.random() * 10000);
			portfolio.setId(123 + i*10);
			portfolio.setOriginalVaue(50000 + Math.random() * 10000);
			OverallReturn overallReturn = createOverAllReturn(portfolio.getCurrentValue(), portfolio.getOriginalVaue());
			portfolio.setOverallReturn(overallReturn );
			List<Integer> propertiesId = new ArrayList<Integer>();
			propertiesId.add(123456 + i*10);
			propertiesId.add(123457 + i*10);
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
