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

    public void handle(RoutingContext event) {
        log.info("Request OLOLOL");
        event.put("startTime", Instant.now());
        event.put("requestId", UUID.randomUUID().toString());
        event.response().bodyEndHandler(() -> {
            Instant start = event.get("startTime");
            Instant end = Instant.now();
            long delta = Duration.between(start, end).toMillis();
            MDC.put("execution_time_ms", String.valueOf(delta));

            MDC.put("requestId", event.get("requestId"));
            MDC.put("client_ip", event.request().remoteAddress().toString());

            log.info("Request Finished!!!");
            MDC.clear();
        });

        Infrastructure.setDroppedExceptionHandler(err -> {
            Instant start = event.get("startTime");
            Instant end = Instant.now();
            long delta = Duration.between(start, end).toMillis();
            MDC.put("execution_time_ms", String.valueOf(delta));

            MDC.put("requestId", event.get("requestId"));
            MDC.put("client_ip", event.request().remoteAddress().toString());

            log.error("Mutiny dropped exception", err);
            MDC.clear();
        });

        event.next();
    }

    @Override
    public void accept(RoutingContext routingContext) {
        handle(routingContext);
    }
}
