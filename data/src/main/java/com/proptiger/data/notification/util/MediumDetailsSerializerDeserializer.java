package com.proptiger.data.notification.util;

import java.io.IOException;
import java.lang.reflect.Type;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.proptiger.data.internal.dto.mail.MediumDetails;
import com.proptiger.data.notification.enums.MediumType;
import com.proptiger.data.notification.model.MediumTypeConfig;
import com.proptiger.data.util.Serializer;

public class MediumDetailsSerializerDeserializer extends JsonDeserializer<MediumDetails> implements
        com.google.gson.JsonDeserializer<MediumDetails>, com.google.gson.JsonSerializer<MediumDetails> {

    private static final String MEDIUM_TYPE = "mediumType";

    @Override
    public MediumDetails deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
            JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        String mediumTypeString = node.get(MEDIUM_TYPE).asText();
        MediumDetails mediumDetails = Serializer.fromJson(
                node.toString(),
                getMediumDetailsClassByMediumType(mediumTypeString));
        return mediumDetails;
    }

    @Override
    public MediumDetails deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject member = (JsonObject) json;
        String mediumTypeString = member.get(MEDIUM_TYPE).getAsString();
        return context.deserialize(json, getMediumDetailsClassByMediumType(mediumTypeString));
    }

    private Class<? extends MediumDetails> getMediumDetailsClassByMediumType(String mediumTypeString) {
        MediumType mediumType = MediumType.valueOf(mediumTypeString);
        MediumTypeConfig config = MediumTypeConfig.mediumTypeConfigMap.get(mediumType);
        return config.getMediumDetailsClassName();
    }

    @Override
    public JsonElement serialize(MediumDetails src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src);
    }
}
