package com.itheima.reggie.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Mapper.SetmealDishMapper;
import com.itheima.reggie.Service.SetmealDishService;
import com.itheima.reggie.entity.SetmealDish;
import org.springframework.stereotype.Service;

@Service("setmealDishService")
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
