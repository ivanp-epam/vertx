package com.programm.vertx.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.io.IOException;

public class JsonHelper<T> {

    public static boolean isJSONValid(String jsonInString) {
        // mark null value as valid JSON
        if (jsonInString == null) {
            return true;
        }

        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(jsonInString);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static <T> T fromJsonObject(String json, Class<T> clazz) {
        try {
            return Json.CODEC.fromString(json, clazz);
        } catch (RuntimeException e) {
            return new JsonObject().mapTo(clazz);
        }
    }
}
