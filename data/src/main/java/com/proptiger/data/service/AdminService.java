package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.core.model.proptiger.CompanySubscription;
import com.proptiger.core.model.proptiger.Permission;
import com.proptiger.core.model.proptiger.SubscriptionPermission;
import com.proptiger.core.model.proptiger.UserSubscriptionMapping;
import com.proptiger.data.service.user.UserSubscriptionService;

/**
 * @author Rajeev Pandey
 *
 */
@Service
public class AdminService {

    @Autowired
    private UserSubscriptionService userSubscriptionService;
    
    @Transactional
    public List<Permission> getUserPermissions(int userId){
        List<Permission> userPermissions = new ArrayList<Permission>();
        List<UserSubscriptionMapping> userSubscriptions = userSubscriptionService
                .getUserSubscriptionMappingList(userId);
        Calendar cal = Calendar.getInstance();
        for(UserSubscriptionMapping mapping: userSubscriptions){
            CompanySubscription companySubscription = mapping.getSubscription();
            if(companySubscription != null && companySubscription.getExpiryTime().after(cal.getTime())){
                if(companySubscription.getPermissions() != null){
                    for(SubscriptionPermission subscriptionPermission: companySubscription.getPermissions()){
                        userPermissions.add(subscriptionPermission.getPermission());
                    }
                }
            }
        }
        return  userPermissions;
    }
}
