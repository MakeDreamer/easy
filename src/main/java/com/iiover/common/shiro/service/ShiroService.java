package com.iiover.common.shiro.service;

import com.iiover.common.shiro.entity.PermissionEntity;
import com.iiover.common.user.entity.UserEntity;

import java.util.List;

public interface ShiroService {
    List<PermissionEntity> getPermissionsByUser(UserEntity user);

    UserEntity getUserByUserName(String username);
}
