/**
 * 
 */
package com.proptiger.data.util;

import java.util.Date;

import org.springframework.core.convert.converter.Converter;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

/**
 * @author mandeep
 *
 */
public class DateToStringConverter implements Converter<Date, String> {

    @Override
    public String convert(Date source) {
        return new ISO8601DateFormat().format(source);
    }
}
