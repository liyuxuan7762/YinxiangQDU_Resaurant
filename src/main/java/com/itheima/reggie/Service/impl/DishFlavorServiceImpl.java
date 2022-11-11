package com.itheima.reggie.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Mapper.DishFlavorMapper;
import com.itheima.reggie.Service.DishFlavorService;
import com.itheima.reggie.entity.DishFlavor;
import org.springframework.stereotype.Service;

@Service("dishFlavorService")
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
