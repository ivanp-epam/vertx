package com.programm.vertx.handler;

import com.programm.vertx.helper.JsonHelper;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonHandlerTest {

    @Test
    void jsonIsNotValidOnMalformedData() {
        Arrays.stream("""
                            {
                            { " }
                            ,
                            [
                            { a:"b" }
                        """.split("\\n"))
                .map(String::trim)
                .forEach(element -> assertFalse(JsonHelper.isJSONValid(element)));
    }

    @Test
    void jsonIsValidOnCorrectJson() {
        Arrays.stream(
                        """
                                    null
                                    ""
                                    {"a":"b"}
                                    []
                                """.split("\\n"))
                .map(String::trim)
                .forEach(element -> assertTrue(JsonHelper.isJSONValid(element)));
    }

    @Test
    void jsonIsValidOnNull() {
        assertTrue(JsonHelper.isJSONValid(null));
    }
}