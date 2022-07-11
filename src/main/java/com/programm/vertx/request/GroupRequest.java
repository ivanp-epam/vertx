package com.programm.vertx.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupRequest {

    private String name;
    private List<String> permissions;

    public String getName() {
        return name;
    }

    public GroupRequest setName(String name) {
        this.name = name;
        return this;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public GroupRequest setPermissions(List<String> permissions) {
        this.permissions = permissions;
        return this;
    }
}
