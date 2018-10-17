package com.iiover.common.shiro.entity;

import lombok.Data;

@Data
public class PermissionEntity {
    //uuid
    private String uuid;
    //资源url
    private String url;
    //角色id
    private String roleid;
    //介绍说明
    private String description;


}
