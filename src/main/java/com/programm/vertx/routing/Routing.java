package com.programm.vertx.routing;

import com.programm.vertx.request.UserRequest;
import com.programm.vertx.handler.JsonHandler;
import com.programm.vertx.handler.UsersHandler;
import com.programm.vertx.handler.ValidationHandler;
import com.programm.vertx.handler.errorHandlers.RouteHandlerManager;
import com.programm.vertx.repository.inmemory.UserRepository;
import com.programm.vertx.validators.UsersValidator;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.impl.StaticHandlerImpl;

import static io.vertx.ext.web.handler.FileSystemAccess.RELATIVE;

public class Routing {
    public static Router routing(Vertx vertx) {
        Router router = Router.router(vertx);

        swagger(router);

        router.route().handler(BodyHandler.create());
        router.route().handler(new JsonHandler());

        router.route("/api/*").subRouter(users(vertx));

        errorHandler(router);
        return router;
    }

    public static void swagger(Router router) {
        StaticHandlerImpl staticHandler = new StaticHandlerImpl(RELATIVE, "swagger");
        router.get("/swagger/*").handler(staticHandler);
    }

    public static void errorHandler(Router router) {
        router.route().failureHandler(new RouteHandlerManager());
    }

    public static Router users(Vertx vertx) {

        UsersHandler usersHandler = new UsersHandler(new UserRepository());

        Router router = Router.router(vertx);

        router.get("/users").handler(usersHandler::getAll);
        router.post("/users")
                .handler(new ValidationHandler<>(UsersValidator.validator, UserRequest.class))
                .handler(usersHandler::create);

        router.get("/users/:id").handler(usersHandler::get);

        router.put("/users/:id")
                .handler(new ValidationHandler<>(UsersValidator.validator, UserRequest.class))
                .handler(usersHandler::put);

        router.delete("/users/:id").handler(usersHandler::delete);

        return router;
    }

}
