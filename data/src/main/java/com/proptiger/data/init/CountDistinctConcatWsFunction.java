package com.proptiger.data.init;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

import com.proptiger.exception.ProAPIException;

/**
 * Implements mysql count(distinct concat_ws(ARGS)) function
 * 
 * Takes list of expression as input. First expression should be a string
 * literal to be used as a separator. List should have a minimum of 2
 * expressions.
 * 
 * @author azi
 * 
 */

public class CountDistinctConcatWsFunction implements SQLFunction {
    @Override
    public boolean hasArguments() {
        return true;
    }

    @Override
    public boolean hasParenthesesIfNoArguments() {
        return true;
    }

    @Override
    public Type getReturnType(Type arg0, Mapping arg1) throws QueryException {
        return StandardBasicTypes.INTEGER;
    }

    @Override
    public String render(Type arg0, List arg1, SessionFactoryImplementor arg2) throws QueryException {
        if (arg1.size() < 2) {
            throw new ProAPIException("CountDistinctConcatWsFunction should have 2 arguments at min");
        }
        return "count(distinct concat_ws(" + StringUtils.join(arg1, ",") + "))";
    }
}
