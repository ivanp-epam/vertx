package com.programm.vertx.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.json.JsonObject;

public class ApplicationConfig {

    private final JsonObject configResult;

    private final DatabaseConfig db = new DatabaseConfig();
    private final HttpConfig http = new HttpConfig();

    public ApplicationConfig(ConfigRetriever retriever) {
        configResult = retriever.getConfig().result();
    }

    public DatabaseConfig getDb() {
        return db;
    }

    public HttpConfig getHttp() {
        return http;
    }

    public class HttpConfig {
        public int getPort() {
            return configResult.getInteger("VERTX_PORT", 8888);
        }
    }

    public class DatabaseConfig {
        public int getDbPort() {
            return configResult.getInteger("PG_PORT", 5435);
        }

        public String getDbHost() {
            return configResult.getString("PG_HOST", "localhost");
        }

        public String getDbName() {
            return configResult.getString("PG_DB", "default_database");
        }

        public String getDbUserName() {
            return configResult.getString("PG_USER", "postresql");
        }

        public String getDbPassword() {
            return configResult.getString("PG_PASSWORD", "password");
        }

        public String getConnectionString() {
            return String.format("jdbc:postgresql://%s:%s/%s", getDbHost(), getDbPort(), getDbName());
        }
    }

}
