package com.programm.vertx.response;

import com.programm.vertx.entities.User;

public class UserResponse {
    private String id;
    private String login;
    private String password;
    private int age;

    public static UserResponse from(User dto) {
        return new UserResponse()
                .setAge(dto.getAge())
                .setId(dto.getStringId())
                .setLogin(dto.getLogin())
                .setPassword(dto.getPassword());
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
}
