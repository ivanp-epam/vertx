package com.programm.vertx.routing;

import com.programm.vertx.bootstrap.IDataBaseBootstrap;
import com.programm.vertx.bootstrap.JwtAuthBootstrap;
import com.programm.vertx.controllers.AuthHandler;
import com.programm.vertx.controllers.GroupsHandler;
import com.programm.vertx.controllers.UserGroupHandler;
import com.programm.vertx.controllers.UsersHandler;
import com.programm.vertx.handler.errorHandlers.RouteHandlerManager;
import com.programm.vertx.handler.middleware.*;
import com.programm.vertx.repository.IGroupRepository;
import com.programm.vertx.repository.IRepositoryManager;
import com.programm.vertx.repository.IUserGroupRepository;
import com.programm.vertx.repository.IUserRepository;
import com.programm.vertx.request.AuthRequest;
import com.programm.vertx.request.GroupRequest;
import com.programm.vertx.request.UserIdsRequest;
import com.programm.vertx.request.UserRequest;
import com.programm.vertx.validators.Validators;
import io.vertx.ext.web.handler.impl.StaticHandlerImpl;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.Route;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.handler.BodyHandler;
import io.vertx.mutiny.ext.web.handler.StaticHandler;
import lombok.extern.slf4j.Slf4j;

import static io.vertx.ext.web.handler.FileSystemAccess.RELATIVE;

@Slf4j
public class Routing {

    private final IDataBaseBootstrap dataBaseBootstrap;
    private final JwtAuthBootstrap authBootstrap;

    public Routing(IDataBaseBootstrap dataBaseBootstrap, JwtAuthBootstrap authBootstrap) {
        log.info("Start Routing");
        this.dataBaseBootstrap = dataBaseBootstrap;
        this.authBootstrap = authBootstrap;
    }

    public Router routing(Vertx vertx) {
        log.info("Start Router routing");
        Router router = Router.router(vertx);

        swagger(router);
        router.route().handler(BodyHandler.create());
        router.route().handler(new LoggingMetadataAdd());
        router.route().handler(new JsonHandler());
        router.route("/api/*").subRouter(auth(vertx));

        auth(router.route("/api/*"));

        router.route("/api/*").subRouter(users(vertx));
        router.route("/api/*").subRouter(groups(vertx));
        router.route("/api/*").subRouter(userGroups(vertx));

        errorHandler(router);
        return router;
    }

    private void auth(Route route) {
        JWTAuthHandler authHandler = new JWTAuthHandler(this.authBootstrap.jwtAuthProvider());
        route.handler(authHandler::handle);
    }


    public void swagger(Router router) {
        StaticHandler staticHandler = new StaticHandler(new StaticHandlerImpl(RELATIVE, "swagger"));
        router.get("/swagger/*").handler(staticHandler);
    }

    public void errorHandler(Router router) {
        router.route().failureHandler(new RouteHandlerManager());
    }

    public Router users(Vertx vertx) {

        UsersHandler usersHandler = new UsersHandler(dataBaseBootstrap.getManager().getUserRepository());

        Router router = Router.router(vertx);

        router.get("/users").handler(usersHandler::getAll);
        router.post("/users")
                .handler(new ValidationHandler<>(Validators.USER_VALIDATOR, UserRequest.class))
                .handler(usersHandler::create);

        router.get("/users/:id").handler(usersHandler::get);
        router.put("/users/:id")
                .handler(new ValidationHandler<>(Validators.USER_VALIDATOR, UserRequest.class))
                .handler(usersHandler::put);
        router.delete("/users/:id").handler(usersHandler::delete);

        return router;
    }

    public Router groups(Vertx vertx) {

        GroupsHandler groupsHandler = new GroupsHandler(dataBaseBootstrap.getManager().getGroupRepository());

        Router router = Router.router(vertx);

        router.get("/groups").handler(groupsHandler::getAll);
        router.post("/groups")
                .handler(new ValidationHandler<>(Validators.GROUP_VALIDATOR, GroupRequest.class))
                .handler(groupsHandler::create);

        router.get("/groups/:id").handler(groupsHandler::get);
        router.put("/groups/:id")
                .handler(new ValidationHandler<>(Validators.GROUP_VALIDATOR, GroupRequest.class))
                .handler(groupsHandler::put);
        router.delete("/groups/:id").handler(groupsHandler::delete);

        return router;
    }

    public Router auth(Vertx vertx) {
        Router router = Router.router(vertx);
        AuthHandler authHandler = new AuthHandler(
                authBootstrap.jwtAuthProvider(), dataBaseBootstrap.getManager().getAuthRepository()
        );

        router.post("/login")
                .handler(new ValidationHandler<>(Validators.AUTH_REQUEST_VALIDATOR, AuthRequest.class))
                .handler(authHandler::auth);

        return router;
    }

    public Router userGroups(Vertx vertx) {

        Router router = Router.router(vertx);

        IRepositoryManager manager = dataBaseBootstrap.getManager();
        IGroupRepository groupRepository = manager.getGroupRepository();
        IUserGroupRepository userGroupRepository = manager.getUserGroupRepository();
        IUserRepository userRepository = manager.getUserRepository();

        UserGroupHandler groupsHandler = new UserGroupHandler(userGroupRepository, userRepository);

        GroupExistsMiddlewareHandler groupExistsMiddleware = new GroupExistsMiddlewareHandler(groupRepository);

        router.route("/groups/:groupId/users/*").handler(groupExistsMiddleware);

        router.get("/groups/:groupId/users").handler(groupsHandler::getUsers);
        router.put("/groups/:groupId/users")
                .handler(new ValidationHandler<>(Validators.GROUP_IDS_VALIDATOR, UserIdsRequest.class))
                .handler(groupsHandler::updateUsers);
        router.delete("/groups/:groupId/users").handler(groupsHandler::deleteUsersFromGroup);

        router.get("/groups/:groupId/users/:userId").handler(groupsHandler::getUser);
        router.delete("/groups/:groupId/users/:userId").handler(groupsHandler::deleteParticularUsersFromGroup);

        return router;
    }

}
