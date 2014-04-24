package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Model for master booking statuses
 * 
 * @author azi
 * 
 */

@Entity
@JsonInclude(Include.NON_NULL)
@Table(name = "cms.master_booking_statuses")
public class BookingStatus extends BaseModel {
    private static final long                            serialVersionUID = 1L;

    @Id
    private Integer                                      id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private com.proptiger.data.model.enums.BookingStatus bookingStatus;

    @Column(name = "display_name")
    private String                                       displayName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public com.proptiger.data.model.enums.BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(com.proptiger.data.model.enums.BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
