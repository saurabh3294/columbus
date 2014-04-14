package com.proptiger.data.model.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Class to convert boolean entity field into Enum database field and vice versa
 * 
 * This class is not tested and is not being used anywhere. Spring does this
 * conversion by default
 * 
 * @author azi
 */
@Converter
public class BooleanConverter implements AttributeConverter<Boolean, String> {
    /**
     * 
     * @param b
     *            {@link Boolean}
     * @return {@link String}
     */
    @Override
    public String convertToDatabaseColumn(Boolean b) {
        if (b == null) {
            return null;
        }
        else if (b) {
            return "True";
        }
        else {
            return "False";
        }
    }

    /**
     * 
     * @param string
     *            {@link String}
     * @return {@link Boolean}
     */
    @Override
    public Boolean convertToEntityAttribute(String string) {
        if (string.toUpperCase().equals("TRUE")) {
            return true;
        }
        else if (string.toUpperCase().equals("FALSE")) {
            return false;
        }
        else {
            return null;
        }
    }
}
