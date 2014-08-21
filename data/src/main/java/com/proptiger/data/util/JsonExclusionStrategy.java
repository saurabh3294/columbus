package com.proptiger.data.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class JsonExclusionStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        if(f.getName() == "serialVersionUID"){
            return true;
        }
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        // TODO Auto-generated method stub
        return false;
    }

}
