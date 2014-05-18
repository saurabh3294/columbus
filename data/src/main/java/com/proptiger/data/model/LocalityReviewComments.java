/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.model.LocalityRatings.LocalityRatingDetails;

/**
 * This class represents review comments for locality
 * 
 * @author mukand
 */
@Entity
@Table(name = "REVIEW_COMMENTS")
public class LocalityReviewComments extends BaseModel {
    private static final long serialVersionUID = 6324079051629045199L;

    @FieldMetaInfo(displayName = "Comment Id", description = "Comment Id")
    @Column(name = "COMMENT_ID")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer           commentId;

    @FieldMetaInfo(displayName = "User Id", description = "User Id")
    @Column(name = "USER_ID")
    private Integer           userId;

    @FieldMetaInfo(displayName = "Project Id", description = "Project Id")
    @Column(name = "PROJECT_ID")
    private int               projectId;

    @FieldMetaInfo(displayName = "Locality Id", description = "Locality Id")
    @Column(name = "LOCALITY_ID")
    private int               localityId;

    @FieldMetaInfo(displayName = "Likes Count", description = "Likes Count")
    @Column(name = "LIKES_COUNT")
    private int               likesCount;

    @FieldMetaInfo(displayName = "Review Label", description = "Review Label")
    @Column(name = "REVIEW_LABEL")
    private String            reviewLabel;

    @FieldMetaInfo(displayName = "Review", description = "User Review")
    @Column(name = "REVIEW")
    private String            review;

    @FieldMetaInfo(displayName = "Recommend", description = "Recommend")
    @Column(name = "RECOMMEND")
    private String            recommend;

    @FieldMetaInfo(displayName = "You know", description = "You Know")
    @Column(name = "YOU_KNOW")
    private String            youKnow;

    @FieldMetaInfo(displayName = "Comment Time", description = "Comment Time")
    @Column(name = "COMMENTTIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date              commenttime;

    @FieldMetaInfo(displayName = "User Name", description = "User Name")
    @Column(name = "USER_NAME")
    private String            userName;

    @FieldMetaInfo(displayName = "Status", description = "Status")
    @Column(name = "STATUS")
    private String            status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID", insertable = false, updatable = false)
    private ForumUser         forumUser;

    @OneToOne(fetch = FetchType.EAGER, optional=true)
    @JoinColumns({
            @JoinColumn(
                    name = "LOCALITY_ID",
                    referencedColumnName = "LOCALITY_ID",
                    insertable = false,
                    updatable = false),
            @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false, nullable = true)})
    private LocalityRatings   localityRatings;

    @ManyToOne
    @JoinColumn(name = "LOCALITY_ID", insertable = false, updatable = false)
    @JsonIgnore
    private Locality          locality;

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getLocalityId() {
        return localityId;
    }

    public void setLocalityId(int localityId) {
        this.localityId = localityId;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public String getReviewLabel() {
        return reviewLabel;
    }

    public void setReviewLabel(String reviewLabel) {
        this.reviewLabel = reviewLabel;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getRecommend() {
        return recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }

    public String getYouKnow() {
        return youKnow;
    }

    public void setYouKnow(String youKnow) {
        this.youKnow = youKnow;
    }

    public Date getCommenttime() {
        return commenttime;
    }

    public void setCommenttime(Date commenttime) {
        this.commenttime = commenttime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalityRatings getLocalityRatings() {
        return localityRatings;
    }

    public void setLocalityRatings(LocalityRatings localityRatings) {
        this.localityRatings = localityRatings;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    @PrePersist
    public void prePersist() {
        commenttime = new Date();
    }

    /**
     * This class will contain few details of a review for locality, like review
     * string, review label etc
     * 
     * @author Rajeev Pandey
     * 
     */
    @JsonInclude(Include.NON_NULL)
    public static class LocalityReviewCustomDetail extends BaseModel {
        private static final long serialVersionUID = -4616992564291158711L;
        private String            review;
        private String            reviewLabel;
        private String            username;
        private Date              commentTime;

        public LocalityReviewCustomDetail(
                String review,
                String reviewLabel,
                String username,
                Date commentTime,
                String commentUserName) {
            super();
            this.review = review;
            this.reviewLabel = reviewLabel;
            this.username = username;
            this.commentTime = commentTime;
            if (this.username == null) {
                this.username = commentUserName;
            }
        }

        public String getReview() {
            return review;
        }

        public String getReviewLabel() {
            return reviewLabel;
        }

        public String getUsername() {
            return username;
        }

        public Date getCommentTime() {
            return commentTime;
        }
    }

    /**
     * This class will contain review and rating details for a locality
     * 
     * @author Rajeev Pandey
     * 
     */
    @JsonInclude(Include.NON_NULL)
    public static class LocalityReviewRatingDetails extends BaseModel {
        private static final long                serialVersionUID = -4616279373858679214L;
        private Long                             totalReviews;
        private List<LocalityReviewCustomDetail> reviews;
        protected Map<Double, Long>              totalUsersByRating;
        protected Double                         averageRatings;
        // totalRatings is total number users who rates the locality
        protected Long                           totalRatings;

        public LocalityReviewRatingDetails(
                Long totalReviews,
                List<LocalityReviewCustomDetail> reviews,
                LocalityRatingDetails localityRatingDetails) {
            super();
            this.totalReviews = totalReviews;
            this.reviews = reviews;
            if (localityRatingDetails != null) {
                this.totalUsersByRating = localityRatingDetails.getTotalUsersByRating();
                this.averageRatings = localityRatingDetails.getAverageRatings();
                this.totalRatings = localityRatingDetails.getTotalRatings();
            }
        }

        public Long getTotalReviews() {
            return totalReviews;
        }

        public List<LocalityReviewCustomDetail> getReviews() {
            return reviews;
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
