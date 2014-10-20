/**
 * 
 */
package com.proptiger.data.model.transaction.thirdparty;

/**
 * @author mandeep
 * 
 */
public class CitrusPayPGInitiatePaymentRequestData {
    private String key;
    private String merchantURLPart;
    private int    merchantTxnId;
    private int    orderAmount;
    private String currency = "INR";
    private String firstName;
    private String lastName;
    private String email;
    private String addressStreet1;
    private String addressCity;
    private String addressZip;
    private String addressState;
    private String phoneNumber;
    private String secSignature;
    private String returnUrl;
    private long   reqtime;
    private String notifyUrl;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMerchantURLPart() {
        return merchantURLPart;
    }

    public void setMerchantURLPart(String merchantURLPart) {
        this.merchantURLPart = merchantURLPart;
    }

    public int getMerchantTxnId() {
        return merchantTxnId;
    }

    public void setMerchantTxnId(int merchantTxnId) {
        this.merchantTxnId = merchantTxnId;
    }

    public int getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(int orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddressStreet1() {
        return addressStreet1;
    }

    public void setAddressStreet1(String addressStreet1) {
        this.addressStreet1 = addressStreet1;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public String getAddressZip() {
        return addressZip;
    }

    public void setAddressZip(String addressZip) {
        this.addressZip = addressZip;
    }

    public String getAddressState() {
        return addressState;
    }

    public void setAddressState(String addressState) {
        this.addressState = addressState;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSecSignature() {
        return secSignature;
    }

    public void setSecSignature(String secSignature) {
        this.secSignature = secSignature;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public long getReqtime() {
        return reqtime;
    }

    public void setReqtime(long reqtime) {
        this.reqtime = reqtime;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

}
