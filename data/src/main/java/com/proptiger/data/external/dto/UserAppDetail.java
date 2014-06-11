package com.proptiger.data.external.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.proptiger.data.enums.SubscriptionSection;
import com.proptiger.data.model.City;
import com.proptiger.data.model.Company;

/**
 * 
 * @author azi
 * 
 */
public class UserAppDetail {
    private List<UserAppSubscription> subscriptions = new ArrayList<>();
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

    public static class UserAppSubscription {
        private Company                  company;
        private Set<SubscriptionSection> sections      = new HashSet<>();
        private Set<City>                cities        = new HashSet<>();
        private int                      cityCount     = 0;
        private int                      localityCount = 0;
        private int                      projectCount  = 0;
        private Date                     expiryTime;
        private String                   userType;

        public Set<SubscriptionSection> getSections() {
            return sections;
        }

        public void setSections(Set<SubscriptionSection> sections) {
            this.sections = sections;
        }

        public Set<City> getCities() {
            return cities;
        }

        public void setCities(Set<City> cities) {
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

        public Date getExpiryTime() {
            return expiryTime;
        }

        public void setExpiryTime(Date expiryTime) {
            this.expiryTime = expiryTime;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public Company getCompany() {
            return company;
        }

        public void setCompany(Company company) {
            this.company = company;
        }
    }
}