/**
 * 
 */
package com.proptiger.data.model.enums.transaction;

/**
 * @author mandeep
 * 
 */
public enum PaymentStatus {
    Success(1), Failed(2), Refunded(3);

    int id;

    private PaymentStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
