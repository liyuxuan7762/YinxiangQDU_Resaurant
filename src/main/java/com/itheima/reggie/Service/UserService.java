package com.itheima.reggie.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.User;

public interface UserService extends IService<User> {
    User getUserByPhone(String phone);
    void createUser(User user);
    User getUserById(Long userId);
}
