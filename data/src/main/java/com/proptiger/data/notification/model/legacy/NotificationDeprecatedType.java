/**
 * 
 */
package com.proptiger.data.notification.model.legacy;

import java.util.Arrays;
import java.util.List;


/**
 * @author mandeep
 *
 */
@Deprecated
public enum NotificationDeprecatedType {
    GOAL_PRICE_MET(new NotificationMediumOld[]{NotificationMediumOld.EMAIL, NotificationMediumOld.MOBILE_PUSH}, DispatchPolicyOld.MONTHLY),
    PRICE_APPRECIATED(new NotificationMediumOld[]{NotificationMediumOld.EMAIL, NotificationMediumOld.WEBSITE}, DispatchPolicyOld.MONTHLY),
    CONSTRUCTION_IMAGES_UPLOADED(new NotificationMediumOld[]{NotificationMediumOld.EMAIL, NotificationMediumOld.MOBILE_PUSH}, DispatchPolicyOld.MONTHLY),
    NEWS_ADDED(new NotificationMediumOld[]{NotificationMediumOld.EMAIL}, DispatchPolicyOld.REAL_TIME),
    DEMAND_RAISED(new NotificationMediumOld[]{NotificationMediumOld.EMAIL}, DispatchPolicyOld.DAILY);

    private List<NotificationMediumOld> defaultNotificationMedia;
    private DispatchPolicyOld defaultDispatchPolicy;

    private NotificationDeprecatedType(NotificationMediumOld[] notificationMedia, DispatchPolicyOld dispatchPolicy) {
        defaultNotificationMedia = Arrays.asList(notificationMedia);
        defaultDispatchPolicy = dispatchPolicy;
    }
}
