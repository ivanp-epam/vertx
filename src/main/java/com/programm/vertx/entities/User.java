package com.programm.vertx.entities;

import com.programm.vertx.request.UserRequest;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE id = $1")
@Where(clause = "is_deleted = false")
public class User implements Serializable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false)
    @Type(type = "pg-uuid")
    private UUID id;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "age")
    private int age;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_groups",
            joinColumns = @JoinColumn(name = "users_id"),
            inverseJoinColumns = @JoinColumn(name = "groups_id")
    )
    private List<Group> groups = new ArrayList<>();

    public User() {

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
