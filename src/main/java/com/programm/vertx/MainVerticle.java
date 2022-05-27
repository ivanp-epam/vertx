package com.programm.vertx;

import com.programm.vertx.routing.Routing;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class MainVerticle extends AbstractVerticle {


  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    // Create the HTTP server
    vertx.createHttpServer()
      // Handle every request using the router
      .requestHandler(Routing.routing(vertx))
      // Start listening
      .listen(8888)
      // Print the port
      .onSuccess(server ->
        System.out.println(
          "HTTP server started on port " + server.actualPort()
        )
      );
  }
}
