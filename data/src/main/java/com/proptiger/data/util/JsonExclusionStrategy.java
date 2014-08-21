package com.proptiger.data.util;

import org.apache.log4j.Logger;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.notification.model.MediumTypeConfig;
import com.proptiger.data.notification.model.NotificationTypeConfig;

public class JsonExclusionStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        if(f.getName().equalsIgnoreCase("serialVersionUID") || f.getName().equalsIgnoreCase("FATAL")){
            return true;
        }
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        if(clazz.equals(Logger.class) || clazz.equals(NotificationTypeConfig.class) || clazz.equals(MediumTypeConfig.class) || clazz.equals(ForumUser.class))
            return true;
        // TODO Auto-generated method stub
        return false;
    }

}
