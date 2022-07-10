package com.programm.vertx;

import com.programm.vertx.bootstrap.IDataBaseBootstrap;
import com.programm.vertx.bootstrap.PgClientBootstrap;
import com.programm.vertx.config.ApplicationConfig;
import com.programm.vertx.routing.Routing;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.Vertx;
import io.vertx.mutiny.core.http.HttpServer;

public class MainVerticle extends AbstractVerticle {

    public Uni<Void> asyncStart() {
        ApplicationConfig appConfig = new ApplicationConfig(ConfigRetriever.create(Vertx.vertx()));
        IDataBaseBootstrap dataBaseBootstrap = new PgClientBootstrap(appConfig, vertx);
        Routing routing = new Routing(dataBaseBootstrap);

        // Create the HTTP server
        Uni<HttpServer> startHttpServer = vertx.createHttpServer()
                // Handle every request using the router
                .requestHandler(routing.routing(vertx))
                // Start listening
                .listen(appConfig.getHttp().getPort())
                // Print the port
                .onItem().invoke((httpServer) ->
                        System.out.println(
                                "HTTP server started on port " + httpServer.actualPort()
                        )
                );

        return Uni.combine().all().unis(dataBaseBootstrap.bootstrap(), startHttpServer).discardItems();
    }
}
