package com.proptiger.data.notification.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.util.DateUtil;
import com.proptiger.data.notification.enums.MediumType;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.service.NotificationGeneratedService;

@Service
public class NotificationScheduler {

    private static Logger                logger      = LoggerFactory.getLogger(NotificationScheduler.class);

    @Autowired
    private NotificationGeneratedService nGeneratedService;

    // Map of userId and medium to priority of Notifications Scheduled
    Map<String, Integer>                 priorityMap = new HashMap<String, Integer>();

    public Integer scheduleNotifications() {
        Integer scheduledNtGeneratedCount = 0;
        List<NotificationGenerated> notificationGeneratedList = nGeneratedService.getRawNotificationGeneratedList();

        notificationGeneratedList = sortNotificationGeneratedByPriority(notificationGeneratedList);

        for (NotificationGenerated nGenerated : notificationGeneratedList) {

            Integer userId = nGenerated.getUserId();
            MediumType mediumType = nGenerated.getNotificationMedium().getName();
            Integer lastPriority = priorityMap.get(generatePriorityMapKey(userId, mediumType));
            Integer currentPriority = nGenerated.getNotificationType().getPriority();
            
            //lesser the priority numeric value higher the priority ie 1 corresponds to higher priority than 2.
            if (lastPriority != null && lastPriority < currentPriority) {
                logger.info("Suppressing notificationGeneratedId: " + nGenerated.getId()
                        + " because a higher priority notification was already scheduled/sent.");
                nGeneratedService.markNotificationGeneratedSuppressed(nGenerated);
                continue;
            }

            NotificationType notificationType = nGenerated.getNotificationType();

            Date lastScheduledNTDate = new Date();
            NotificationGenerated lastScheduledOrSentNtGenerated = nGeneratedService
                    .getLastScheduledOrSendNotificationGeneratedSameAs(nGenerated);
            if (lastScheduledOrSentNtGenerated != null) {
                lastScheduledNTDate = lastScheduledOrSentNtGenerated.getUpdatedAt();
            }

            Date lastScheduledInMediumDate = new Date();
            NotificationGenerated lastScheduledOrSentNtGeneratedInMedium = nGeneratedService
                    .getLastScheduledOrSentNotificationGeneratedInMediumSameAs(nGenerated);
            if (lastScheduledOrSentNtGeneratedInMedium != null) {
                lastScheduledInMediumDate = lastScheduledOrSentNtGeneratedInMedium.getUpdatedAt();
            }

            List<Date> dateList = new ArrayList<Date>();
            Date expiryDate = new Date();
            Integer holdingPeriodValue = notificationType.getHoldingPeriodValue();
            
            switch (notificationType.getHoldingPeriodType()) {
                case SECONDS:
                    expiryDate = DateUtils.addSeconds(lastScheduledNTDate, holdingPeriodValue);
                    break;
                case MINUTES:
                    expiryDate = DateUtils.addMinutes(lastScheduledNTDate, holdingPeriodValue);
                    break;
                case HOURS:
                    expiryDate = DateUtils.addHours(lastScheduledNTDate, holdingPeriodValue);
                    break;
                case DAYS:
                    expiryDate = DateUtils.addDays(lastScheduledNTDate, holdingPeriodValue);
                    break;
                case WEEKS:
                    expiryDate = DateUtils.addWeeks(lastScheduledNTDate, holdingPeriodValue);
                    break;
                case MONTHS:
                    expiryDate = DateUtils.addMonths(lastScheduledNTDate, holdingPeriodValue);
                    break;
                case UPCOMING_MONTHS:
                    expiryDate = DateUtils.addMonths(lastScheduledNTDate, holdingPeriodValue);
                    expiryDate = DateUtils.truncate(expiryDate, Calendar.MONTH);
                    break;
                case INFINITE:
                    expiryDate = DateUtils.addYears(lastScheduledNTDate, 50);
                    expiryDate = DateUtils.truncate(expiryDate, Calendar.YEAR);
                    break;
                default:
                    break;
            }            
            dateList.add(expiryDate);
            dateList.add(DateUtil.addSeconds(lastScheduledInMediumDate, nGenerated.getNotificationMedium()
                    .getFrequencyCycleInSeconds().intValue()));
            
            Date nextScheduleDate = DateUtil.getMaxDate(dateList);
            if (nextScheduleDate.after(new Date()) && !notificationType.getCanReschedule()) {
                logger.info("Suppressing notificationGeneratedId: " + nGenerated.getId()
                        + " because there are some notifications which were recently scheduled/sent"
                        + " and this can't be rescheduled to a later date.");
                nGeneratedService.markNotificationGeneratedSuppressed(nGenerated);
            }
            else {
                dateList.clear();
                dateList.add(nextScheduleDate);
                dateList.add(new Date());
                dateList.add(DateUtil.addSeconds(nGenerated.getCreatedAt(), notificationType.getFixedDelay().intValue()));
                Date newScheduleDate = DateUtil.getMaxDate(dateList);
                logger.info("Scheduling notificationGeneratedId: " + nGenerated.getId() + " at " + newScheduleDate);
                nGeneratedService.markNotificationGeneratedScheduled(nGenerated, newScheduleDate);
                priorityMap.put(generatePriorityMapKey(userId, mediumType), nGenerated.getNotificationType().getPriority());
                scheduledNtGeneratedCount++;
            }
        }
        return scheduledNtGeneratedCount;
    }

    private List<NotificationGenerated> sortNotificationGeneratedByPriority(List<NotificationGenerated> ngList) {
        Collections.sort(ngList, new Comparator<NotificationGenerated>() {
            @Override
            public int compare(NotificationGenerated ng1, NotificationGenerated ng2) {
                int priorityComp = ng1.getNotificationType().getPriority()
                        .compareTo(ng2.getNotificationType().getPriority());
                if (priorityComp != 0) {
                    return priorityComp;
                }
                return ng1.getCreatedAt().compareTo(ng2.getCreatedAt());
            }
        });
        return ngList;
    }

    private String generatePriorityMapKey(Integer userId, MediumType medium) {
        return userId + "." + medium.name();
    }

}