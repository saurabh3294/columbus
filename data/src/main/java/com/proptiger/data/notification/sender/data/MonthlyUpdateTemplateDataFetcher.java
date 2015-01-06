package com.proptiger.data.notification.sender.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.cms.Locality;
import com.proptiger.core.model.cms.Project;
import com.proptiger.core.model.cms.Property;
import com.proptiger.core.model.proptiger.Enquiry;
import com.proptiger.core.model.proptiger.PortfolioListing;
import com.proptiger.core.model.user.User;
import com.proptiger.data.model.LocalityReviewComments;
import com.proptiger.data.model.ProjectDiscussion;
import com.proptiger.data.notification.enums.NotificationTypeEnum;
import com.proptiger.data.notification.enums.Tokens;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;
import com.proptiger.data.service.EnquiryService;
import com.proptiger.data.service.LocalityReviewService;
import com.proptiger.data.service.PropertyService;
import com.proptiger.data.service.user.ProjectDiscussionsService;
import com.proptiger.data.service.user.portfolio.PortfolioService;
import com.proptiger.userservice.mvc.UserService;

@Service
public class MonthlyUpdateTemplateDataFetcher extends TemplateDataFetcher {

    @Autowired
    private UserService               userService;

    @Autowired
    private PortfolioService          portfolioService;

    @Autowired
    private PropertyService           propertyService;

    @Autowired
    private ProjectDiscussionsService projectDiscussionsService;

    @Autowired
    private LocalityReviewService     localityReviewService;

    @Autowired
    private EnquiryService            enquiryService;

    public Map<String, Object> fetchTemplateData(NotificationGenerated nGenerated) {
        NotificationMessagePayload payload = nGenerated.getNotificationMessagePayload();
        Map<String, List<NotificationMessagePayload>> payloadMap = new HashMap<String, List<NotificationMessagePayload>>();
        payloadMap = getMessagePayloadsMappedByType(payloadMap, payload);

        Map<String, Object> dataMap = new HashMap<String, Object>();
        Integer userId = nGenerated.getUserId();

        /**
         * Populating Username
         */
        User user = userService.getUserById(userId);
        dataMap.put("username", user.getFullName());

        /**
         * Price change property count
         */
        List<NotificationMessagePayload> priceChange = payloadMap.get(NotificationTypeEnum.PortfolioMonthlyPriceChange.getName());
        Integer propertyCount = 0;
        if (priceChange != null && !priceChange.isEmpty()) {
            propertyCount = priceChange.size();
        }
        dataMap.put("propertyCount", propertyCount);

        /**
         * Overall Price changes
         */
        List<PortfolioListing> listings = portfolioService.getAllActivePortfolioListings(userId);
        Double newPrice = 0.0;
        Double oldPrice = 0.0;
        Date firstPurchaseDate = null;
        List<Integer> propertyIds = new ArrayList<Integer>();

        for (PortfolioListing listing : listings) {
            Date purchaseDate = listing.getPurchaseDate();
            if (purchaseDate != null && (firstPurchaseDate == null || firstPurchaseDate.after(purchaseDate))) {
                firstPurchaseDate = purchaseDate;
            }
            newPrice += listing.getCurrentPrice();
            oldPrice += listing.getTotalPrice();
            propertyIds.add(listing.getTypeId());
        }
        dataMap.put("netWorth", String.format("%.0f", newPrice));
        if (oldPrice > newPrice) {
            dataMap.put("isPriceIncreased", Boolean.FALSE);
            dataMap.put("priceChangeString", "Loss");
            dataMap.put("priceChangeValue", String.format("%.0f", oldPrice - newPrice));
            Double priceChangePercentage = ((oldPrice - newPrice) * 100) / oldPrice;
            dataMap.put("priceChangePercentage", String.format("%.1f", priceChangePercentage));
        }
        else {
            dataMap.put("isPriceIncreased", Boolean.TRUE);
            dataMap.put("priceChangeString", "Gain");
            dataMap.put("priceChangeValue", String.format("%.0f", newPrice - oldPrice));
            Double priceChangePercentage = ((newPrice - oldPrice) * 100) / oldPrice;
            dataMap.put("priceChangePercentage", String.format("%.1f", priceChangePercentage));
        }

        if (firstPurchaseDate == null) {
            dataMap.put("purchaseMonths", 0);
        }
        else {
            Long timeDiff = (new Date()).getTime() - firstPurchaseDate.getTime();
            Long purchaseMonths = timeDiff / 2592000000L;
            dataMap.put("purchaseMonths", purchaseMonths);
        }

        /**
         * Construction Updates
         */
        Integer constructionUpdateTotalCount = 0;
        Integer constructionProjectCount = 0;
        List<Integer> constructionImageCount = new ArrayList<Integer>();
        List<String> constructionProjectName = new ArrayList<String>();

        List<NotificationMessagePayload> photoAddPayloadList = payloadMap
                .get(NotificationTypeEnum.PortfolioMonthlyPhotoAdd.getName());

        if (photoAddPayloadList != null) {
            for (NotificationMessagePayload photoAddPayload : photoAddPayloadList) {
                List<NotificationTypePayload> typePayloads = new ArrayList<NotificationTypePayload>();
                typePayloads = getAllChildNotificationTypePayloads(
                        typePayloads,
                        photoAddPayload.getNotificationTypePayload());
                constructionUpdateTotalCount += typePayloads.size();
                constructionImageCount.add(typePayloads.size());
                constructionProjectName.add((String) photoAddPayload.getExtraAttributes().get(
                        Tokens.PortfolioPhotoAdd.ProjectName.name()));
                constructionProjectCount++;
            }
        }

        if (constructionProjectCount > 2) {
            constructionProjectCount = 2;
        }

        dataMap.put("constructionProjectCount", constructionProjectCount);
        dataMap.put("constructionUpdateTotalCount", constructionUpdateTotalCount);
        dataMap.put("constructionImageCount", constructionImageCount);
        dataMap.put("constructionProjectName", constructionProjectName);

        List<Project> projects = new ArrayList<Project>();
        List<Locality> localities = new ArrayList<Locality>();
        for (Integer propertyId : propertyIds) {
            Property property = propertyService.getPropertyFromSolr(propertyId);
            projects.add(property.getProject());
            localities.add(property.getProject().getLocality());
        }

        /**
         * Project Enquiry
         */
        Integer enquiryTotalCount = 0;
        Integer enquiryProjectCount = 0;
        List<Integer> enquiryCount = new ArrayList<Integer>();
        List<String> enquiryProjectName = new ArrayList<String>();

        for (Project project : projects) {
            List<Enquiry> enquiries = enquiryService.getEnquiriesForProjectIdInLastMonth(project.getProjectId());
            if (enquiries != null && enquiries.size() > 0) {
                enquiryTotalCount += enquiries.size();
                enquiryCount.add(enquiries.size());
                enquiryProjectName.add(project.getName());
                enquiryProjectCount++;
            }
        }

        if (enquiryProjectCount > 2) {
            enquiryProjectCount = 2;
        }

        dataMap.put("enquiryTotalCount", enquiryTotalCount);
        dataMap.put("enquiryProjectCount", enquiryProjectCount);
        dataMap.put("enquiryCount", enquiryCount);
        dataMap.put("enquiryProjectName", enquiryProjectName);

        /**
         * Project Discussion
         */
        Integer discussionProjectCount = 0;
        List<String> discussionPersonName = new ArrayList<String>();
        List<String> discussionProjectName = new ArrayList<String>();
        List<String> discussionComment = new ArrayList<String>();

        for (Project project : projects) {
            List<ProjectDiscussion> discussions = projectDiscussionsService.getCommentsForProjectIdInLastMonth(project
                    .getProjectId());
            if (discussions != null && discussions.size() > 0) {
                User commentUser = userService.getUserById(discussions.get(0).getUserId());
                discussionPersonName.add(commentUser.getFullName());
                discussionProjectName.add(project.getName());
                discussionComment.add(discussions.get(0).getComment());
                discussionProjectCount++;
            }
        }

        if (discussionProjectCount > 2) {
            discussionProjectCount = 2;
        }

        dataMap.put("discussionProjectCount", discussionProjectCount);
        dataMap.put("discussionPersonName", discussionPersonName);
        dataMap.put("discussionProjectName", discussionProjectName);
        dataMap.put("discussionComment", discussionComment);

        /**
         * Locality Reviews
         */
        Integer reviewLocalityCount = 0;
        List<String> reviewPersonName = new ArrayList<String>();
        List<String> reviewLocalityName = new ArrayList<String>();
        List<String> reviewComment = new ArrayList<String>();
        List<String> reviewLabel = new ArrayList<String>();

        for (Locality locality : localities) {
            List<LocalityReviewComments> reviews = localityReviewService.getCommentsForLocalityIdInLastMonth(locality
                    .getLocalityId());
            if (reviews != null && reviews.size() > 0) {
                User commentUser = userService.getUserById(reviews.get(0).getUserId());
                reviewPersonName.add(commentUser.getFullName());
                reviewLocalityName.add(locality.getLabel());
                reviewComment.add(reviews.get(0).getReview());
                reviewLabel.add(reviews.get(0).getReviewLabel());
                reviewLocalityCount++;
            }
        }

        if (reviewLocalityCount > 2) {
            reviewLocalityCount = 2;
        }

        dataMap.put("reviewLocalityCount", reviewLocalityCount);
        dataMap.put("reviewPersonName", reviewPersonName);
        dataMap.put("reviewLocalityName", reviewLocalityName);
        dataMap.put("reviewComment", reviewComment);
        dataMap.put("reviewLabel", reviewLabel);

        /**
         * Project or Locality News
         */
        Integer newsCount = 0;
        List<String> newsContent = new ArrayList<String>();
        List<String> newslabel = new ArrayList<String>();

        List<NotificationMessagePayload> projectNews = payloadMap.get(NotificationTypeEnum.PortfolioMonthlyProjectNews.getName());
        if (projectNews != null && projectNews.size() > 0) {
            newsCount++;
            newslabel.add((String) projectNews.get(0).getExtraAttributes().get(Tokens.PortfolioNews.NewsTitle.name()));
            newsContent.add((String) projectNews.get(0).getExtraAttributes().get(Tokens.PortfolioNews.NewsBody.name()));
        }

        List<NotificationMessagePayload> localityNews = payloadMap
                .get(NotificationTypeEnum.PortfolioMonthlyLocalityNews.getName());
        if (localityNews != null && localityNews.size() > 0) {
            newsCount++;
            newslabel.add((String) localityNews.get(0).getExtraAttributes().get(Tokens.PortfolioNews.NewsTitle.name()));
            newsContent.add((String) localityNews.get(0).getExtraAttributes().get(Tokens.PortfolioNews.NewsBody.name()));
        }

        if (newsCount > 2) {
            newsCount = 2;
        }

        dataMap.put("newsCount", newsCount);
        dataMap.put("newslabel", newslabel);
        dataMap.put("newsContent", newsContent);

        return dataMap;
    }
}
