package com.itheima.reggie.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Mapper.CategoryMapper;
import com.itheima.reggie.Mapper.SetmealMapper;
import com.itheima.reggie.Service.CategoryService;
import com.itheima.reggie.Service.DishService;
import com.itheima.reggie.Service.SetmealService;
import com.itheima.reggie.common.CustomerException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    // 删除一个分类 需要检查该分类下是否有菜品 如果有菜品则删除失败

    /**
     *
     * @param id 菜品或套菜分类的ID
     */
    public void remove(Long id) {
        // 根据ID去菜品表和套餐表中category_id字段查询 如果count不为0，说明该分类关联过菜品或套餐
        // 查询菜品
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int dishCount = dishService.count(dishLambdaQueryWrapper);
        if(dishCount > 0) {
            // 抛出异常
            throw new CustomerException("该分类已经关联菜品");
        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int setmealCount = setmealService.count(setmealLambdaQueryWrapper);
        if(setmealCount > 0) {
            // 抛出异常
            throw new CustomerException("该分类已经关联菜品");
        }

        // 如果没有关联菜品，则直接删除
        super.removeById(id);
    }
}
