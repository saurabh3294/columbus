package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * Locality review model class, this class is basically consiting locality
 * ratings relate data.
 * 
 * @author Rajeev Pandey
 * 
 */
@Entity
@Table(name = "LOCALITY_REVIEW")
@ResourceMetaInfo
@JsonFilter("fieldFilter")
public class LocalityReview implements BaseModel{
	private static final long serialVersionUID = 7492287125669474763L;

	@FieldMetaInfo(displayName = "Review Id", description = "Review Id")
	@Column(name = "REVIEW_ID")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long reviewId;
	@FieldMetaInfo(displayName = "User Id", description = "User Id")
	@Column(name = "USER_ID")
	private Integer userId;
	@FieldMetaInfo(displayName = "Locality Id", description = "Locality Id")
	@Column(name = "LOCALITY_ID")
	private Integer localityId;
	@FieldMetaInfo(displayName = "Overall Rating", description = "Overall Rating")
	@Column(name = "OVERALL_RATING")
	private double overallRating;
	@FieldMetaInfo(displayName = "Location", description = "Location")
	@Column(name = "LOCATION")
	private double location;
	@FieldMetaInfo(displayName = "Safety", description = "Safety")
	@Column(name = "SAFETY")
	private double safety;
	@FieldMetaInfo(displayName = "Public Transport", description = "Public Transport")
	@Column(name = "PUB_TRANS")
	private double pubTrans;
	@FieldMetaInfo(displayName = "Rest Shop", description = "Rest Shop")
	@Column(name = "REST_SHOP")
	private double restShop;
	@FieldMetaInfo(displayName = "Schools", description = "Schools")
	@Column(name = "SCHOOLS")
	private double schools;
	@FieldMetaInfo(displayName = "Parks", description = "Parks")
	@Column(name = "PARKS")
	private double parks;
	@FieldMetaInfo(displayName = "Traffic", description = "Traffic")
	@Column(name = "TRAFFIC")
	private double traffic;
	@FieldMetaInfo(displayName = "Hospitals", description = "Hospitals")
	@Column(name = "HOSPITALS")
	private double hospitals;
	@FieldMetaInfo(displayName = "Civic", description = "Civic")
	@Column(name = "CIVIC")
	private double civic;

	@ManyToOne
	@JoinColumn(name = "LOCALITY_ID", referencedColumnName = "LOCALITY_ID", insertable = false, updatable = false)
	@JsonIgnore
	private Locality locality;

	public Long getReviewId() {
		return reviewId;
	}

	public void setReviewId(Long reviewId) {
		this.reviewId = reviewId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getLocalityId() {
		return localityId;
	}

	public void setLocalityId(Integer localityId) {
		this.localityId = localityId;
	}

	public double getOverallRating() {
		return overallRating;
	}

	public void setOverallRating(double overallRating) {
		this.overallRating = overallRating;
	}

	public double getLocation() {
		return location;
	}

	public void setLocation(double location) {
		this.location = location;
	}

	public double getSafety() {
		return safety;
	}

	public void setSafety(double safety) {
		this.safety = safety;
	}

	public double getPubTrans() {
		return pubTrans;
	}

	public void setPubTrans(double pubTrans) {
		this.pubTrans = pubTrans;
	}

	public double getRestShop() {
		return restShop;
	}

	public void setRestShop(double restShop) {
		this.restShop = restShop;
	}

	public double getSchools() {
		return schools;
	}

	public void setSchools(double schools) {
		this.schools = schools;
	}

	public double getParks() {
		return parks;
	}

	public void setParks(double parks) {
		this.parks = parks;
	}

	public double getTraffic() {
		return traffic;
	}

	public void setTraffic(double traffic) {
		this.traffic = traffic;
	}

	public double getHospitals() {
		return hospitals;
	}

	public void setHospitals(double hospitals) {
		this.hospitals = hospitals;
	}

	public double getCivic() {
		return civic;
	}

	public void setCivic(double civic) {
		this.civic = civic;
	}

	public void update(LocalityReview newRatings) {
		this.setCivic(newRatings.getCivic());
		this.setHospitals(newRatings.getHospitals());
		this.setLocation(newRatings.getLocation());
		this.setOverallRating(newRatings.getOverallRating());
		this.setParks(newRatings.getParks());
		this.setPubTrans(newRatings.getPubTrans());
		this.setRestShop(newRatings.getRestShop());
		this.setSafety(newRatings.getSafety());
		this.setSchools(newRatings.getSchools());
		this.setTraffic(newRatings.getTraffic());
	}
	
	public static class LocalityAverageRatingCategory{
		public LocalityAverageRatingCategory(double overallRating, double location, double safety,
				double pubTrans, double restShop, double schools, double parks,
				double traffic, double hospitals, double civic) {
			this.overallRating = overallRating;
			this.location = location;
			this.safety = safety;
			this.pubTrans = pubTrans;
			this.restShop = restShop;
			this.schools = schools;
			this.parks = parks;
			this.traffic = traffic;
			this.hospitals = hospitals;
			this.civic = civic;
		}
		private double overallRating;
		private double location;
		private double safety;
		private double pubTrans;
		private double restShop;
		private double schools;
		private double parks;
		private double traffic;
		private double hospitals;
		private double civic;
		
		public double getOverallRating() {
			return overallRating;
		}
		public double getLocation() {
			return location;
		}
		public double getSafety() {
			return safety;
		}
		public double getPubTrans() {
			return pubTrans;
		}
		public double getRestShop() {
			return restShop;
		}
		public double getSchools() {
			return schools;
		}
		public double getParks() {
			return parks;
		}
		public double getTraffic() {
			return traffic;
		}
		public double getHospitals() {
			return hospitals;
		}
		public double getCivic() {
			return civic;
		}
		
	}
}
