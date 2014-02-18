package com.proptiger.data.init;

import org.hibernate.dialect.MySQL5InnoDBDialect;

public class CustomMySQL5InnoDBDialec extends MySQL5InnoDBDialect {
    public CustomMySQL5InnoDBDialec() {
        super();
        registerFunction("group_concat", new GroupConcatFunction());
        registerFunction("median", new MedianFunction());
    }
}
