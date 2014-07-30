package com.proptiger.data.external.dto;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;

import com.proptiger.data.enums.Application;
import com.proptiger.data.enums.SubscriptionSection;
import com.proptiger.data.model.Company;
import com.proptiger.data.model.UserPreference;
import com.proptiger.data.model.user.Dashboard;

/**
 * 
 * @author azi
 * 
 */

public class CustomUser {
    private int                             id;
    private String                          email;
    private String                          firstName;
    private String                          lastName;
    private String                          contactNumber;
    private String                          profileImageUrl;
    private Set<Integer>                    companyIds = new HashSet<>();
    private List<Dashboard>                 dashboards = new ArrayList<>();
    private Map<Application, UserAppDetail> appDetails = new HashMap<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Set<Integer> getCompanyIds() {
        return companyIds;
    }

    public void setCompanyIds(Set<Integer> companyIds) {
        this.companyIds = companyIds;
    }

    public List<Dashboard> getDashboards() {
        return dashboards;
    }

    public void setDashboards(List<Dashboard> dashboards) {
        this.dashboards = dashboards;
    }

    public Map<Application, UserAppDetail> getAppDetails() {
        return appDetails;
    }

    public void setAppDetails(Map<Application, UserAppDetail> appDetails) {
        this.appDetails = appDetails;
    }

    public static class UserAppDetail {
        private List<UserAppSubscription> subscriptions = new ArrayList<>();
        private UserPreference            preference;

        public List<UserAppSubscription> getSubscriptions() {
            return subscriptions;
        }

        public void setSubscriptions(List<UserAppSubscription> subscriptions) {
            this.subscriptions = subscriptions;
        }

        public UserPreference getPreference() {
            return preference;
        }

        public void setPreference(UserPreference preference) {
            this.preference = preference;
        }

        public static class UserAppSubscription {
            private Company                  company;
            private Set<SubscriptionSection> sections      = new HashSet<>();
            private Set<CustomCity>          cities        = new HashSet<>();
            private int                      cityCount     = 0;
            private int                      localityCount = 0;
            private int                      projectCount  = 0;
            private Date                     dataUpdationDate;
            private Date                     expiryDate;
            private String                   userType;

            public Set<SubscriptionSection> getSections() {
                return sections;
            }

            public void setSections(Set<SubscriptionSection> sections) {
                this.sections = sections;
            }

            public Set<CustomCity> getCities() {
                return cities;
            }

            public void setCities(Set<CustomCity> cities) {
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

            public Date getDataUpdationDate() {
                return dataUpdationDate;
            }

            public void setDataUpdationDate(Date dataUpdationDate) {
                this.dataUpdationDate = dataUpdationDate;
            }

            public Date getExpiryDate() {
                return expiryDate;
            }

            public void setExpiryDate(Date expiryDate) {
                this.expiryDate = DateUtils.ceiling(expiryDate, Calendar.DATE);
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

        public static class CustomCity {
            private int                  id;

            private String               name;

            private List<CustomLocality> localities = new ArrayList<>();

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<CustomLocality> getLocalities() {
                return localities;
            }

            public void setLocalities(List<CustomLocality> localities) {
                this.localities = localities;
            }
        }

        public static class CustomLocality {
            private int    id;

            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}