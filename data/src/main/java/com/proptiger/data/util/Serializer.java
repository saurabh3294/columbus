package com.proptiger.data.util;

import java.lang.reflect.Modifier;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.proptiger.core.event.model.payload.EventTypePayload;
import com.proptiger.data.internal.dto.mail.MediumDetails;
import com.proptiger.data.notification.util.MediumDetailsSerializerDeserializer;

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
                .registerTypeAdapter(MediumDetails.class, new MediumDetailsSerializerDeserializer())
                .registerTypeAdapter(EventTypePayload.class, new InterfaceAdapter<EventTypePayload>());

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
