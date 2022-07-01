package com.programm.vertx.entities;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type = "pg-uuid")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "permissions")
    @Type(type = "list-array")
    private List<Permission> permissions = new ArrayList<>();

    @ManyToMany(targetEntity = User.class, mappedBy = "groups", fetch = FetchType.EAGER)
    private List<User> users = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public Group setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Group setName(String name) {
        this.name = name;
        return this;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public Group setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
        return this;
    }

    public List<User> getUsers() {
        return users;
    }

    public Group setUsers(List<User> users) {
        this.users = users;
        return this;
    }
}
