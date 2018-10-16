package com.iiover.common.shiro.dao;


import com.iiover.common.shiro.entity.PermissionEntity;
import com.iiover.common.user.entity.UserEntity;

import java.util.Collection;
import java.util.List;

public interface ShiroDao {
    UserEntity getUserByUserName(String username);

    List<String> getUserRoleByUserId(String id);

    Collection<? extends PermissionEntity> getPermissionsByRoleId(String i);
}
