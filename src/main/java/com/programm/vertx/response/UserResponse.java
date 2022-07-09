package com.programm.vertx.response;

import com.programm.vertx.entities.Group;
import com.programm.vertx.entities.Permission;
import com.programm.vertx.entities.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserResponse implements Serializable {
    private String id;
    private String login;
    private String password;
    private int age;
    private List<GroupResponse> groups = new ArrayList<>();

    public static UserResponse from(User dto) {
        List<GroupResponse> groups = new ArrayList<>();
        dto.getGroups().forEach(group -> groups.add(new GroupResponse(group)));

        return new UserResponse()
                .setAge(dto.getAge())
                .setId(dto.getStringId())
                .setLogin(dto.getLogin())
                .setPassword(dto.getPassword())
                .setGroups(groups);
    }

    public String getId() {
        return id;
    }

    public UserResponse setId(String id) {
        this.id = id;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public UserResponse setLogin(String login) {
        this.login = login;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserResponse setPassword(String password) {
        this.password = password;
        return this;
    }

    public int getAge() {
        return age;
    }

    public UserResponse setAge(int age) {
        this.age = age;
        return this;
    }

    public List<GroupResponse> getGroups() {
        return groups;
    }

    public UserResponse setGroups(List<GroupResponse> groups) {
        this.groups = groups;
        return this;
    }

    public static class GroupResponse implements Serializable {
        private final String uuid;
        private final String name;
        private final List<Permission> permissions;

        public GroupResponse(Group group) {
            this.uuid = group.getId().toString();
            this.name = group.getName();
            this.permissions = group.getPermissions();
        }

        public String getUuid() {
            return uuid;
        }

        public String getName() {
            return name;
        }

        public List<Permission> getPermissions() {
            return permissions;
        }
    }
}
