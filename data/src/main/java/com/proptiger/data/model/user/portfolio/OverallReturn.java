package com.proptiger.data.model.user.portfolio;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.proptiger.data.enums.portfolio.ReturnType;
import com.proptiger.data.model.BaseModel;

/**
 * @author Rajeev Pandey
 * 
 */
@JsonFilter("fieldFilter")
public class OverallReturn extends BaseModel {

    private static final long serialVersionUID = 656252525344007387L;

    private ReturnType        returnType;

    private double        changeAmount;

    private double        changePercent;

    public ReturnType getReturnType() {
        return returnType;
    }

    public void setReturnType(ReturnType returnType) {
        this.returnType = returnType;
    }

    public double getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(double changeAmount) {
        this.changeAmount = changeAmount;
    }

    public double getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(double changePercent) {
        this.changePercent = changePercent;
    }

}
