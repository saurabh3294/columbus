/**
 * 
 */
package com.proptiger.data.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.proptiger.exception.ProAPIException;

/**
 * @author mandeep
 * @author azi
 * 
 */
public class StringToDateConverter implements Converter<String, Date> {

    @Override
    public Date convert(String source) {
        try {
            return new ISO8601DateFormat().parse(source);
        }
        catch (ParseException e) {
            throw new IllegalArgumentException("Could not parse date", e);
        }
    }

    /**
     * @param date
     *            {@link String} in YYYY-mm-dd format
     * @return {@link Date}
     */
    public static Date parseYYYYmmddDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return format.parse(date);
        }
        catch (ParseException e) {
            throw new ProAPIException("Unable to parse date", e);
        }
    }
}
