package com.proptiger.data.init;

import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

public class GroupConcatDistinctFunction implements SQLFunction {
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
        if (arg1.size() != 1) {
            throw new QueryException(new IllegalArgumentException("group_concat_distinct should have one arg"));
        }
        return "group_concat(distinct " + arg1.get(0) + ")";
    }
}