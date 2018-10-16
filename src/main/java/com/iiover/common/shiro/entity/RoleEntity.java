package com.iiover.common.shiro.entity;

public class RoleEntity {
    //uuid
    private String uuid;
    //角色
    private String role;
    //描述
    private String description;

    public RoleEntity(String uuid, String role, String description) {
        this.uuid = uuid;
        this.role = role;
        this.description = description;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "RoleEntity{" +
                "uuid='" + uuid + '\'' +
                ", role='" + role + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public RoleEntity() {
        super();
    }
}
