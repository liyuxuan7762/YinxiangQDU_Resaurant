package com.itheima.reggie.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Mapper.UserMapper;
import com.itheima.reggie.Service.UserService;
import com.itheima.reggie.entity.User;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public User getUserByPhone(String phone) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        return super.getOne(queryWrapper);
    }

    @Override
    public void createUser(User user) {
        super.save(user);
    }

    @Override
    public User getUserById(Long userId) {
        return super.getById(userId);
    }
}
