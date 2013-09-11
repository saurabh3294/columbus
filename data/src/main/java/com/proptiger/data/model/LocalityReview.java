package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * Locality review model class
 * @author Rajeev Pandey
 * 
 */
@Entity
@Table(name = "LOCALITY_REVIEW")
@ResourceMetaInfo(name = "Locality Review")
public class LocalityReview {

	@FieldMetaInfo(name = "review_id", displayName = "Review Id", dataType = DataType.STRING, description = "Review Id")
	@Column(name = "REVIEW_ID")
	@JsonProperty(value = "review_id")
	@Id
	private long reviewId;
	@FieldMetaInfo(name = "user_id", displayName = "User Id", dataType = DataType.STRING, description = "User Id")
	@Column(name = "USER_ID")
	@JsonProperty(value = "user_id")
	private long userId;
	@FieldMetaInfo(name = "locality_id", displayName = "Locality Id", dataType = DataType.STRING, description = "Locality Id")
	@Column(name = "LOCALITY_ID")
	@JsonProperty(value = "locality_id")
	private long localityId;
	@FieldMetaInfo(name = "overall_rating", displayName = "Overall Rating", dataType = DataType.STRING, description = "Overall Rating")
	@Column(name = "OVERALL_RATING")
	@JsonProperty(value = "overall_rating")
	private float overallRating;
	@FieldMetaInfo(name = "location", displayName = "Location", dataType = DataType.STRING, description = "Location")
	@Column(name = "LOCATION")
	@JsonProperty(value = "location")
	private float location;
	@FieldMetaInfo(name = "safety", displayName = "Safety", dataType = DataType.STRING, description = "Safety")
	@Column(name = "SAFETY")
	@JsonProperty(value = "safety")
	private float safety;
	@FieldMetaInfo(name = "pub_trans", displayName = "Public Transport", dataType = DataType.STRING, description = "Public Transport")
	@Column(name = "PUB_TRANS")
	@JsonProperty(value = "pub_trans")
	private float pubTrans;
	@FieldMetaInfo(name = "rest_shop", displayName = "Rest Shop", dataType = DataType.STRING, description = "Rest Shop")
	@Column(name = "REST_SHOP")
	@JsonProperty(value = "rest_shop")
	private float restShop;
	@FieldMetaInfo(name = "schools", displayName = "Schools", dataType = DataType.STRING, description = "Schools")
	@Column(name = "SCHOOLS")
	@JsonProperty(value = "schools")
	private float schools;
	@FieldMetaInfo(name = "parks", displayName = "Parks", dataType = DataType.STRING, description = "Parks")
	@Column(name = "PARKS")
	@JsonProperty(value = "parks")
	private float parks;
	@FieldMetaInfo(name = "traffic", displayName = "Traffic", dataType = DataType.STRING, description = "Traffic")
	@Column(name = "TRAFFIC")
	@JsonProperty(value = "traffic")
	private float traffic;
	@FieldMetaInfo(name = "hospitals", displayName = "Hospitals", dataType = DataType.STRING, description = "Hospitals")
	@Column(name = "HOSPITALS")
	@JsonProperty(value = "hospitals")
	private float hospitals;
	@FieldMetaInfo(name = "civic", displayName = "Civic", dataType = DataType.STRING, description = "Civic")
	@Column(name = "CIVIC")
	@JsonProperty(value = "civic")
	private float civic;

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

	public long getLocalityId() {
		return localityId;
	}

	public void setLocalityId(long localityId) {
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
