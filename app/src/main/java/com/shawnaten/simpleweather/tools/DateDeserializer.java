package com.shawnaten.simpleweather.tools;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

import javax.inject.Inject;

public class DateDeserializer implements JsonDeserializer<Date> {

    @Inject
    public DateDeserializer() {

    }

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return new Date(json.getAsJsonPrimitive().getAsLong() * 1000);
    }
}
