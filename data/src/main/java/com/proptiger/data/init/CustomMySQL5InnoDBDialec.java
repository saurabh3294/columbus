package com.proptiger.data.init;

import org.hibernate.dialect.MySQL5InnoDBDialect;

/**
 * 
 * @author azi
 * 
 */
public class CustomMySQL5InnoDBDialec extends MySQL5InnoDBDialect {
    public CustomMySQL5InnoDBDialec() {
        super();
        registerFunction("group_concat", new GroupConcatFunction());
        registerFunction("group_concat_distinct", new GroupConcatDistinctFunction());
        registerFunction("median", new MedianFunction());
        registerFunction("weighted_average", new WeightedAverageFunction());
        registerFunction("substring_index", new SubstringIndexFunction());
        registerFunction("concat_ws", new ConcatWsFunction());
        registerFunction("count_distinct_concat_ws", new CountDistinctConcatWsFunction());
    }
}
