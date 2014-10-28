/**
 * 
 */
package com.proptiger.data.model.transaction;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.core.model.BaseModel;

/**
 * @author mandeep
 * 
 */
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "proptiger.master_payment_statuses")
public class MasterPaymentStatus extends BaseModel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    private int               id;
    private String            label;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
