package com.iiover.common.user.entity;

import lombok.Data;

/**
 * @author YuMing_Huai
 */
@Data
public class UserEntity {
    //uuid
    private String id;
    //用户名
    private String username;
    //密码
    private String password;
    //姓名
    private String name;
    //年龄
    private Integer age;

}
