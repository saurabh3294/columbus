package com.proptiger.data.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.proptiger.exception.ProAPIException;

/**
 * Utility functions using calander
 * 
 * @author Azitabh Ajit
 * 
 */

public class DateUtil {
    private static Logger   logger              = LoggerFactory.getLogger(DateUtil.class);

    public static int       MonthCountInQuarter = 3;
    public static int       MonthStartDate      = 1;

    public static final int secondsInADay       = 86400;

    public static Date addHours(Date baseDate, int hoursToAdd) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(baseDate);
        calendar.add(Calendar.HOUR, hoursToAdd);
        return calendar.getTime();
    }

    public static Date addMinutes(Date baseDate, int minutesToAdd) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(baseDate);
        calendar.add(Calendar.MINUTE, minutesToAdd);
        return calendar.getTime();
    }

    public static Date addSeconds(Date baseDate, int secondsToAdd) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(baseDate);
        calendar.add(Calendar.SECOND, secondsToAdd);
        return calendar.getTime();
    }

    public static Date getFirstDayOfCurrentMonth(Date currentDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 0);

        return cal.getTime();
    }

    public static Date getMaxDate(List<Date> dates) {
        if (dates == null || dates.isEmpty()) {
            return null;
        }
        Collections.sort(dates, new Comparator<Date>() {
            @Override
            public int compare(Date o1, Date o2) {
                return o2.compareTo(o1);
            }
        });
        return dates.get(0);
    }

    /**
     * @return {@link Date} date in YYYY-dd-mm format
     * 
     * @param date
     *            {@link Date} reference date
     * @param shift
     *            {@link Integer} no of months to go in forward direction..
     *            negative to go back
     * 
     * @author Azitabh Ajit
     * 
     */
    public static Date shiftMonths(Date date, Integer shift) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, shift);
        return calendar.getTime();
    }

    /**
     * @return {@link String} date in YYYY-dd-mm format
     * 
     * @param date
     *            {@link String} in YYYY-mm--dd format
     * @param shift
     *            {@link Integer} no of months to go in forward direction..
     *            negative to go back
     * 
     * @author Azitabh Ajit
     * 
     */
    public static String shiftMonths(String stringDate, Integer shift) {
        Date date = parseYYYYmmddStringToDate(stringDate);
        return new SimpleDateFormat("yyyy-MM-dd").format(shiftMonths(date, shift)).toString();
    }

    /**
     * @param stringDate
     *            {@link String} date in YYYY-dd-mm format
     * @param shift
     *            {@link Integer} no of months to go back.
     * @return {@link String} subtracted date in YYYY-dd-mm format
     */
    /* Wrapper over "shift months" to enforce subtraction only */
    public static String subtractMonths(String stringDate, Integer shift) {
        if (shift <= 0) {
            return stringDate;
        }
        else {
            return shiftMonths(stringDate, -1 * shift);
        }
    }

    /**
     * @return {@link String} start month of this Quarter in YYYY-mm-dd format.
     *         or null if something goes wrong.
     * @param date
     *            {@link String} in YYYY-mm--dd format
     * 
     * */
    public static String getQuarterStartDateString(String stringDate) {
        if (stringDate == null || stringDate.isEmpty()) {
            return null;
        }

        try {
            Date date = parseYYYYmmddStringToDate(stringDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int monthNumber = calendar.get(Calendar.MONTH);
            int quarterStartMonthNumber = ((monthNumber / MonthCountInQuarter) * (MonthCountInQuarter));
            calendar.set(Calendar.MONTH, quarterStartMonthNumber);
            calendar.set(Calendar.DATE, MonthStartDate);

            /* Formatting back to Date-String */
            return (new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
        }
        /* TODO :: what are the conventions? (Null Return VS ApiExeption throw) */
        catch (Exception e) {
            logger.error("Error while parsing date : " + stringDate, e);
            return null;
        }
    }

    /**
     * @param stringDate
     *            {@link String} in YYYY-mm-dd format
     * @return integer between 0-11 representing month number.
     */
    public static int getMonthNumberFromDateString(String stringDate) {
        Date date = parseYYYYmmddStringToDate(stringDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }

    /**
     * @param date
     *            {@link String} in YYYY-mm-dd format
     * @return {@link Date}
     */
    public static Date parseYYYYmmddStringToDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return format.parse(date);
        }
        catch (ParseException e) {
            throw new ProAPIException("Unable to parse date", e);
        }
    }

    public static Date addDays(Date baseDate, int daysToAdd) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(baseDate);
        calendar.add(Calendar.DAY_OF_YEAR, daysToAdd);
        return calendar.getTime();
    }

    /**
     * 
     * @param date
     * @param timeToAddInSecond
     * @return a new date with timeToAddInSecond seconds of working time added
     *         in input date
     */
    public static Date getWorkingTimeAddedIntoDate(Date date, int timeToAddInSecond) {
        int add = 1;
        if (timeToAddInSecond < 0) {
            add = -1;
            timeToAddInSecond = add * timeToAddInSecond;
        }

        DateTime finalTime = new DateTime(date.getTime());
        DateTime workingHourStartTime = finalTime.withTimeAtStartOfDay().plusSeconds(getWorkingTimeStartSeconds());
        if (!isWorkingTime(finalTime)) {
            if (workingHourStartTime.isAfter(finalTime)) {
                finalTime = workingHourStartTime;
            }
            else {
                finalTime = workingHourStartTime.plusDays(1);
            }
        }
        if (workingHourStartTime.isAfter(finalTime)) {
            finalTime = workingHourStartTime;
        }

        int workingSecondsInADay = getWorkingSecondsInADay();
        int days = timeToAddInSecond / getWorkingSecondsInADay();
        finalTime = finalTime.plusDays(add * days);

        int secsInIncompleteDay = timeToAddInSecond % workingSecondsInADay;
        finalTime = finalTime.plusSeconds(add * secsInIncompleteDay);

        if (!isWorkingTime(finalTime)) {
            finalTime = finalTime.plusSeconds(add * getNonWorkingSecondsInADay());
        }

        return new Date(finalTime.getMillis());
    }

    /**
     * 
     * @param date
     * @param timeToAddInSecond
     * @return a new date with timeToAddInSecond seconds of working time
     *         subtracted in input date
     */
    public static Date getWorkingTimeSubtractedFromDate(Date date, int timeToAddInSecond) {
        return getWorkingTimeAddedIntoDate(date, -1 * timeToAddInSecond);
    }

    /**
     * 
     * @return {@link Integer} no of working seconds in a day
     */
    private static int getWorkingSecondsInADay() {
        return getWorkingTimeEndSeconds() - getWorkingTimeStartSeconds();
    }

    /**
     * 
     * @return {@link Integer} no of non-working seconds in a day
     */
    private static int getNonWorkingSecondsInADay() {
        return secondsInADay - getWorkingSecondsInADay();
    }

    /**
     * gives min of two dates... null if both are null.. other date if one is
     * null
     * 
     * @param date1
     * @param date2
     * @return
     */
    public static Date min(Date date1, Date date2) {
        Date minDate;
        if (date1 == null) {
            minDate = date2;
        }
        else if (date2 == null) {
            minDate = date1;
        }
        else if (date1.after(date2)) {
            minDate = date2;
        }
        else {
            minDate = date1;
        }
        return minDate;
    }

    /**
     * gives max of two dates... null if both are null.. other date if one is
     * null
     * 
     * @param date1
     * @param date2
     * @return
     */
    public static Date max(Date date1, Date date2) {
        Date maxDate;
        if (date1 == null) {
            maxDate = date2;
        }
        else if (date2 == null) {
            maxDate = date1;
        }
        else if (min(date1, date2).equals(date1)) {
            maxDate = date2;
        }
        else {
            maxDate = date1;
        }
        return maxDate;
    }

    public static int getWorkingTimeStartSeconds() {
        return PropertyReader.getRequiredPropertyAsInt(PropertyKeys.CALENDAR_WORKING_HOUR_START);
    }

    public static int getWorkingTimeEndSeconds() {
        return PropertyReader.getRequiredPropertyAsInt(PropertyKeys.CALENDAR_WORKING_HOUR_END);
    }

    public static boolean isWorkingTime(Date date) {
        DateTime dateTime = new DateTime(date);
        return isWorkingTime(dateTime);
    }

    private static boolean isWorkingTime(DateTime date) {
        int seconds = date.getSecondOfDay();
        return seconds >= getWorkingTimeStartSeconds() && seconds <= getWorkingTimeEndSeconds();
    }
}