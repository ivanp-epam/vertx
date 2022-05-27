package com.programm.vertx.dto;

import java.util.UUID;

public class UserDto {
  private String id;
  private String login;
  private String password;
  private int age;
  private boolean isDeleted;

  public static UserDto from(UserInput input) {
    return new UserDto()
      .setAge(input.getAge())
      .setId(UUID.randomUUID().toString())
      .setLogin(input.getLogin())
      .setPassword(input.getPassword());
  }

  public String getId() {
    return id;
  }

  public UserDto setId(String id) {
    this.id = id;
    return this;
  }

  public String getLogin() {
    return login;
  }

  public UserDto setLogin(String login) {
    this.login = login;
    return this;
  }

  public String getPassword() {
    return password;
  }

  public UserDto setPassword(String password) {
    this.password = password;
    return this;
  }

  public int getAge() {
    return age;
  }

  public UserDto setAge(int age) {
    this.age = age;
    return this;
  }

  public boolean isDeleted() {
    return isDeleted;
  }

  public UserDto setDeleted(boolean deleted) {
    isDeleted = deleted;
    return this;
  }
}
