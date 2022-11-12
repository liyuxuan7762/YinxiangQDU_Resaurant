package com.itheima.reggie.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Mapper.DishMapper;
import com.itheima.reggie.Service.*;
import com.itheima.reggie.common.CustomerException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service("dishService")
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

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
    @Transactional
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

    @Override
    public void deleteDish(String ids) {
        // 查询dish的状态
        List<String> idList = Arrays.asList(ids.split(","));
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, idList);
        queryWrapper.eq(Dish::getStatus, 1);
        int count = super.count(queryWrapper);
        if(count > 0) {
            throw new CustomerException("菜品正在售卖中，无法删除");
        }

        // 删除
        super.removeByIds(idList);
    }

    @Override
    @Transactional
    public void offSaleDish(String ids) {
        String[] dishIds = ids.split(",");
        UpdateWrapper<Dish> dishUpdateWrapper = new UpdateWrapper<>();
        dishUpdateWrapper.set("status", 0);
        dishUpdateWrapper.in("id", Arrays.asList(dishIds));
        super.update(null, dishUpdateWrapper);

        // 停售菜品后需要相应的把包含这个菜品的所有套餐也停售
        // 1.根据菜品ID到dish_setmeal表中查询到所有包含该菜品的setmeal_id
        List<SetmealDish> setmealDisheList = setmealDishService.list(new QueryWrapper<SetmealDish>().select("DISTINCT `setmeal_id`").lambda().in(SetmealDish::getDishId, Arrays.asList(dishIds)));
        // 2.根据setmeal_id到setmeal表中设置status字段为0
        StringBuilder setmealIds = new StringBuilder();
        for(SetmealDish setmealDish : setmealDisheList) {
            setmealIds.append(setmealDish.getSetmealId()).append(",");
        }
        setmealService.offSaleSetmeal(setmealIds.toString());
    }

    @Override
    public void startSaleDish(String ids) {
        String[] dishIds = ids.split(",");
        UpdateWrapper<Dish> dishUpdateWrapper = new UpdateWrapper<>();
        dishUpdateWrapper.set("status", 1);
        dishUpdateWrapper.in("id", Arrays.asList(dishIds));
        super.update(null, dishUpdateWrapper);
    }
}
