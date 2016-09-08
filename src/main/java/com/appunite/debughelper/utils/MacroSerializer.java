package com.appunite.debughelper.utils;

import com.appunite.debughelper.macro.GenericSavedField;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class MacroSerializer implements JsonDeserializer<GenericSavedField>, JsonSerializer<GenericSavedField> {

    @Override
    public GenericSavedField deserialize(final JsonElement json,
                                         final Type typeOfT,
                                         final JsonDeserializationContext context) throws JsonParseException {
        try {
            final String type = json.getAsJsonObject().get("type").getAsString();
            final Class<?> clazz = Class.forName(type);
            return context.deserialize(json, clazz);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }

    @Override
    public JsonElement serialize(final GenericSavedField src,
                                 final Type typeOfSrc,
                                 final JsonSerializationContext context) {
        final JsonObject serialized = (JsonObject) context.serialize(src);
        serialized.addProperty("type", src.getClass().getName());
        return serialized;
    }
}