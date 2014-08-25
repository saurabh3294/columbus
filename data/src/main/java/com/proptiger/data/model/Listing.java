package com.proptiger.data.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.proptiger.data.enums.Status;

/**
 * 
 * @author azi
 * @author Rajeev
 * 
 */
@Entity
@Table(name = "cms.listings")
@JsonFilter("fieldFilter")
public class Listing extends BaseModel {
    
    private static final long   serialVersionUID = 1L;
    
    @Id
    private Integer             id;
    
    @Column(name = "option_id")
    private Integer             propertyId;
    
    @Column(name = "phase_id")
    private Integer             phaseId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status              status;
    
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "listingId", cascade = CascadeType.ALL)
    private List<ProjectSupply> projectSupply;
    
    @OneToMany(mappedBy = "listingId", fetch = FetchType.LAZY)
    private List<ListingPrice>  listingPrices;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Integer propertyId) {
        this.propertyId = propertyId;
    }

    public Integer getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(Integer phaseId) {
        this.phaseId = phaseId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<ProjectSupply> getProjectSupply() {
        return projectSupply;
    }

    public void setProjectSupply(List<ProjectSupply> projectSupply) {
        this.projectSupply = projectSupply;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public List<ListingPrice> getListingPrices() {
        return listingPrices;
    }

    public void setListingPrices(List<ListingPrice> listingPrices) {
        this.listingPrices = listingPrices;
    }
}
