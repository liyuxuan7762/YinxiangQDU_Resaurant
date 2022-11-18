package com.qdu.reggie.controller;

import com.qdu.reggie.Service.UserService;
import com.qdu.reggie.common.CodeGenerator;
import com.qdu.reggie.common.MailUtils;
import com.qdu.reggie.common.R;
import com.qdu.reggie.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    // 引入RedisTemplate
    @Autowired
    private RedisTemplate redisTemplate;

    // 实现用户登录向邮箱发送验证码
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody Map<String, String> map, HttpSession session) {
        // 生成验证码
        String code = CodeGenerator.generateCode4();
        String email = map.get("email");
        log.info("验证码是" + code);
        // 发送验证码
        MailUtils.sendCode(email, code);
        // 将验证码保存到session中
        // session.setAttribute(user.getPhone(), code);
        // 将验证码保存到redis中 设置生命周期为5分钟
        redisTemplate.opsForValue().set(email, code, 5, TimeUnit.MINUTES);

        return R.success("验证码发送成功");
    }

    // 用户登录
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        // 1.获取用户的邮箱和验证码
        // 2.从session中获取验证码和用户传递的验证码比较
        // 3.判断用户是否注册过，如果没有自动注册
        // 4.返回相应信息

        String email = map.get("email").toString();
        String code = map.get("code").toString();
        // 从session中读取验证码
        // String codeInSession = session.getAttribute(email).toString();

        // 从redis中读取验证码
        String codeInSession = (String) redisTemplate.opsForValue().get(email);

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
            // 如果登录成功的话 就清空redis中这个用户的验证码
            redisTemplate.delete(email);
            return R.success(user);
        } else {
            // 验证码不正确
            return R.error("验证码错误");
        }
    }

    @PostMapping("/logout")
    public R<String> logout(HttpSession session) {
        session.removeAttribute("user");
        return R.success("退出成功!");
    }
}
