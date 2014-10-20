/**
 * 
 */
package com.proptiger.data.model.enums.transaction;

/**
 * @author mandeep
 * 
 */
public enum TransactionType {
    BuyCoupon(1);

    int id;

    private TransactionType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
