package com.programm.vertx.dto;

public class UserInput {
  private String login;
  private String password;
  private int age;
  private boolean isDeleted;

  public String getLogin() {
    return login;
  }

  public UserInput setLogin(String login) {
    this.login = login;
    return this;
  }

  public String getPassword() {
    return password;
  }

  public UserInput setPassword(String password) {
    this.password = password;
    return this;
  }

  public int getAge() {
    return age;
  }

  public UserInput setAge(int age) {
    this.age = age;
    return this;
  }

  public boolean isDeleted() {
    return isDeleted;
  }

  public UserInput setDeleted(boolean deleted) {
    isDeleted = deleted;
    return this;
  }
}
