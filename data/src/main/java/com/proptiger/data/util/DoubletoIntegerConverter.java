package com.proptiger.data.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;

public class DoubletoIntegerConverter implements Converter<Double, Integer> {

    @Override
    public Integer convert(Double value) {
        if (value != null) {
            return value.intValue();
        }

        return null;
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructType(Double.TYPE);
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructType(Integer.TYPE);
    }

}
