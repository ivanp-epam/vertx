package com.programm.vertx.routing;

import com.programm.vertx.dto.UserInput;
import com.programm.vertx.handler.JsonHandler;
import com.programm.vertx.handler.UsersHandler;
import com.programm.vertx.handler.ValidationHandler;
import com.programm.vertx.repository.inmemory.UserRepository;
import com.programm.vertx.validators.UsersValidator;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.io.File;
import java.net.URL;

public class Routing {
    public static Router routing(Vertx vertx) {
        Router router = Router.router(vertx);
        swagger(router);

        router.route().handler(BodyHandler.create());

        router.route().handler(new JsonHandler());

        router.route("/api/*").subRouter(users(vertx));
        return router;
    }
    public static void swagger(Router router) {
//        router.get().handler(
//                StaticHandler
//                        .create()
//                        .setCachingEnabled(false)
//                        .setWebRoot("swagger"));

        router.get("/swagger").respond(event -> {
            String s = Routing.class.getClassLoader()
                    .getResource("swagger/index.html").getFile();
            System.out.println(s);
            return event.response().sendFile(s);
            }
        );
        router.get("/swagger/:filename").respond(event -> {
            String filename = event.pathParam("filename");
            ClassLoader classLoader = Routing.class.getClassLoader();
            URL resource = classLoader.getResource("swagger/" + filename);
            if(resource == null){
                return event.response().setStatusCode(404).end("Not found");
            }
            String filepath = resource.getFile();
            if(!new File(filepath).exists()){
                return event.response().setStatusCode(404).end("Not found");
            }
            return event.response().sendFile(filepath);
        });
    }

    public static Router users(Vertx vertx) {

        UsersHandler usersHandler = new UsersHandler(new UserRepository());

        Router router = Router.router(vertx);

        router.get("/users").handler(usersHandler::getAll);
        router.post("/users")
                .handler(new ValidationHandler<>(UsersValidator.validator, UserInput.class))
                .handler(usersHandler::create);

        router.get("/users/:id").handler(usersHandler::get);

        router.put("/users/:id")
                .handler(new ValidationHandler<>(UsersValidator.validator, UserInput.class))
                .handler(usersHandler::put);

        router.delete("/users/:id").handler(usersHandler::delete);

        return router;
    }

}
