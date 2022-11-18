package com.qdu.reggie.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qdu.reggie.Mapper.SetmealDishMapper;
import com.qdu.reggie.Service.SetmealDishService;
import com.qdu.reggie.entity.SetmealDish;
import org.springframework.stereotype.Service;

@Service("setmealDishService")
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
