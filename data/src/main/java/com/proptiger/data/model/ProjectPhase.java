package com.proptiger.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.core.enums.DataVersion;
import com.proptiger.core.enums.EntityType;
import com.proptiger.core.enums.Status;
import com.proptiger.core.enums.UnitType;
import com.proptiger.core.model.BaseModel;
import com.proptiger.core.model.cms.Listing;
import com.proptiger.core.model.cms.ProjectSupply;
import com.proptiger.core.model.cms.Property;
import com.proptiger.data.enums.ConstructionStatus;
import com.proptiger.data.util.Constants;
import com.proptiger.data.util.DateUtil;

/**
 * Model for project phases
 * 
 * @author azi
 * 
 */

@JsonInclude(Include.NON_NULL)
@Entity
@JsonFilter("fieldFilter")
@Table(name = "cms.resi_project_phase")
public class ProjectPhase extends BaseModel {
    private static final long    serialVersionUID = 1L;

    @Id
    @Column(name = "PHASE_ID")
    private Integer              phaseId;

    @Enumerated(EnumType.STRING)
    private DataVersion          version;

    @Enumerated(EnumType.STRING)
    @Column(name = "PHASE_TYPE")
    private EntityType           phaseType;

    @Column(name = "PROJECT_ID")
    private Integer              projectId;

    @Column(name = "PHASE_NAME")
    private String               phaseName;

    @Column(name = "LAUNCH_DATE")
    private Date                 launchDate;

    @Column(name = "PRE_LAUNCH_DATE")
    private Date                 preLaunchDate;

    @Column(name = "COMPLETION_DATE")
    private Date                 completionDate;

    @Column(name = "REMARKS")
    private String               remarks;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private Status               status;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "BOOKING_STATUS_ID", referencedColumnName = "id")
    private BookingStatus        bookingStatus;

    @Column(name = "updated_by")
    private Integer              updatedBy;

    @Column(name = "created_at")
    private Date                 createdAt;

    @Column(name = "updated_at")
    private Date                 updatedAt;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "phaseId", cascade = CascadeType.ALL)
    private List<Listing>        listings         = new ArrayList<>();

    @Transient
    private List<Property>       properties       = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "phaseId", fetch = FetchType.LAZY)
    private List<SecondaryPrice> secondaryPrices;

    @Transient
    private Integer              supply           = 0;

    @Transient
    private Integer              launchedUnit     = 0;

    @Transient
    private Integer              sumAvailability;

    @Transient
    private boolean              isResale         = true;

    @Transient
    private boolean              isPrimary        = true;

    @Transient
    private boolean              isSoldOut        = false;

    @PostLoad
    private void populatePostLoadAttributes() {
        for (Listing listing : listings) {
            if (listing.getStatus().equals(Status.Active)) {
                List<ProjectSupply> supplies = listing.getProjectSupply();
                for (ProjectSupply projectSupply : supplies) {
                    if (projectSupply.getVersion().equals(this.version)) {
                        this.supply += projectSupply.getSupply();
                        this.launchedUnit += projectSupply.getLaunchedUnit();
                    }
                }
            }
        }
    }

    public Integer getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(Integer phaseId) {
        this.phaseId = phaseId;
    }

    public DataVersion getVersion() {
        return version;
    }

    public void setVersion(DataVersion version) {
        this.version = version;
    }

    public EntityType getPhaseType() {
        return phaseType;
    }

    public void setPhaseType(EntityType phaseType) {
        this.phaseType = phaseType;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getPhaseName() {
        return phaseName;
    }

    public void setPhaseName(String phaseName) {
        this.phaseName = phaseName;
    }

    public Date getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(Date launchDate) {
        this.launchDate = launchDate;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Listing> getListings() {
        return listings;
    }

    public void setListings(List<Listing> listings) {
        this.listings = listings;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public Integer getSupply() {
        return supply;
    }

    public void setSupply(Integer supply) {
        this.supply = supply;
    }

    public Integer getLaunchedUnit() {
        return launchedUnit;
    }

    public void setLaunchedUnit(Integer launchedUnit) {
        this.launchedUnit = launchedUnit;
    }

    public Integer getSumAvailability() {
        return sumAvailability;
    }

    public void setSumAvailability(Integer sumAvailability) {
        this.sumAvailability = sumAvailability;
    }

    public List<SecondaryPrice> getSecondaryPrices() {
        return secondaryPrices;
    }

    public void setSecondaryPrices(List<SecondaryPrice> secondaryPrices) {
        this.secondaryPrices = secondaryPrices;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public boolean getIsResale() {
        return isResale;
    }

    public void setIsResale(boolean isResale) {
        this.isResale = isResale;
    }

    public boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public boolean getIsSoldOut() {
        return isSoldOut;
    }

    public void setIsSoldOut(boolean isSoldOut) {
        this.isSoldOut = isSoldOut;
    }

    public Set<Integer> getSupplyIdsForActiveListing() {
        Set<Integer> supplyIds = new HashSet<>();
        for (Listing listing : this.listings) {
            if (listing.getStatus().equals(Status.Active)) {
                for (ProjectSupply projectSupply : listing.getProjectSupply()) {
                    if (projectSupply.getVersion().equals(this.version)) {
                        supplyIds.add(projectSupply.getId());
                    }
                }
            }
        }
        return supplyIds;
    }

    public Set<Integer> getPropertyIdsForActiveListing() {
        Set<Integer> propertyIds = new HashSet<>();
        for (Listing listing : this.listings) {
            if (listing.getStatus().equals(Status.Active)) {
                propertyIds.add(listing.getPropertyId());
            }
        }
        return propertyIds;
    }

    public Set<Integer> getActiveListingIds() {
        Set<Integer> result = new HashSet<>();
        for (Listing listing : listings) {
            result.add(listing.getId());
        }
        return result;
    }

    public ProjectPhase populateResaleStatus(ConstructionStatus projectConstructionStatus) {
        this.isResale = !projectConstructionStatus.equals(ConstructionStatus.OnHold) && ((this.sumAvailability == null && Constants.CONSTRUCTION_STATUS_FOR_RESALE
                .contains(projectConstructionStatus)) || (this.sumAvailability != null && this.sumAvailability == 0) || (this.secondaryPrices != null && this.secondaryPrices
                .size() > 0));
        return this;
    }

    public ProjectPhase populatePrimaryStatus(ConstructionStatus projectConstructionStatus) {
        this.isPrimary = true;

        if (this.sumAvailability == null) {
            if (!Constants.CONSTRUCTION_STATUS_FOR_PRIMARY.contains(projectConstructionStatus)) {
                this.isPrimary = false;
            }
        }
        else if (this.sumAvailability == 0) {
            if (this.completionDate != null && this.completionDate.getTime() < DateUtil.shiftMonths(new Date(), 6)
                    .getTime()) {
                this.isPrimary = false;
            }
        }
        return this;
    }

    public ProjectPhase populateSoldStatus() {
        if (this.sumAvailability != null && this.sumAvailability == 0) {
            this.isSoldOut = true;
        }
        else {
            this.isSoldOut = false;
        }
        return this;
    }

    public static class CustomCurrentPhaseSecondaryPrice implements Serializable {
        private static final long serialVersionUID = 1L;
        private int               phaseId;
        private UnitType          unitType;
        private Date              effectiveMonth;
        private Integer           secondaryPrice;

        public CustomCurrentPhaseSecondaryPrice(
                int phaseId,
                UnitType unitType,
                Date effectiveMonth,
                Integer secondaryPrice) {
            this.phaseId = phaseId;
            this.unitType = unitType;
            this.effectiveMonth = effectiveMonth;
            this.secondaryPrice = secondaryPrice;
        }

        public int getPhaseId() {
            return phaseId;
        }

        public void setPhaseId(int phaseId) {
            this.phaseId = phaseId;
        }

        public UnitType getUnitType() {
            return unitType;
        }

        public void setUnitType(UnitType unitType) {
            this.unitType = unitType;
        }

        public Date getEffectiveMonth() {
            return effectiveMonth;
        }

        public void setEffectiveMonth(Date effectiveMonth) {
            this.effectiveMonth = effectiveMonth;
        }

        public Integer getSecondaryPrice() {
            return secondaryPrice;
        }

        public void setSecondaryPrice(Integer secondaryPrice) {
            this.secondaryPrice = secondaryPrice;
        }
    }

    public Date getPreLaunchDate() {
        return preLaunchDate;
    }

    public void setPreLaunchDate(Date preLaunchDate) {
        this.preLaunchDate = preLaunchDate;
    }
}