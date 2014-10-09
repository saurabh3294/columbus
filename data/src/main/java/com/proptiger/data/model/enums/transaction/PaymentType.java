/**
 * 
 */
package com.proptiger.data.model.enums.transaction;

/**
 * @author mandeep
 * 
 */
public enum PaymentType {
    Online(1), Cheque(2), Cash(3);

    int id;

    private PaymentType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
