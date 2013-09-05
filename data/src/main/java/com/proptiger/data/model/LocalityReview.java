package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "LOCALITY_REVIEW")
public class LocalityReview {

	@Column(name = "REVIEW_ID")
	@JsonProperty(value = "review_id")
	@Id
	private long reviewId;
	@Column(name = "USER_ID")
	@JsonProperty(value = "user_id")
	private long userId;
	@Column(name = "LOCALITY_ID")
	@JsonProperty(value = "locality_id")
	private long localityId;
	@Column(name = "OVERALL_RATING")
	@JsonProperty(value = "overall_tating")
	private float overallRating;
	@Column(name = "LOCATION")
	@JsonProperty(value = "location")
	private float location;
	@Column(name = "SAFETY")
	@JsonProperty(value = "safety")
	private float safety;
	@Column(name = "PUB_TRANS")
	@JsonProperty(value = "pub_trans")
	private float pubTrans;
	@Column(name = "REST_SHOP")
	@JsonProperty(value = "rest_shop")
	private float restShop;
	@Column(name = "SCHOOLS")
	@JsonProperty(value = "schools")
	private float schools;
	@Column(name  = "PARKS")
	@JsonProperty(value = "parks")
	private float parks;
	@Column(name = "TRAFFIC")
	@JsonProperty(value = "traffic")
	private float traffic;
	@Column(name = "HOSPITALS")
	@JsonProperty(value = "hospitals")
	private float hospitals;
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
