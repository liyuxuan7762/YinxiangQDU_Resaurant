package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.Service.CategoryService;
import com.itheima.reggie.Service.DishService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> addDish(@RequestBody DishDto dishDto) {
        dishService.addDish(dishDto);
        return R.success("添加成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        // 创建Page对象
        Page<Dish> dishPageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPageInfo = new Page<>(page, pageSize);
        List<DishDto> records = new ArrayList<>();
        // 创建条件
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.like(name != null, Dish::getName, name);
        dishLambdaQueryWrapper.orderByAsc(Dish::getSort);
        // 执行查询
        dishService.page(dishPageInfo);

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
        return R.success(dishDtoPageInfo);
    }

    // 负责修改页面的回显
    @GetMapping("/{id}")
    public R<DishDto> edit(@PathVariable(name = "id") Long id) {
        DishDto dishDto = dishService.edit(id);
        return R.success(dishDto);
    }

    // 修改菜品信息
    @PutMapping
    public R<String> updateDish(@RequestBody DishDto dishDto) {
        dishService.updateDish(dishDto);
        return R.success("修改完成");
    }

    // 停售或批量停售菜品
    @PostMapping("/status/0")
    public R<String> offSaleDish(String ids) {
        // 将菜品的status属性设置为0即可
        dishService.offSaleDish(ids);
        return R.success("停售成功");
    }

    // 起售或批量起售
    @PostMapping("/status/1")
    public R<String> startSaleDish(String ids) {
        dishService.startSaleDish(ids);
        return R.success("起售成功");
    }

    // 删除和批量删除菜品
    @DeleteMapping
    public R<String> deleteDish(String ids) {
        dishService.deleteDish(ids);
        return R.success("删除成功");
    }

    // 根据菜品分类查询到对应分类下面的所有菜品
    // 在添加套餐中，根据不同的菜系 选择菜品添加时调用该方法
    // 当时菜品的类型已经查出来了，菜品类型id直接从前端传过来，然后后台根据id查出菜品即可
    // 现在移动端除了需要菜品的基本信息外，还需要菜品的口味信息，因此需要扩展这个方法
    // 返回dishDto 来保存菜品信息
    @GetMapping("/list")
    public R<List<DishDto>> getDishesByCategoryId(Dish dish) {
        List<DishDto> dishList = dishService.getDishesByCategoryId(dish);
        return R.success(dishList);
    }


}
