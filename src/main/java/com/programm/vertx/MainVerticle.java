package com.programm.vertx;

import com.programm.vertx.routing.Routing;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.http.HttpServer;

public class MainVerticle extends AbstractVerticle {

    public Uni<Void> asyncStart() {
        ConfigRetriever retriever = ConfigRetriever.create(Vertx.vertx());
        JsonObject config = retriever.getConfig().result();

        // Create the HTTP server
        Uni<HttpServer> startHttpServer = vertx.createHttpServer()
                // Handle every request using the router
                .requestHandler(Routing.routing(vertx))
                // Start listening
                .listen(config.getInteger("VERTX_PORT", 8888))
                // Print the port
                .onItem().invoke((httpServer) ->
                        System.out.println(
                                "HTTP server started on port " + httpServer.actualPort()
                        )
                );
        return startHttpServer.replaceWithVoid();
    }
}
