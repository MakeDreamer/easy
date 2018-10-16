package com.iiover.common.shiro.entity;

public class PermissionEntity {
    //uuid
    private String uuid;
    //资源url
    private String url;
    //角色id
    private String roleid;
    //介绍说明
    private String description;

    public PermissionEntity(String uuid, String url, String roleid, String description) {
        this.uuid = uuid;
        this.url = url;
        this.roleid = roleid;
        this.description = description;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRoleid() {
        return roleid;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "PermissionEntity{" +
                "uuid='" + uuid + '\'' +
                ", url='" + url + '\'' +
                ", roleid='" + roleid + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public PermissionEntity() {
        super();
    }
}
