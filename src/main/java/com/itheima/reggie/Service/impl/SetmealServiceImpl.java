package com.itheima.reggie.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Mapper.SetmealMapper;
import com.itheima.reggie.Service.CategoryService;
import com.itheima.reggie.Service.DishService;
import com.itheima.reggie.Service.SetmealDishService;
import com.itheima.reggie.Service.SetmealService;
import com.itheima.reggie.common.CustomerException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
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

@Service("setmealService")
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;

    @Override
    @Transactional
    @CacheEvict(value = "setmealCache", key = "#setmealDto.categoryId")
    public void addSetmeal(SetmealDto setmealDto) {
        // 需要操作两个表
        // 首先将基本信息保存在Setmeal表中
        // 然后将该套餐对应的菜品信息都保存到setmeal_dish表中

        this.save(setmealDto);

        // 因为dto中的保存每一个套餐中所有的菜品的list中的每一个dish是没有setmeal_id 的
        // 因此先遍历设置setmeal_id

        for (SetmealDish setmealDish : setmealDto.getSetmealDishes()) {
            setmealDish.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(setmealDto.getSetmealDishes());
    }

    @Override
    @Transactional
    @CacheEvict(value = "setmealCache", allEntries = true)
    public void deleteSeameal(String ids) {
        // 套餐的id
        List<String> idList = Arrays.asList(ids.split(","));
        // 1.根据套餐id查询套餐状态 在售 停售 只有停售的套餐才可以删除
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId, idList);
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus, 1);
        int count = super.count(setmealLambdaQueryWrapper);
        if (count > 0) {
            // 当前要删除的套餐中还有正在售卖的 无法删除
            throw new CustomerException("当前删除的套餐中存在正在售卖的套餐，无法删除");
        }
        // 2.删除setmeal表中相关记录
        super.removeByIds(idList);

        // 3.删除setmeal_dish表中相关记录
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
    }

    @Override
    @CacheEvict(value = "setmealCache", allEntries = true)
    public void offSaleSetmeal(String ids) {
        List<String> idsList = Arrays.asList(ids.split(","));
        UpdateWrapper<Setmeal> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", idsList);
        updateWrapper.set("status", 0);
        super.update(updateWrapper);
    }

    @Override
    @Transactional
    @CacheEvict(value = "setmealCache", allEntries = true)
    public void startSaleSetmeal(String ids) {
        List<String> idsList = Arrays.asList(ids.split(","));
        UpdateWrapper<Setmeal> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", idsList);
        updateWrapper.set("status", 1);
        super.update(updateWrapper);

        // 套餐起售，则套餐中所有产品均应该起售
        // 1.在setmeal_dish表中根据setmeal_id查找到所有的dish_id
        List<SetmealDish> setmealDisheList = setmealDishService.list(new QueryWrapper<SetmealDish>().select("DISTINCT `dish_id`").lambda().in(SetmealDish::getSetmealId, idsList));
        StringBuilder dish_ids = new StringBuilder();
        for (SetmealDish setmealDish : setmealDisheList) {
            dish_ids.append(setmealDish.getDishId()).append(",");
        }
        // 2.根据dish_id 起售所有产品
        dishService.startSaleDish(dish_ids.toString());
    }

    @Override
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId", unless = "#result == null")
    public List<SetmealDto> getSetmealByCategoryId(Setmeal setmeal) {
        // 1.根据分类id查询出套餐基本信息
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, setmeal.getCategoryId());
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus, setmeal.getStatus());
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = super.list(setmealLambdaQueryWrapper);

        // 2.然后根据套餐id去dish_setmeal表中查询出该套餐包含的所有菜品
        List<SetmealDto> setmealDtoList = new ArrayList<>();
        SetmealDto setmealDto = null;
        for (Setmeal s : setmealList) {
            setmealDto = new SetmealDto();
            BeanUtils.copyProperties(s, setmealDto);
            // 获取套餐的类别名称
            String categoryName = categoryService.getById(s.getCategoryId()).getName();
            // 获取该套餐所有的菜品
            List<SetmealDish> setmealDishList = setmealDishService.list(new LambdaQueryWrapper<SetmealDish>().eq(SetmealDish::getSetmealId, s.getId()));
            // 设置dto属性
            setmealDto.setCategoryName(categoryName);
            setmealDto.setSetmealDishes(setmealDishList);
            setmealDtoList.add(setmealDto);
        }

        return setmealDtoList;
    }

    @Override
    @CacheEvict(value = "setmealCache", allEntries = true)
    public SetmealDto getSetmealDetailById(Long id) {
        Setmeal setmeal = super.getOne(new LambdaQueryWrapper<Setmeal>().eq(Setmeal::getId, id));
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);
        // 获取套餐的类别名称
        String categoryName = categoryService.getById(setmeal.getCategoryId()).getName();
        // 获取该套餐所有的菜品
        List<SetmealDish> setmealDishList = setmealDishService.list(new LambdaQueryWrapper<SetmealDish>().eq(SetmealDish::getSetmealId, setmeal.getId()));
        // 设置dto属性
        setmealDto.setCategoryName(categoryName);
        setmealDto.setSetmealDishes(setmealDishList);
        return setmealDto;
    }

    @Override
    public Page page(int page, int pageSize, String name) {
        // 由于Page中查询的字段只有CategoryId，页面上要显示的是分类的名称，因此使用DTO
        // 1.先查出来setmeal的基本信息
        Page<Setmeal> setmealPageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.like(name != null, Setmeal::getName, name);
        super.page(setmealPageInfo, setmealLambdaQueryWrapper);

        // 创建一个dto的page对象，把基本信息都拷贝过去
        Page<SetmealDto> setmealDtoPageInfo = new Page<>();
        List<SetmealDto> setmealDtoList = new ArrayList<>();
        BeanUtils.copyProperties(setmealPageInfo, setmealDtoPageInfo, "records");

        // 遍历setmeal的Page中的records，取出每一条record，得到categoryId，然后根据ID查询categoryName
        SetmealDto setmealDto = null;
        for (Setmeal setmeal : setmealPageInfo.getRecords()) {
            setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);
            String categoryName = categoryService.getById(setmeal.getCategoryId()).getName();
            setmealDto.setCategoryName(categoryName);
            setmealDtoList.add(setmealDto);
        }

        setmealDtoPageInfo.setRecords(setmealDtoList);
        return setmealDtoPageInfo;
    }
}
