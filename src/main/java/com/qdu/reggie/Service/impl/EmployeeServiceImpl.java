package com.qdu.reggie.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qdu.reggie.Mapper.EmployeeMapper;
import com.qdu.reggie.Service.EmployeeService;
import com.qdu.reggie.entity.Employee;
import org.springframework.stereotype.Service;

@Service("employeeService")
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
