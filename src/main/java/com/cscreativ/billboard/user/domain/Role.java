package com.cscreativ.billboard.user.domain;

import java.util.Set;
import java.util.UUID;

public class Role {
    private UUID id;
    private String name;
    private Set<Permission> permissions;

    public Role(UUID id, String name, Set<Permission> permissions) {
        this.id = id;
        this.name = name;
        this.permissions = permissions;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public Set<Permission> getPermissions() { return permissions; }
}
