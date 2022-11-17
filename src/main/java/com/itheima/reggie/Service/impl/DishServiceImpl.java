package com.itheima.reggie.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Mapper.DishMapper;
import com.itheima.reggie.Service.*;
import com.itheima.reggie.common.CustomerException;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.SetmealDish;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private OrderDetailService orderDetailService;

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
    @CacheEvict(value = "dishCache", key = "#dishDto.categoryId")
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

    }

    @Override
    @CacheEvict(value = "dishCache", allEntries = true)
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

    }

    @Override
    @Transactional
    @CacheEvict(value = "dishCache", allEntries = true)
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

    }

    @Override
    @CacheEvict(value = "dishCache", allEntries = true)
    public void startSaleDish(String ids) {
        String[] dishIds = ids.split(",");
        UpdateWrapper<Dish> dishUpdateWrapper = new UpdateWrapper<>();
        dishUpdateWrapper.set("status", 1);
        dishUpdateWrapper.in("id", Arrays.asList(dishIds));
        super.update(null, dishUpdateWrapper);
        // 清空所有缓存
    }

    @Override
    @Cacheable(value = "dishCache", key = "#dish.categoryId", unless = "#result == null")
    public List<DishDto> getDishesByCategoryId(Dish dish) {
        // 1.首先从redis中尝试获取数据，如果获取到数据了，那么直接返回，否则再去数据库查询，并将查询结果存储到redis中
        // 2. 如果缓存中没有的话 使用数据库查询 首先根据categoryId查出菜品的基本信息
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        queryWrapper.eq(Dish::getStatus, 1);
        List<Dish> dishList = super.list(queryWrapper);

        // 2.遍历产品基本信息list，然后根据菜品id，去dish_flavor表中查询菜品对应的口味信息
        List<DishDto> dishDtoList = new ArrayList<>();
        DishDto dishDto = null;
        for (Dish d : dishList) {
            dishDto = new DishDto();
            BeanUtils.copyProperties(d, dishDto);
            List<DishFlavor> flavorList = dishFlavorService.list(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, d.getId()));
            dishDto.setFlavors(flavorList);

            // 查询菜品的销量
            Long saleNum = orderDetailService.getSaleNumByDishId(d.getId(), 0);
            dishDto.setSaleNum(saleNum);

            dishDtoList.add(dishDto);
        }

        return dishDtoList;
    }

    @Override
    public Page page(int page, int pageSize, String name) {
        Page<Dish> dishPageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPageInfo = new Page<>(page, pageSize);
        List<DishDto> records = new ArrayList<>();
        // 创建条件
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.like(name != null, Dish::getName, name);
        dishLambdaQueryWrapper.orderByAsc(Dish::getSort);
        // 执行查询
        super.page(dishPageInfo);

        // 执行完查询以后可以查询到dish的所有信息 然后使用对象拷贝，将DishPageInfo中的属性都拷贝到dishDtoPageInfo
        BeanUtils.copyProperties(dishPageInfo, dishDtoPageInfo, "records");

        // 遍历records，根据categoryId查询其对应的类型名称，然后赋值给dto的categoryName属性，最后将所有的dto封装到list
        // 然后传递给dishDtoPageInfo的record属性即可
        DishDto dishDto = null;
        for (Dish dish : dishPageInfo.getRecords()) {
            // 获取categoryId,然后根据Id查找到对应的category, 然后获取到category的name属性，赋值给dishDto
            // 由于dishDto是new出来的，里面所有的属性都为空，因此需要先将dish的属性拷贝到dishDto
            dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);
            Category category = categoryService.getById(dish.getCategoryId());
            if (category != null) {
                dishDto.setCategoryName(category.getName());
                records.add(dishDto);
            }
        }

        dishDtoPageInfo.setRecords(records);
        return dishDtoPageInfo;
    }

    @Override
    public Dish getDishById(Long id) {
        return super.getById(id);
    }


}
