package com.programm.vertx.bootstrap;

import com.programm.vertx.config.ApplicationConfig;
import com.programm.vertx.repository.IUserRepository;
import com.programm.vertx.repository.RepositoryManager;
import com.programm.vertx.repository.pgclient.GroupRepository;
import com.programm.vertx.repository.pgclient.UserRepository;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.PoolOptions;

public class PgClientBootstrap implements IDataBaseBootstrap {

    private final ApplicationConfig.DatabaseConfig dbConfig;
    private final Vertx vertx;

    public PgClientBootstrap(ApplicationConfig config, Vertx vertx) {
        this.dbConfig = config.getDb();
        this.vertx = vertx;
    }

    public PgConnectOptions makeConnectionOptions() {
        return new PgConnectOptions()
                .setPort(dbConfig.getDbPort())
                .setHost(dbConfig.getDbHost())
                .setDatabase(dbConfig.getDbName())
                .setUser(dbConfig.getDbUserName())
                .setPassword(dbConfig.getDbPassword());
    }

    public PoolOptions makePoolOptions() {
        return new PoolOptions()
                .setMaxSize(5);
    }

    public PgPool getClient() {
        return PgPool.pool(vertx, makeConnectionOptions(), makePoolOptions());
    }

    @Override
    public Uni<Void> bootstrap() {
        return Uni.createFrom().voidItem();
    }


    @Override
    public RepositoryManager getManager() {
        return new RepositoryManager(getClient());
    }
}
