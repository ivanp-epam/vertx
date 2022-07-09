package com.programm.vertx.bootstrap;

import com.programm.vertx.repository.IUserRepository;
import com.programm.vertx.repository.RepositoryManager;
import io.smallrye.mutiny.Uni;

public interface IDataBaseBootstrap {
    public IUserRepository getRepository();
    public Uni<Void> bootstrap();

    public RepositoryManager getManager();
}
