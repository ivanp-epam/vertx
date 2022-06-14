package com.programm.vertx.entities;

import com.programm.vertx.request.UserRequest;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "users")
@SQLDelete(sql = "update users set id_deleted=true where id=?")
@Where(clause = "is_deleted = false")
public class User {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private String id;


    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "age")
    private int age;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    public static User from(UserRequest input) {
        return new User()
                .setAge(input.getAge())
                .setId(UUID.randomUUID().toString())
                .setLogin(input.getLogin())
                .setPassword(input.getPassword());
    }

    public User with(UserRequest input) {
        return User.from(input).setId(id);
    }

    public String getId() {
        return id;
    }

    public User setId(String id) {
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
}
