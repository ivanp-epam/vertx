package com.programm.vertx.handler.middleware;

import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.vertx.mutiny.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
public class LoggingMetadataAdd implements Consumer<RoutingContext> {

    private final String PROPERTY_START_TIME = "startTime";
    private final String PROPERTY_REQUEST_ID = "requestId";

    private final String METADATA_EXECUTION_TIME_MS = "execution_time_ms";
    private final String METADATA_REQUEST_ID = "request_id";
    private final String METADATA_CLIENT_IP = "client_ip";

    private void generateStartTimeAndRequestId(RoutingContext event) {
        event.put(PROPERTY_START_TIME, Instant.now());
        event.put(PROPERTY_REQUEST_ID, UUID.randomUUID().toString());
    }

    private void fillAddedMetadata(RoutingContext event) {
        Instant start = event.get(PROPERTY_START_TIME);
        Instant end = Instant.now();
        long delta = Duration.between(start, end).toMillis();

        MDC.put(METADATA_EXECUTION_TIME_MS, String.valueOf(delta));
        MDC.put(METADATA_REQUEST_ID, event.get(PROPERTY_REQUEST_ID));
        MDC.put(METADATA_CLIENT_IP, event.request().remoteAddress().toString());
    }

    private void cleanupMetadata() {
        MDC.clear();
    }

    public void handle(RoutingContext event) {
        generateStartTimeAndRequestId(event);

        event.response().bodyEndHandler(() -> {
            fillAddedMetadata(event);
            log.info("Request Finished!");
            cleanupMetadata();
        });

        Infrastructure.setDroppedExceptionHandler(err -> {
            fillAddedMetadata(event);
            log.error("Mutiny dropped exception", err);
            cleanupMetadata();
        });

        event.next();
    }

    @Override
    public void accept(RoutingContext routingContext) {
        handle(routingContext);
    }
}
