package com.proptiger.data.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.proptiger.exception.ProAPIException;

/**
 * Utility functions using calander
 * 
 * @author Azitabh Ajit
 * 
 */

public class DateUtil {
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
}
