package com.programm.vertx.entities;

import com.programm.vertx.request.UserRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User implements Serializable {

    private UUID id;

    private String login;

    private String password;

    private int age;

    private boolean isDeleted;

    private List<Group> groups = new ArrayList<>();

    public User() {

    }

    public User(String id) {
        this.id = UUID.fromString(id);
    }

    public static User from(UserRequest input) {
        return new User()
                .setAge(input.getAge())
                .setStringId(UUID.randomUUID().toString())
                .setLogin(input.getLogin())
                .setPassword(input.getPassword());
    }

    public User with(UserRequest input) {
        return User.from(input).setStringId(getStringId());
    }

    public String getStringId() {
        return id.toString();
    }

    public User setStringId(String id) {
        this.id = UUID.fromString(id);
        return this;
    }

    public UUID getId() {
        return id;
    }

    public User setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public User setLogin(String login) {
        this.login = login;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public int getAge() {
        return age;
    }

    public User setAge(int age) {
        this.age = age;
        return this;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public User setDeleted(boolean deleted) {
        isDeleted = deleted;
        return this;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public User setGroups(List<Group> groups) {
        this.groups = groups;
        return this;
    }
}
