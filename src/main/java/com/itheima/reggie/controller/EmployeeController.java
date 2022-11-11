package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.Service.EmployeeService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // 用户登录
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        // 1. 将密码进行MD5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2.调用MybatisPlus到数据库中根据用户名查找该用户
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee user = employeeService.getOne(queryWrapper);

        // 如果用户不存在 登录失败
        if(user == null) {
            return R.error("登录失败");
        }

        // 如果密码不正确
        if(!user.getPassword().equals(password)) {
            return R.error("登录失败");
        }

        // 如果被禁用 登录失败
        if(user.getStatus() == 0) {
            return R.error("账户被禁用");
        }

        // 登陆成功
        request.getSession().setAttribute("employee", user.getId());
        return R.success(user);
    }

    // 用户退出
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    // 新增员工
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        // 设置初始密码123456
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        // 设置创建时间
        // employee.setCreateTime(LocalDateTime.now());
        // 设置创建人
        // Long currentEmpId = (Long) request.getSession().getAttribute("employee");
        // employee.setCreateUser(currentEmpId);
        // 设置更新时间
        // employee.setUpdateTime(LocalDateTime.now());
        // 设置更新人
        // employee.setUpdateUser(currentEmpId);
        // 使用MP保存到数据库
        employeeService.save(employee);
        // 返回信息
        return R.success("添加用户成功");
    }

    // 根据员工姓名(姓名可选)分页查询员工信息
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        // 创建Page对象，将分页信息设置到Page对象中
        Page<Employee> pageInfo = new Page<>(page, pageSize);
        // 创建条件对象 按照姓名模糊查询 并按照创建时间降序
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        // 添加like条件
        queryWrapper.like(StringUtils.hasText(name), Employee::getName, name);
        // 添加降序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        // 执行查询
        employeeService.page(pageInfo, queryWrapper);
        // 封装相应信息
        return R.success(pageInfo);
    }

    // 更新员工信息

    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        // 设置更新时间 这些公共字段通过MP的metaObjectHandler处理
        // employee.setUpdateTime(LocalDateTime.now());
        // 设置更新人
        // employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));

        employeeService.updateById(employee);

        return R.success("更新成功！");
    }

    // 根据ID查询员工信息 用于修改员工信息功能
    @GetMapping("/{id}")
    public R<Employee> getEmployeeById(@PathVariable (name = "id") Long id) {
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        } else {
            return R.error("没有查询到员工信息");
        }
    }

}
