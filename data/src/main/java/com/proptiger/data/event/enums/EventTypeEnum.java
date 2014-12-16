package com.proptiger.data.event.enums;

public enum EventTypeEnum {
        
    PortfolioPriceChange("portfolio_price_change"),
    PortfolioProjectNews("portfolio_project_news"),
    PortfolioLocalityNews("portfolio_locality_news");
    
    String name;
    
    private EventTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
