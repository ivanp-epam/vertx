package com.programm.vertx.bootstrap;

import com.programm.vertx.repository.IRepositoryManager;
import com.programm.vertx.repository.IUserRepository;
import io.smallrye.mutiny.Uni;

public interface IDataBaseBootstrap {
    public Uni<Void> bootstrap();

    public IRepositoryManager getManager();
}
