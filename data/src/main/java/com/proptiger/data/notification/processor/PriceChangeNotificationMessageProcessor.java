package com.proptiger.data.notification.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.proptiger.PortfolioListing;
import com.proptiger.core.model.user.User;
import com.proptiger.data.notification.enums.Tokens;
import com.proptiger.data.notification.model.NotificationTypeGenerated;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;

@Service
public class PriceChangeNotificationMessageProcessor extends NotificationMessageProcessor {

    private static Logger logger = LoggerFactory.getLogger(PriceChangeNotificationMessageProcessor.class);

    @Override
    public Map<Integer, NotificationMessagePayload> getNotificationMessagePayloadByUnsubscribedUserList(
            List<User> unsubscribedUserList,
            NotificationTypeGenerated ntGenerated) {

        Map<Integer, NotificationMessagePayload> payloadMap = new HashMap<Integer, NotificationMessagePayload>();

        NotificationTypePayload notificationTypePayload = ntGenerated.getNotificationTypePayload();
        Integer propertyId = ((Number) notificationTypePayload.getPrimaryKeyValue()).intValue();
        List<PortfolioListing> portfolioListings = getPortfolioListingsByPropertyId(propertyId);
        portfolioListings = removeUsersFromPortfolioListings(unsubscribedUserList, portfolioListings);
        
        if (portfolioListings == null) {
            logger.debug("No portfolio listing found for property id : " + propertyId);
            return payloadMap;
        }

        for (PortfolioListing portfolioListing : portfolioListings) {
            Double percentageDifference = getPercentageDifference(
                    portfolioListing.getBasePrice(),
                    portfolioListing.getListingSize(),
                    (Double) notificationTypePayload.getNewValue());

            Map<String, Object> userDataMap = new HashMap<String, Object>();
            userDataMap.put(Tokens.PortfolioPriceChange.ProjectName.name(), portfolioListing.getProjectName());
            userDataMap.put(Tokens.PortfolioPriceChange.PropertyName.name(), portfolioListing.getName());
            userDataMap.put(
                    Tokens.PortfolioPriceChange.AbsolutePercentageDifference.name(),
                    Math.abs(percentageDifference));
            userDataMap.put(
                    Tokens.PortfolioPriceChange.PercentageChangeString.name(),
                    getPercentageChangeString(percentageDifference));

            NotificationMessagePayload nmPayload = new NotificationMessagePayload();
            nmPayload.setExtraAttributes(userDataMap);
            nmPayload.setNotificationTypePayload(notificationTypePayload);

            payloadMap.put(portfolioListing.getUserId(), nmPayload);
        }

        return payloadMap;
    }

    private Double getPercentageDifference(Double totalBasePrice, Double size, Double newPrice) {
        Double totalNewPrice = newPrice * size;
        Double priceDiff = totalNewPrice - totalBasePrice;

        Double percentageDiff = (priceDiff * 100) / totalBasePrice;
        return percentageDiff;
    }

    private String getPercentageChangeString(Double percentageDifference) {
        if (percentageDifference < 0) {
            return "decreased";
        }
        return "increased";
    }
}
