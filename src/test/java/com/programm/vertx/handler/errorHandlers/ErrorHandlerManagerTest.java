package com.programm.vertx.handler.errorHandlers;

import io.vertx.mutiny.ext.web.RoutingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ErrorHandlerManagerTest {

    @Mock
    RoutingContext event;

    @Mock
    ErrorHandlerSpecification handlerSpecific;

    @Mock
    ErrorHandlerSpecification defaultHandler;

    private ErrorHandlerManager handler;

    @BeforeEach
    protected void setUp() {
        handler = new ErrorHandlerManager(List.of(handlerSpecific), defaultHandler);
    }

    @Test
    void testSpecificHandlerAppliesIfSatisfy() {
        // given
        when(handlerSpecific.isSatisfy(event)).thenReturn(true);

        // when
        handler.accept(event);

        // then
        verify(handlerSpecific, atLeastOnce()).accept(event);
        verify(defaultHandler, times(0)).accept(event);
    }

    @Test
    void testDefaultHandlerAppliesIfNoneSatisfies() {
        // given
        when(handlerSpecific.isSatisfy(event)).thenReturn(false);

        // when
        handler.accept(event);

        // then
        verify(defaultHandler, atLeastOnce()).accept(event);
        verify(handlerSpecific, times(0)).accept(event);
    }

}
