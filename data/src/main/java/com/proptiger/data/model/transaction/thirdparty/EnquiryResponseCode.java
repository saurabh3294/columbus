package com.proptiger.data.model.transaction.thirdparty;

public enum EnquiryResponseCode {
    SuccessPayment("0"), RefundInitiated("8"), RefundSuccess("11");
    
    String responseCode;
    
    private EnquiryResponseCode(String code){
        this.responseCode = code;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }
}
