package com.proptiger.data.external.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.proptiger.data.enums.SubscriptionSection;
import com.proptiger.data.model.City;

/**
 * 
 * @author azi
 * 
 */
public class UserAppDetail {
    private List<UserAppSubscription> subscriptions;
    private JsonNode                  preferences;

    public List<UserAppSubscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<UserAppSubscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public JsonNode getPreferences() {
        return preferences;
    }

    public void setPreferences(JsonNode preferences) {
        this.preferences = preferences;
    }

    public class UserAppSubscription {
        private List<SubscriptionSection> sections;
        private List<City>                cities;
        private int                       cityCount;
        private int                       localityCount;
        private int                       projectCount;
        private Date                      expiryDate;
        private String                    userType;

        public List<SubscriptionSection> getSections() {
            return sections;
        }

        public void setSections(List<SubscriptionSection> sections) {
            this.sections = sections;
        }

        public List<City> getCities() {
            return cities;
        }

        public void setCities(List<City> cities) {
            this.cities = cities;
        }

        public int getCityCount() {
            return cityCount;
        }

        public void setCityCount(int cityCount) {
            this.cityCount = cityCount;
        }

        public int getLocalityCount() {
            return localityCount;
        }

        public void setLocalityCount(int localityCount) {
            this.localityCount = localityCount;
        }

        public int getProjectCount() {
            return projectCount;
        }

        public void setProjectCount(int projectCount) {
            this.projectCount = projectCount;
        }

        public Date getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(Date expiryDate) {
            this.expiryDate = expiryDate;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }
    }
}