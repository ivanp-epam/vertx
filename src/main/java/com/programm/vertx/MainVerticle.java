package com.programm.vertx;

import com.programm.vertx.routing.Routing;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

public class MainVerticle extends AbstractVerticle {


  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    ConfigRetriever retriever = ConfigRetriever.create(vertx);
    JsonObject config = retriever.getConfig().result();

    // Create the HTTP server
    vertx.createHttpServer()
      // Handle every request using the router
      .requestHandler(Routing.routing(vertx))
      // Start listening
      .listen(config.getInteger("VERTX_PORT",8888))
      // Print the port
      .onSuccess(server ->
        System.out.println(
          "HTTP server started on port " + server.actualPort()
        )
      );
  }
}
