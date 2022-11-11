package com.itheima.reggie.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Mapper.DishMapper;
import com.itheima.reggie.Service.CategoryService;
import com.itheima.reggie.Service.DishFlavorService;
import com.itheima.reggie.Service.DishService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("dishService")
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    // 实现对菜品的添加
    @Transactional
    public void addDish(DishDto dishDto) {
        // 先完成对dish表的添加 都是基本信息 所以直接添加即可
        this.save(dishDto); // 这里可以传dto是因为dto继承了dish类

        // 然后遍历dto中的dishFlavor列表，因为从前端传递的数据里面列表中只有name，value，没有对应的dishID
        // 因此要先遍历list，将每一个flavor对象的dishId设置一下

        List<DishFlavor> flavors = dishDto.getFlavors();
        for(DishFlavor df : flavors) {
            df.setDishId(dishDto.getId());
        }

        dishFlavorService.saveBatch(flavors);

    }

    @Override
    public DishDto edit(Long id) {
        // 根据ID查询dish信息
        DishDto dishDto = new DishDto();
        Dish dish = this.getById(id);
        BeanUtils.copyProperties(dish, dishDto);
        // 然后根据ID查询口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(dishFlavorList);
        return dishDto;
    }

    @Override
    public void updateDish(DishDto dishDto) {
        // 更新dish表
        this.updateById(dishDto);
        // 删除flavor表中dishId的字段
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        // 重新添加flavor
        for(DishFlavor flavor : dishDto.getFlavors()) {
            flavor.setDishId(dishDto.getId());
        }
        dishFlavorService.saveBatch(dishDto.getFlavors());
    }
}
