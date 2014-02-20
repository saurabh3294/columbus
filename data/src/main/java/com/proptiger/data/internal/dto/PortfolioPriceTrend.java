package com.proptiger.data.internal.dto;

import java.util.List;

/**
 * @author Rajeev Pandey
 * 
 */
public class PortfolioPriceTrend {

    // private List<PriceDetail> portfolioPriceTrend;

    private List<ProjectPriceTrend> projectPriceTrend;

    // public List<PriceDetail> getPortfolioPriceTrend() {
    // return portfolioPriceTrend;
    // }
    //
    // public void setPortfolioPriceTrend(List<PriceDetail> portfolioPriceTrend)
    // {
    // this.portfolioPriceTrend = portfolioPriceTrend;
    // }

    public List<ProjectPriceTrend> getProjectPriceTrend() {
        return projectPriceTrend;
    }

    public void setProjectPriceTrend(List<ProjectPriceTrend> projectPriceTrend) {
        this.projectPriceTrend = projectPriceTrend;
    }

}
