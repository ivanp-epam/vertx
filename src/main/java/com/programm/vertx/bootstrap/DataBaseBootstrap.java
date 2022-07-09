package com.programm.vertx.bootstrap;

import com.programm.vertx.config.ApplicationConfig;
import com.programm.vertx.repository.IUserRepository;
import com.programm.vertx.repository.RepositoryManager;
import com.programm.vertx.repository.hibernate.UserRepository;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.persistence.Persistence;
import java.util.Map;

public class DataBaseBootstrap implements IDataBaseBootstrap {

    private final ApplicationConfig config;
    private final Vertx vertx;
    private Mutiny.SessionFactory sessionFactory;

    public DataBaseBootstrap(ApplicationConfig config, Vertx vertx) {
        this.config = config;
        this.vertx = vertx;
    }

    private Mutiny.SessionFactory createSessionFactory() {
        var props = Map.of(
                "javax.persistence.jdbc.url", config.getDb().getConnectionString(),
                "javax.persistence.jdbc.user", config.getDb().getDbUserName(),
                "javax.persistence.jdbc.password", config.getDb().getDbPassword()
        );  // <1>
        return Persistence
                .createEntityManagerFactory("pg-demo", props)
                .unwrap(Mutiny.SessionFactory.class);
    }

    private Mutiny.SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = createSessionFactory();
        }
        return sessionFactory;
    }

    public IUserRepository getRepository() {
        return new UserRepository(getSessionFactory());
    }

    public Uni<Void> bootstrap() {
        Uni<Void> startHibernate = Uni.createFrom().deferred(() -> {
            getSessionFactory();
            return Uni.createFrom().voidItem();
        });

        return vertx.executeBlocking(startHibernate)  // (2)
                .onItem().invoke(() -> System.out.println("✅ Hibernate Reactive is ready"));
    }

    @Override
    public RepositoryManager getManager() {
        return null;
    }
}
