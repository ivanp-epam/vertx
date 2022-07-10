package com.programm.vertx.entities;

import com.programm.vertx.request.GroupRequest;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public static Group from(GroupRequest groupRequest) {
        return new Group().setName(groupRequest.getName())
                .setPermissions(groupRequest.getPermissions()
                        .stream()
                        .map(Permission::valueOf)
                        .collect(Collectors.toList())
                );
    }

    public UUID getId() {
        return id;
    }

    public Group setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getIdAsString() {
        return id.toString();
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

    public String[] getPermissionsAsStrings() {
        return permissions.stream().map(Permission::name).toList().toArray(String[]::new);
    }

    public List<User> getUsers() {
        return users;
    }

    public Group setUsers(List<User> users) {
        this.users = users;
        return this;
    }
}
