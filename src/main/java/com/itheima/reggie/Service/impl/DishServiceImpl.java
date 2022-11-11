package com.itheima.reggie.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Mapper.DishMapper;
import com.itheima.reggie.Service.DishService;
import com.itheima.reggie.entity.Dish;
import org.springframework.stereotype.Service;

@Service("dishService")
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
}
