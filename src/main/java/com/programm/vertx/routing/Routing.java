package com.programm.vertx.routing;

import com.programm.vertx.bootstrap.IDataBaseBootstrap;
import com.programm.vertx.controllers.GroupsHandler;
import com.programm.vertx.controllers.UserGroupHandler;
import com.programm.vertx.handler.middleware.JsonHandler;
import com.programm.vertx.controllers.UsersHandler;
import com.programm.vertx.handler.middleware.GroupExistsMiddlewareHandler;
import com.programm.vertx.handler.middleware.ValidationHandler;
import com.programm.vertx.handler.errorHandlers.RouteHandlerManager;
import com.programm.vertx.repository.IGroupRepository;
import com.programm.vertx.repository.IRepositoryManager;
import com.programm.vertx.repository.IUserGroupRepository;
import com.programm.vertx.repository.IUserRepository;
import com.programm.vertx.request.UserIdsRequest;
import com.programm.vertx.request.GroupRequest;
import com.programm.vertx.request.UserRequest;
import com.programm.vertx.validators.Validators;
import io.vertx.ext.web.handler.impl.StaticHandlerImpl;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.handler.BodyHandler;
import io.vertx.mutiny.ext.web.handler.StaticHandler;

import static io.vertx.ext.web.handler.FileSystemAccess.RELATIVE;

public class Routing {

    private final IDataBaseBootstrap bootstrap;

    public Routing(IDataBaseBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public Router routing(Vertx vertx) {
        Router router = Router.router(vertx);

        swagger(router);
        router.route().handler(BodyHandler.create());
        router.route().handler(new JsonHandler());

        router.route("/api/*").subRouter(users(vertx));
        router.route("/api/*").subRouter(groups(vertx));
        router.route("/api/*").subRouter(userGroups(vertx));

        errorHandler(router);
        return router;
    }

    public void swagger(Router router) {
        StaticHandler staticHandler = new StaticHandler(new StaticHandlerImpl(RELATIVE, "swagger"));
        router.get("/swagger/*").handler(staticHandler);
    }

    public void errorHandler(Router router) {
        router.route().failureHandler(new RouteHandlerManager());
    }

    public Router users(Vertx vertx) {

        UsersHandler usersHandler = new UsersHandler(bootstrap.getManager().getUserRepository());

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

        GroupsHandler groupsHandler = new GroupsHandler(bootstrap.getManager().getGroupRepository());

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

    public Router userGroups(Vertx vertx) {

        Router router = Router.router(vertx);

        IRepositoryManager manager = bootstrap.getManager();
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
