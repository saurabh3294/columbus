package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * Locality review model class
 * 
 * @author Rajeev Pandey
 * 
 */
@Entity
@Table(name = "LOCALITY_REVIEW")
@ResourceMetaInfo
public class LocalityReview implements BaseModel{
    @FieldMetaInfo(displayName = "Review Id", description = "Review Id")
    @Column(name = "REVIEW_ID")
    @Id
    private long reviewId;
    @FieldMetaInfo(displayName = "User Id", description = "User Id")
    @Column(name = "USER_ID")
    private long userId;
    @FieldMetaInfo(displayName = "Locality Id", description = "Locality Id")
    @Column(name = "LOCALITY_ID")
    private int localityId;
    @FieldMetaInfo(displayName = "Overall Rating", description = "Overall Rating")
    @Column(name = "OVERALL_RATING")
    private float overallRating;
    @FieldMetaInfo(displayName = "Location", description = "Location")
    @Column(name = "LOCATION")
    private float location;
    @FieldMetaInfo(displayName = "Safety", description = "Safety")
    @Column(name = "SAFETY")
    private float safety;
    @FieldMetaInfo(displayName = "Public Transport", description = "Public Transport")
    @Column(name = "PUB_TRANS")
    private float pubTrans;
    @FieldMetaInfo(displayName = "Rest Shop", description = "Rest Shop")
    @Column(name = "REST_SHOP")
    private float restShop;
    @FieldMetaInfo(displayName = "Schools", description = "Schools")
    @Column(name = "SCHOOLS")
    private float schools;
    @FieldMetaInfo(displayName = "Parks", description = "Parks")
    @Column(name = "PARKS")
    private float parks;
    @FieldMetaInfo(displayName = "Traffic", description = "Traffic")
    @Column(name = "TRAFFIC")
    private float traffic;
    @FieldMetaInfo(displayName = "Hospitals", description = "Hospitals")
    @Column(name = "HOSPITALS")
    private float hospitals;
    @FieldMetaInfo(displayName = "Civic", description = "Civic")
    private float civic;

    @ManyToOne
    @JoinColumn(name = "LOCALITY_ID", referencedColumnName = "LOCALITY_ID", insertable = false, updatable = false)
    @JsonIgnore
    private Locality locality;
    
    public long getReviewId() {
        return reviewId;
    }

    public void setReviewId(long reviewId) {
        this.reviewId = reviewId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getLocalityId() {
        return localityId;
    }

    public void setLocalityId(int localityId) {
        this.localityId = localityId;
    }

    public float getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(float overallRating) {
        this.overallRating = overallRating;
    }

    public float getLocation() {
        return location;
    }

    public void setLocation(float location) {
        this.location = location;
    }

    public float getSafety() {
        return safety;
    }

    public void setSafety(float safety) {
        this.safety = safety;
    }

    public float getPubTrans() {
        return pubTrans;
    }

    public void setPubTrans(float pubTrans) {
        this.pubTrans = pubTrans;
    }

    public float getRestShop() {
        return restShop;
    }

    public void setRestShop(float restShop) {
        this.restShop = restShop;
    }

    public float getSchools() {
        return schools;
    }

    public void setSchools(float schools) {
        this.schools = schools;
    }

    public float getParks() {
        return parks;
    }

    public void setParks(float parks) {
        this.parks = parks;
    }

    public float getTraffic() {
        return traffic;
    }

    public void setTraffic(float traffic) {
        this.traffic = traffic;
    }

    public float getHospitals() {
        return hospitals;
    }

    public void setHospitals(float hospitals) {
        this.hospitals = hospitals;
    }

    public float getCivic() {
        return civic;
    }

    public void setCivic(float civic) {
        this.civic = civic;
    }
}
