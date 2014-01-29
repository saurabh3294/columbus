package com.proptiger.data.model;

import java.util.Map;

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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * Locality review model class, this class is basically consisting locality
 * ratings relate data for various categories like transport, safety etc.
 * 
 * @author Rajeev Pandey
 * 
 */
@Entity
@Table(name = "LOCALITY_REVIEW")
@ResourceMetaInfo
@JsonFilter("fieldFilter")
public class LocalityRatings implements BaseModel{
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

	public void update(LocalityRatings newRatings) {
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
	
	/**
	 * This class holds the average rating of all category for a locality, while
	 * calculating average it does not include null and zero values
	 * 
	 * @author Rajeev Pandey
	 * 
	 */
	@JsonInclude(Include.NON_NULL)
	public static class LocalityAverageRatingByCategory implements BaseModel{
		
		private static final long serialVersionUID = 8265420768264430368L;

		public LocalityAverageRatingByCategory(Double overallRating, Double location, Double safety,
				Double pubTrans, Double restShop, Double schools, Double parks,
				Double traffic, Double hospitals, Double civic) {
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
		
		public LocalityAverageRatingByCategory() {
			super();
		}

		private Double overallRating;
		private Double location;
		private Double safety;
		private Double pubTrans;
		private Double restShop;
		private Double schools;
		private Double parks;
		private Double traffic;
		private Double hospitals;
		private Double civic;
		
		public Double getOverallRating() {
			return overallRating;
		}
		public Double getLocation() {
			return location;
		}
		public Double getSafety() {
			return safety;
		}
		public Double getPubTrans() {
			return pubTrans;
		}
		public Double getRestShop() {
			return restShop;
		}
		public Double getSchools() {
			return schools;
		}
		public Double getParks() {
			return parks;
		}
		public Double getTraffic() {
			return traffic;
		}
		public Double getHospitals() {
			return hospitals;
		}
		public Double getCivic() {
			return civic;
		}
	}
	
	/**
	 * A kind of DTO from Dao layer to service later. Use this object in Select
	 * clause of query with NEW operator
	 * 
	 * @author Rajeev Pandey
	 * 
	 */
	@JsonInclude(Include.NON_NULL)
	public static class LocalityRatingUserCount implements BaseModel{
		private static final long serialVersionUID = 423920351135118515L;
		private Double rating;
		private long userCount;
		
		public LocalityRatingUserCount(Double rating, long userCount){
			this.rating = rating;
			this.userCount = userCount;
		}
				
		public Double getRating() {
			return rating;
		}

		public long getUserCount() {
			return userCount;
		}
	}
	
	/**
	 * This class contains locality rating details, like total ratings, user
	 * count rating wise, and average rating od locality
	 * 
	 * @author Rajeev Pandey
	 * 
	 */
	@JsonInclude(Include.NON_NULL)
	public static class LocalityRatingDetails implements BaseModel{
		private static final long serialVersionUID = 6890543357432298905L;
		protected Map<Double, Long> totalUsersByRating;
		protected Double averageRatings;
		//totalRatings is total number users who rates the locality
		protected Long totalRatings;
		public LocalityRatingDetails(Map<Double, Long> totalUsersByRating,
				Double averageRatings, Long totalRatings) {
			super();
			this.totalUsersByRating = totalUsersByRating;
			this.averageRatings = averageRatings;
			this.totalRatings = totalRatings;
		}
		
		public LocalityRatingDetails() {
			super();
		}

		public Map<Double, Long> getTotalUsersByRating() {
			return totalUsersByRating;
		}
		public Double getAverageRatings() {
			return averageRatings;
		}
		public Long getTotalRatings() {
			return totalRatings;
		}
	}
}
