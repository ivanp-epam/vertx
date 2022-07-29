package com.programm.vertx.handler.middleware;

import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.core.Validator;
import com.programm.vertx.exceptions.ValidationException;
import com.programm.vertx.handler.middleware.stub.StubClass;
import com.programm.vertx.handler.middleware.stub.StubClass2;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.http.HttpServerResponse;
import io.vertx.mutiny.ext.web.RequestBody;
import io.vertx.mutiny.ext.web.RoutingContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidationHandlerTest {
    @Mock
    RoutingContext event;

    @Mock
    RequestBody requestBody;

    @Mock
    HttpServerResponse response;

    ValidationHandler<StubClass> handler;
    Validator<StubClass> validator = ValidatorBuilder.<StubClass>of()
            .constraint(StubClass::getFieldA, "fieldA", c -> c.notNull().notBlank())
            .constraint(StubClass::getFieldB, "fieldB", c -> c.notBlank().notNull())
            .build();

    @BeforeEach
    protected void setUp() {
        handler = new ValidationHandler<>(validator, StubClass.class);
    }

    @Test
    void handlerShouldCallNextHandlerIfValidationPassed() {
        // given
        StubClass stubClass = new StubClass("a", "b");
        when(event.body()).thenReturn(requestBody);
        when(requestBody.asString()).thenReturn(JsonObject.mapFrom(stubClass).toString());

        // when
        handler.accept(event);

        // then
        verify(event, Mockito.atLeastOnce()).next();
    }

    @Test
    void dtoWithoutEmptyConstructorThrowsError() {
        // given
        Validator<StubClass2> validator = ValidatorBuilder.<StubClass2>of()
                .build();
        ValidationHandler<StubClass2> handler1 = new ValidationHandler<>(validator, StubClass2.class);
        StubClass2 stubClass = new StubClass2("a");

        when(event.body()).thenReturn(requestBody);
        when(requestBody.asString()).thenReturn(JsonObject.mapFrom(stubClass).toString());

        // when + then
        Assertions.assertThrows(RuntimeException.class, () -> handler1.accept(event));
    }

    @Test
    void handlerShouldThrowValidationExceptionIfValidationFailed() {
        // given
        StubClass stubClass = new StubClass();

        when(event.body()).thenReturn(requestBody);
        when(requestBody.asString()).thenReturn(JsonObject.mapFrom(stubClass).toString());

        // when + then
        Assertions.assertThrows(ValidationException.class, () -> {
            handler.accept(event);
        });
    }
}