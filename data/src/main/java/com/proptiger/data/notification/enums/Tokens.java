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

    public enum PortfolioNews {
        NewsTitle, NewsBody;
    }

    public enum PortfolioProjectUpdates {
        ProjectName, PropertyName;
    }

    public enum CouponIssued {
        CouponCode, Date, UserName, ProjectName, UnitName, Size, Discount, CouponPrice, DiscountPrice;
    }

    public enum CouponCancelled {
        CouponCode, Date, UserName, ProjectName, UnitName, Size, Discount, CouponPrice, DiscountPrice;
    }

    public enum CouponRefunded {
        CouponCode, UserName, CouponPrice, TransactionId;
    }

    public enum CouponRedeemed {
        CouponCode, UserName, ProjectName, UnitName, Size, CouponPrice, DiscountPrice, RedeemedDate;
    }

    public enum CouponPaymentFailure {
        CouponCode, Date, UserName, ProjectName, UnitName, Size, Discount, CouponPrice, DiscountPrice, FailedAmount;
    }

}
