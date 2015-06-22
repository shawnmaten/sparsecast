package com.shawnaten.tools;


import android.net.Uri;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import javax.inject.Inject;

public class UriDeserializer implements JsonDeserializer<Uri> {

    @Inject
    public UriDeserializer() {

    }

    @Override
    public Uri deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Uri.parse(json.getAsJsonPrimitive().getAsString());
    }
}
