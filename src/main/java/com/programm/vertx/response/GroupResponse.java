package com.programm.vertx.response;

import com.programm.vertx.entities.Group;
import com.programm.vertx.entities.Permission;
import com.programm.vertx.entities.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupResponse implements Serializable {
    private String id;
    private String name;
    private List<Permission> permissions;
    private List<UserResponse> users = new ArrayList<>();

    public static GroupResponse from(Group dto) {
        return new GroupResponse()
                .setId(dto.getIdAsString())
                .setName(dto.getName())
                .setPermissions(dto.getPermissions())
                .setUsers(dto.getUsers().stream().map(UserResponse::new).collect(Collectors.toList()));
    }

    public String getId() {
        return id;
    }

    public GroupResponse setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public GroupResponse setName(String name) {
        this.name = name;
        return this;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public GroupResponse setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
        return this;
    }

    public List<UserResponse> getUsers() {
        return users;
    }

    public GroupResponse setUsers(List<UserResponse> users) {
        this.users = users;
        return this;
    }

    public static class UserResponse {
        private final String id;
        private final String login;
        private final String password;
        private final int age;

        public UserResponse(User user) {
            this.id = user.getStringId();
            this.login = user.getLogin();
            this.password = user.getPassword();
            this.age = user.getAge();
        }

        public String getId() {
            return id;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }

        public int getAge() {
            return age;
        }
    }
}
