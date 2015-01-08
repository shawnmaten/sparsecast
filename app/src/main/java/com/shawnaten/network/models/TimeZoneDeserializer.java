package com.shawnaten.network.models;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.TimeZone;

import javax.inject.Inject;

public class TimeZoneDeserializer implements JsonDeserializer<TimeZone> {

    @Inject
    public TimeZoneDeserializer() {

    }

    @Override
    public TimeZone deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return TimeZone.getTimeZone(json.getAsJsonPrimitive().getAsString());
    }
}
