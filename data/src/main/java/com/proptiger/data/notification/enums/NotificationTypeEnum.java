package com.proptiger.data.notification.enums;

public enum NotificationTypeEnum {
    
    Default("default"),
    
    PortfolioPriceChange("portfolio_price_change"),
    PortfolioGoalPrice("portfolio_goal_price"),
    PortfolioPhotoAdd("portfolio_photo_add"),
    PortfolioProjectNews("portfolio_project_news"),
    PortfolioLocalityNews("portfolio_locality_news"),
    PortfolioProjectUpdates("portfolio_project_updates"),
    PortfolioUpdates("portfolio_updates"),
     
    MarketplaceDefault("marketplace_default"),
    
    CouponIssued("coupon_issued"),
    CouponCancelled("coupon_cancelled"),
    CouponRefunded("coupon_refunded"),
    CouponRedeemed("coupon_redeemed"),
    CouponPaymentFailure("coupon_payment_failure"),
    
    AppDownload("app_download");
    
    String name;
    
    private NotificationTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
