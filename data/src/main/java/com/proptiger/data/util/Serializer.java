package com.proptiger.data.util;

import com.google.gson.Gson;

public class Serializer {
    private static Gson serializer = new Gson();

    private Serializer() {

    }

    public static Gson getInstance() {
        return serializer;
    }

    public static String toJson(Object data) {
        return serializer.toJson(data);
    }

    public static <T> T fromJson(String json, Class<T> T) {
        return serializer.fromJson(json, T);
    }
}
