package com.iiover.shiro;

import com.iiover.common.shiro.entity.PermissionEntity;
import com.iiover.common.shiro.service.ShiroService;
import com.iiover.common.user.entity.UserEntity;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ShiroReaml extends AuthorizingRealm {
    private static final Logger log = LoggerFactory.getLogger(ShiroReaml.class);

    private ShiroService shiroService;

    public ShiroService getShiroService() {
        return shiroService;
    }

    public void setShiroService(ShiroService shiroService) {
        this.shiroService = shiroService;
    }
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection pc) {

        /**
         *
         * 流程
         * 1.根据用户user->2.获取角色id->3.根据角色id获取权限permission
         */
        //方法一：获得user对象
        UserEntity user=(UserEntity)pc.getPrimaryPrincipal();
        log.info("This is User:"+user.toString());
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        //获取permission
        if(user!=null) {
            List<PermissionEntity> permissionsByUser = shiroService.getPermissionsByUser(user);
            if (permissionsByUser.size()!=0) {
                for (PermissionEntity p: permissionsByUser) {

                    info.addStringPermission(p.getUrl());
                }
                return info;
            }
        }

        //方法二： 从subject管理器里获取user
//      Subject subject = SecurityUtils.getSubject();
//      User _user = (User) subject.getPrincipal();
//      System.out.println("subject"+_user.getUsername());

        return null;
    }

    // 认证方法
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("进来验证了");
        //验证账号密码
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        System.out.println("1:"+token.getUsername());
        UserEntity user = shiroService.getUserByUserName(token.getUsername());
        System.out.println("2");
        if(user==null){
            return null;
        }
        //最后的比对需要交给安全管理器
        //三个参数进行初步的简单认证信息对象的包装
        AuthenticationInfo info = new SimpleAuthenticationInfo(user, user.getPassword(), this.getClass().getSimpleName());
        log.info("AuthenticationInfo::"+info.toString());
        return info;
    }

}
