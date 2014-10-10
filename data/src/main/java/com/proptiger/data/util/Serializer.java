package com.proptiger.data.util;

import java.lang.reflect.Modifier;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Serializer {
    private static Serializer  serializer;
    private static Gson        gson;
    private static GsonBuilder gsonBuilder;

    static {
        getInstance();
    }

    private Serializer() {
        ExclusionStrategy jsonExclusionStrategy = new JsonExclusionStrategy();
        gsonBuilder = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC)
                .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                .addSerializationExclusionStrategy(jsonExclusionStrategy)
                .addDeserializationExclusionStrategy(jsonExclusionStrategy)
                .serializeNulls();

        gson = gsonBuilder.create();
    }

    public static Serializer getInstance() {
        if (serializer == null) {
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
