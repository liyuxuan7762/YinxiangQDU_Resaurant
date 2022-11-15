package com.itheima.reggie.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Mapper.DishMapper;
import com.itheima.reggie.Service.DishFlavorService;
import com.itheima.reggie.Service.DishService;
import com.itheima.reggie.Service.SetmealDishService;
import com.itheima.reggie.Service.SetmealService;
import com.itheima.reggie.common.CustomerException;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.SetmealDish;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service("dishService")
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private RedisTemplate redisTemplate;

    // 实现对菜品的添加
    @Transactional
    @CacheEvict(value = "dishCache", key = "#dishDto.categoryId")
    public void addDish(DishDto dishDto) {
        // 先完成对dish表的添加 都是基本信息 所以直接添加即可
        this.save(dishDto); // 这里可以传dto是因为dto继承了dish类
        // 然后遍历dto中的dishFlavor列表，因为从前端传递的数据里面列表中只有name，value，没有对应的dishID
        // 因此要先遍历list，将每一个flavor对象的dishId设置一下
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor df : flavors) {
            df.setDishId(dishDto.getId());
        }
        // 只清理更新产品所在分类的所有菜品信息
        redisTemplate.delete(redisTemplate.keys("dish_" + dishDto.getCategoryId() + "_1"));
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
        redisTemplate.delete(redisTemplate.keys("*"));
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
        for (DishFlavor flavor : dishDto.getFlavors()) {
            flavor.setDishId(dishDto.getId());
        }
        dishFlavorService.saveBatch(dishDto.getFlavors());
        // 只清理更新产品所在分类的所有菜品信息
        redisTemplate.delete(redisTemplate.keys("dish_" + dishDto.getCategoryId() + "_1"));
    }

    @Override
    public void deleteDish(String ids) {
        // 查询dish的状态
        List<String> idList = Arrays.asList(ids.split(","));
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, idList);
        queryWrapper.eq(Dish::getStatus, 1);
        int count = super.count(queryWrapper);
        if (count > 0) {
            throw new CustomerException("菜品正在售卖中，无法删除");
        }

        // 删除
        super.removeByIds(idList);
        redisTemplate.delete(redisTemplate.keys("*"));
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
        for (SetmealDish setmealDish : setmealDisheList) {
            setmealIds.append(setmealDish.getSetmealId()).append(",");
        }
        setmealService.offSaleSetmeal(setmealIds.toString());
        redisTemplate.delete(redisTemplate.keys("*"));
    }

    @Override
    public void startSaleDish(String ids) {
        String[] dishIds = ids.split(",");
        UpdateWrapper<Dish> dishUpdateWrapper = new UpdateWrapper<>();
        dishUpdateWrapper.set("status", 1);
        dishUpdateWrapper.in("id", Arrays.asList(dishIds));
        super.update(null, dishUpdateWrapper);
        // 清空所有缓存
        redisTemplate.delete(redisTemplate.keys("*"));
    }

    @Override
    public List<DishDto> getDishesByCategoryId(Dish dish) {
        // 1.首先从redis中尝试获取数据，如果获取到数据了，那么直接返回，否则再去数据库查询，并将查询结果存储到redis中
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        List<DishDto> dishDtoList = null;
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if (dishDtoList != null) {
            return dishDtoList;
        }
        // 2. 如果缓存中没有的话 使用数据库查询 首先根据categoryId查出菜品的基本信息
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        queryWrapper.eq(Dish::getStatus, 1);
        List<Dish> dishList = super.list(queryWrapper);

        // 2.遍历产品基本信息list，然后根据菜品id，去dish_flavor表中查询菜品对应的口味信息
        dishDtoList = new ArrayList<>();
        DishDto dishDto = null;
        for (Dish d : dishList) {
            dishDto = new DishDto();
            BeanUtils.copyProperties(d, dishDto);
            List<DishFlavor> flavorList = dishFlavorService.list(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, d.getId()));
            dishDto.setFlavors(flavorList);
            dishDtoList.add(dishDto);
        }
        // 将查询后的数据放到redis中
        redisTemplate.opsForValue().set(key, dishDtoList, 60, TimeUnit.MINUTES);
        return dishDtoList;
    }
}
