/**
 * 
 */
package com.proptiger.data.model.transaction.thirdparty;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.model.BaseModel;

/**
 * @author mandeep
 * 
 */
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "proptiger.payment_gateway_responses")
public class PaymentGatewayResponse extends BaseModel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)    
    private int    id;
    
    @Column(name="json_dump")
    private String jsonDump;

    @Column(name="created_at")
    private Date   createdAt = new Date();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJsonDump() {
        return jsonDump;
    }

    public void setJsonDump(String jsonDump) {
        this.jsonDump = jsonDump;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
