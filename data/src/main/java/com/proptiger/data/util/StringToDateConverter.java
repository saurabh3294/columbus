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
}