/**
 * 
 */
package com.proptiger.data.model.transaction.thirdparty;

/**
 * @author mandeep
 * 
 */
public class CitrusPayPGPaymentResponseData {
    private int    TxId;
    private String TxRefNo;
    private long   pgTxnNo;
    private String transactionID;
    private String TxStatus;
    private double amount;
    private String TxMsg;
    private String firstName;
    private String lastName;
    private String email;
    private String addressStreet1;
    private String addressCity;
    private String addressZip;
    private String addressState;
    private String addressCountry;
    private String mobileNo;
    private String signature;
    private int pgRespCode;
    private String issuerRefNo;
    private String authIdCode;

    public int getTxId() {
        return TxId;
    }

    public void setTxId(int txId) {
        TxId = txId;
    }

    public String getTxRefNo() {
        return TxRefNo;
    }

    public void setTxRefNo(String txRefNo) {
        TxRefNo = txRefNo;
    }

    public long getPgTxnNo() {
        return pgTxnNo;
    }

    public void setPgTxnNo(long pgTxnNo) {
        this.pgTxnNo = pgTxnNo;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getTxStatus() {
        return TxStatus;
    }

    public void setTxStatus(String txStatus) {
        TxStatus = txStatus;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTxMsg() {
        return TxMsg;
    }

    public void setTxMsg(String txMsg) {
        TxMsg = txMsg;
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

    public String getAddressCountry() {
        return addressCountry;
    }

    public void setAddressCountry(String addressCountry) {
        this.addressCountry = addressCountry;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getPgRespCode() {
        return pgRespCode;
    }

    public void setPgRespCode(int pgRespCode) {
        this.pgRespCode = pgRespCode;
    }

    public String getIssuerRefNo() {
        return issuerRefNo;
    }

    public void setIssuerRefNo(String issuerRefNo) {
        this.issuerRefNo = issuerRefNo;
    }

    public String getAuthIdCode() {
        return authIdCode;
    }

    public void setAuthIdCode(String authIdCode) {
        this.authIdCode = authIdCode;
    }

}
