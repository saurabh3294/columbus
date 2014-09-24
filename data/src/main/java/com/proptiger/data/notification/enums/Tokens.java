package com.proptiger.data.notification.enums;

public class Tokens {

    public enum Default {
        Subject, Body, Template;
    }

    public enum PortfolioPriceChange {
        ProjectName, PropertyName, PercentageChangeString, AbsolutePercentageDifference;
    }

    public enum PortfolioGoalPrice {
        ProjectName, PropertyName;
    }

    public enum PortfolioPhotoAdd {
        ProjectName, PropertyName;
    }

    public enum PortfolioProjectUpdates {
        ProjectName, PropertyName;
    }

    public enum CouponIssued {
        CouponCode, Date;
    }

    public enum CouponCancelled {
        CouponCode;
    }

    public enum CouponRefunded {
        CouponCode;
    }

    public enum CouponRedeemed {
        CouponCode;
    }

}
