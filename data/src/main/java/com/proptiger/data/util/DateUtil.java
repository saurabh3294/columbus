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

public class DateUtil 
{
	public static int MonthCountInQuarter = 3;
	public static int MonthStartDate = 1;

	
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
     * @param stringDate {@link String} date in YYYY-dd-mm format
     * @param shift {@link Integer} no of months to go back.
     * @return {@link String} subtracted date in YYYY-dd-mm format
     */
    /* Wrapper over "shift months" to enforce subtraction only */
    public static String subtractMonths(String stringDate, Integer shift) 
    {
        if(shift <= 0 )
        {	return stringDate;	}
        else
        {	return shiftMonths(stringDate, -1 * shift);	}
    }
    
	/**
	 *  @return {@link String}  start month of this Quarter in YYYY-mm-dd format.
	 *  						or null if something goes wrong.
	 *  @param date {@link String} in YYYY-mm--dd format
     *            
	 * */
    public static String getQuarterStartDateString(String stringDate)
    {
    	if(stringDate == null || stringDate.isEmpty())
    	{	return null;	}
    	
    	try
    	{
     		Date date = parseYYYYmmddStringToDate(stringDate);
       		Calendar calendar = Calendar.getInstance();
       		calendar.setTime(date);
       		int monthNumber = calendar.get(Calendar.MONTH);
	    	int quarterStartMonthNumber = ((monthNumber/MonthCountInQuarter) * (MonthCountInQuarter));
	   		calendar.set(Calendar.MONTH, quarterStartMonthNumber);
	   		calendar.set(Calendar.DATE, MonthStartDate);
	   		
	    	/* Formatting back to Date-String */
	    	return (new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
    	}
    	/* TODO :: what are the conventions? (Null Return VS ApiExeption throw) */
    	catch(Exception e)
    	{	return null;	}
    }

	/**
	 * @param stringDate {@link String} in YYYY-mm-dd format
	 * @return integer between 0-11 representing month number.
	 */
    public static int getMonthNumberFromDateString(String stringDate)
    {
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
}
