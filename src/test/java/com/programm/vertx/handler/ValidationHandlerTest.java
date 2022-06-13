package com.programm.vertx.handler;

import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.core.Validator;
import com.programm.vertx.handler.stub.StubClass;
import com.programm.vertx.handler.stub.StubClass2;
import com.programm.vertx.http.StatusCodes;
import com.programm.vertx.response.error.ErrorDetail;
import com.programm.vertx.response.error.ErrorObject;
import com.programm.vertx.response.error.ErrorResponse;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.RoutingContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidationHandlerTest {
//    @Mock
//    RoutingContext event;
//    @Mock
//    RequestBody requestBody;
//    @Mock
//    HttpServerResponse response;
//
//    ValidationHandler<StubClass> handler;
//    Validator<StubClass> validator = ValidatorBuilder.<StubClass>of()
//            .constraint(StubClass::getFieldA, "fieldA", c -> c.notNull().notBlank())
//            .constraint(StubClass::getFieldB, "fieldB", c -> c.notBlank().notNull())
//            .build();
//
//    @BeforeEach
//    protected void setUp() {
//        handler = new ValidationHandler<>(validator, StubClass.class);
//    }
//
//    @Test
//    void handlerShouldCallNextHandlerIfValidationPassed() {
//        // given
//        StubClass stubClass = new StubClass("a", "b");
//        when(event.body()).thenReturn(requestBody);
//        when(requestBody.asJsonObject()).thenReturn(JsonObject.mapFrom(stubClass));
//
//        // when
//        handler.handle(event);
//
//        // then
//        verify(event, Mockito.atLeastOnce()).next();
//    }
//
//    @Test
//    void dtoWithoutEmptyConstructorCouldThrowError() {
//        // given
//        Validator<StubClass2> validator = ValidatorBuilder.<StubClass2>of()
//                .build();
//        ValidationHandler<StubClass2> handler1 = new ValidationHandler<>(validator, StubClass2.class);
//        StubClass2 stubClass = new StubClass2("a");
//
//        when(event.body()).thenReturn(requestBody);
//        when(requestBody.asJsonObject()).thenReturn(JsonObject.mapFrom(stubClass));
//
//        // when + then
//        Assertions.assertThrows(RuntimeException.class, () -> handler1.handle(event));
//    }
//
//    @Test
//    void dtoWithEmptyConstructorCouldNotThrowError() {
//        // given
//        StubClass stubClass = new StubClass();
//
//        when(event.body()).thenReturn(requestBody);
//        when(requestBody.asJsonObject()).thenReturn(JsonObject.mapFrom(stubClass));
//        when(event.response()).thenReturn(response);
//
//        // when + then
//        Assertions.assertDoesNotThrow(() -> handler.handle(event));
//    }
//
//    @Test
//    void handlerShouldReturnJsonIfValidationFailed() {
//        // given
//        StubClass stubClass = new StubClass();
//
//        when(event.body()).thenReturn(requestBody);
//        when(requestBody.asJsonObject()).thenReturn(JsonObject.mapFrom(stubClass));
//        when(event.response()).thenReturn(response);
//
//        // when
//        handler.handle(event);
//
//        // then
//        Map<String, List<ErrorDetail>> details = new HashMap<>();
//        details.put("fieldA",
//                List.of(
//                        new ErrorDetail("object.notNull", "\"fieldA\" must not be null"),
//                        new ErrorDetail("charSequence.notBlank", "\"fieldA\" must not be blank")
//                )
//        );
//        details.put("fieldB",
//                List.of(
//                        new ErrorDetail("charSequence.notBlank", "\"fieldB\" must not be blank"),
//                        new ErrorDetail("object.notNull", "\"fieldB\" must not be null")
//                )
//        );
//
//        Buffer buffer = Json.encodeToBuffer(new ErrorResponse(ErrorObject.VALIDATION_ERROR(details)));
//
//        verify(response, atLeastOnce()).setStatusCode(StatusCodes.BAD_REQUEST);
//        verify(response, atLeastOnce()).end(buffer);
//    }
}