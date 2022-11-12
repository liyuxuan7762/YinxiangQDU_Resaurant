package com.itheima.reggie.controller;

import com.itheima.reggie.Service.UserService;
import com.itheima.reggie.common.CodeGenerator;
import com.itheima.reggie.common.MailUtils;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    // 实现用户登录向邮箱发送验证码
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        // 生成验证码
        String code = CodeGenerator.generateCode4();
        // 发送验证码
        MailUtils.sendCode(user.getPhone(), code);
        // 将验证码保存到session中
        session.setAttribute(user.getPhone(), code);

        return R.success("验证码发送成功");
    }

    // 用户登录
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        // 1.获取用户的邮箱和验证码
        // 2.从session中获取验证码和用户传递的验证码比较
        // 3.判断用户是否注册过，如果没有自动注册
        // 4.返回相应信息

        String email = map.get("phone").toString();
        String code = map.get("code").toString();

        String codeInSession = session.getAttribute(email).toString();
        if (code.equals(codeInSession)) {
            // 如果验证码正确 判断用户是否存在
            User user = userService.getUserByPhone(email);
            if (user == null) {
                // 创建新用户
                user = new User();
                user.setPhone(email);
                user.setStatus(1);
                userService.createUser(user);
                user = userService.getUserByPhone(email); // 为了获取新创建的用户的ID
            }
            // 这里原代码有问题，此时user并没有Id
            session.setAttribute("user", user.getId());
            return R.success(user);
        } else {
            // 验证码不正确
            return R.error("验证码错误");
        }
    }
}
