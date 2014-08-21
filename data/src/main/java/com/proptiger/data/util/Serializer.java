package com.proptiger.data.util;

import java.lang.reflect.Modifier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Serializer {
    private static Serializer serializer;
    private static Gson gson;
    private static GsonBuilder gsonBuilder;
    
    static {
        getInstance();
    }
    private Serializer() {
        gsonBuilder = new GsonBuilder();
        gsonBuilder.excludeFieldsWithModifiers(Modifier.STATIC);
        gsonBuilder.excludeFieldsWithModifiers(Modifier.TRANSIENT);
        JsonExclusionStrategy jsonExclusionStrategy = new JsonExclusionStrategy();
        gsonBuilder.addSerializationExclusionStrategy(jsonExclusionStrategy);
        gsonBuilder.addDeserializationExclusionStrategy(jsonExclusionStrategy);
                
        gson = gsonBuilder.create();
        
    }

    public static Serializer getInstance() {
        if(serializer == null){
            serializer = new Serializer();
        }
        return serializer;
    }

    public static String toJson(Object data) {
        return gson.toJson(data);
    }

    public static <T> T fromJson(String json, Class<T> T) {
        return gson.fromJson(json, T);
    }
    
    
}
