package com.programm.vertx.helper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IntegerHelperTest {

    @Test
    void tryParseIntReturnsIntegerFromString() {
        String number = "12345";
        int i = IntegerHelper.tryParseInt(number, -1);
        assertEquals(12345, i);
    }

    @Test
    void tryParseIntReturnsDefaultIntegerIfCanNotConvertStringToInteger() {
        String number = "12345a";
        int i = IntegerHelper.tryParseInt(number, -1);
        assertEquals(-1, i);
    }
}