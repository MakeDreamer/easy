package com.iiover.common.user.service.impl;


import com.iiover.common.user.dao.UserMapper;
import com.iiover.common.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private UserMapper userMapper;
    @Override
    public List<Map<String, Object>> getUserList() {

        return userMapper.getUserList();
    }
}
