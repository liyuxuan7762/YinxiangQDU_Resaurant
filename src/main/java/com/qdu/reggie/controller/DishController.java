package com.qdu.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qdu.reggie.Service.DishService;
import com.qdu.reggie.common.R;
import com.qdu.reggie.dto.DishDto;
import com.qdu.reggie.entity.Dish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @PostMapping
    public R<String> addDish(@RequestBody DishDto dishDto) {
        dishService.addDish(dishDto);
        return R.success("添加成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        // 创建Page对象
        Page pageInfo = dishService.page(page, pageSize, name);
        return R.success(pageInfo);
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
