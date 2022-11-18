package com.qdu.reggie.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qdu.reggie.Mapper.DishFlavorMapper;
import com.qdu.reggie.Service.DishFlavorService;
import com.qdu.reggie.entity.DishFlavor;
import org.springframework.stereotype.Service;

@Service("dishFlavorService")
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
