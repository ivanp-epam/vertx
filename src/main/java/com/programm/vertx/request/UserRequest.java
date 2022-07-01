package com.programm.vertx.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRequest {
    private String login;
    private String password;
    private int age;
    private boolean isDeleted;

    public String getLogin() {
        return login;
    }

    public UserRequest setLogin(String login) {
        this.login = login;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    public int getAge() {
        return age;
    }

    public UserRequest setAge(int age) {
        this.age = age;
        return this;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public UserRequest setDeleted(boolean deleted) {
        isDeleted = deleted;
        return this;
    }
}
