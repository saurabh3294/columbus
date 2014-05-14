package com.proptiger.data.init;

import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

/**
 * 
 * @author azi
 * 
 */
public class SubstringIndexFunction implements SQLFunction {
    @Override
    public boolean hasArguments() {
        return true;
    }

    @Override
    public boolean hasParenthesesIfNoArguments() {
        return true;
    }

    @Override
    public Type getReturnType(org.hibernate.type.Type arg0, Mapping arg1) throws QueryException {
        // TODO Auto-generated method stub
        return StandardBasicTypes.STRING;
    }

    @Override
    public String render(Type arg0, List arg1, SessionFactoryImplementor arg2) throws QueryException {
        if (arg1.size() != 3) {
            throw new QueryException(new IllegalArgumentException("group_concat should have one arg"));
        }
        return "substring_index(" + arg1.get(0) + "," + arg1.get(1) + "," + arg1.get(2) + ")";
    }
}
