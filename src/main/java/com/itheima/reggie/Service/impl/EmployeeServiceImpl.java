package com.itheima.reggie.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Mapper.EmployeeMapper;
import com.itheima.reggie.Service.EmployeeService;
import com.itheima.reggie.entity.Employee;
import org.springframework.stereotype.Service;

@Service("employeeService")
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
