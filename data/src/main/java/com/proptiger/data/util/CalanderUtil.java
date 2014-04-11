package com.proptiger.data.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Utility functions using calander
 * 
 * @author Azitabh Ajit
 * 
 */

public class CalanderUtil {
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
        Date date = StringToDateConverter.parseYYYYmmddStringToDate(stringDate);
        return new SimpleDateFormat("yyyy-MM-dd").format(shiftMonths(date, shift)).toString();
    }
}
