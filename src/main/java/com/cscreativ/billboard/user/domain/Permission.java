package com.cscreativ.billboard.user.domain;

import java.util.UUID;

public class Permission {
    private UUID id;
    private String name;

    public Permission(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
}
