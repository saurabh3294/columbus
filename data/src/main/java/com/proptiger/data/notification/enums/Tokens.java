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
        CouponCode, Date, UserName, ProjectName, UnitName, Discount, CouponPrice, DiscountPrice;
    }

    public enum CouponCancelled {
        CouponCode, Date, UserName, ProjectName, UnitName, Discount, CouponPrice, DiscountPrice, Size;
    }

    public enum CouponRefunded {
        CouponCode, UserName, CouponPrice, TransactionId;
    }

    public enum CouponRedeemed {
        CouponCode, UserName, ProjectName, UnitName;
    }
    
    public enum CouponPaymentFailure {
        CouponCode, Date, UserName, ProjectName, UnitName, Discount, CouponPrice, DiscountPrice;
    }

}
