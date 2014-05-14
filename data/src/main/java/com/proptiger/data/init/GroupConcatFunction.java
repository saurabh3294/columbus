package com.proptiger.data.init;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
public class GroupConcatFunction implements SQLFunction {
    private static final List<String> ASC_DESC_MARKER = Arrays.asList("1", "-1");

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
        int arg1Size = arg1.size();

        if (arg1Size == 0) {
            throw new QueryException(new IllegalArgumentException("group_concat should have one arg"));
        }
        else {
            String queryString = "group_concat(" + arg1.get(0);
            List<String> orderByList = new ArrayList<>();
            for (int i = 1; i < arg1Size; i++) {
                String arg = arg1.get(i).toString().trim();

                if (ASC_DESC_MARKER.contains(arg)) {
                    continue;
                }

                String orderString = arg;
                if (i + 1 < arg1Size && arg1.get(i + 1).toString().trim().equals("-1")) {
                    orderString = orderString + " desc";
                }
                else {
                    orderString += " asc";
                }
                orderByList.add(orderString);
            }

            if (arg1Size > 1) {
                queryString += " order by ";
                queryString += StringUtils.join(orderByList, ", ");
            }
            queryString += ")";
            return queryString;
        }
    }
}