package com.iiover.common.shiro.service.impl;

import com.iiover.common.shiro.dao.ShiroDao;
import com.iiover.common.shiro.entity.PermissionEntity;
import com.iiover.common.shiro.service.ShiroService;
import com.iiover.common.user.entity.UserEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("shiroService")
public class ShiroServiceImpl implements ShiroService {
    @Autowired
    private ShiroDao shiroDao;
    @Override
    public UserEntity getUserByUserName(String username) {
        //根据账号获取账号密码
        UserEntity  userByUserName = shiroDao.getUserByUserName(username);
        return userByUserName;
    }

    @Override
    public List<PermissionEntity> getPermissionsByUser(UserEntity user) {
        //获取到用户角色userRole
        List<String> roleId = shiroDao.getUserRoleByUserId(user.getId());
        List<PermissionEntity> perArrary = new ArrayList();

        if (roleId!=null&&roleId.size()!=0) {
            //根据roleid获取peimission

            for (int i = 0;i<roleId.size();i++){
                perArrary.addAll(shiroDao.getPermissionsByRoleId(roleId.get(i)));
            }

        }

        System.out.println(perArrary);
        return perArrary;
    }
}
