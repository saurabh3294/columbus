/**
 * 
 */
package com.proptiger.data.notification.model;

import java.util.Arrays;
import java.util.List;


/**
 * @author mandeep
 *
 */
@Deprecated
public enum NotificationDeprecatedType {
    GOAL_PRICE_MET(new NotificationMedium[]{NotificationMedium.EMAIL, NotificationMedium.MOBILE_PUSH}, DispatchPolicy.MONTHLY),
    PRICE_APPRECIATED(new NotificationMedium[]{NotificationMedium.EMAIL, NotificationMedium.WEBSITE}, DispatchPolicy.MONTHLY),
    CONSTRUCTION_IMAGES_UPLOADED(new NotificationMedium[]{NotificationMedium.EMAIL, NotificationMedium.MOBILE_PUSH}, DispatchPolicy.MONTHLY),
    NEWS_ADDED(new NotificationMedium[]{NotificationMedium.EMAIL}, DispatchPolicy.REAL_TIME),
    DEMAND_RAISED(new NotificationMedium[]{NotificationMedium.EMAIL}, DispatchPolicy.DAILY);

    private List<NotificationMedium> defaultNotificationMedia;
    private DispatchPolicy defaultDispatchPolicy;

    private NotificationDeprecatedType(NotificationMedium[] notificationMedia, DispatchPolicy dispatchPolicy) {
        defaultNotificationMedia = Arrays.asList(notificationMedia);
        defaultDispatchPolicy = dispatchPolicy;
    }
}
