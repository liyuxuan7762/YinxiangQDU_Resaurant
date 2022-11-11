package com.itheima.reggie.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Mapper.SetmealMapper;
import com.itheima.reggie.Service.SetmealService;
import com.itheima.reggie.entity.Setmeal;
import org.springframework.stereotype.Service;

@Service("setmealService")
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
}
