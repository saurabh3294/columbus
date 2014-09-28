/**
 * 
 */
package com.proptiger.data.model.enums.transaction;

/**
 * @author mandeep
 * 
 */
public enum TransactionStatus {
    Incomplete(1), Complete(2), Refunded(3), CouponExercised(4), RefundInitiated(5);

    int id;

    private TransactionStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
