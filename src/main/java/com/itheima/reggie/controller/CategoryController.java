package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.Service.CategoryService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    // 新增分类 type=1 菜品分类 type=2 套餐分类
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        categoryService.save(category);
        return R.success("添加成功");
    }

    // 分页查询分类信息
    @GetMapping("/page")
    public R<Page> getCategories(int page, int pageSize) {
        // 创建Page对象
        Page<Category> pageInfo = new Page<>(page, pageSize);
        // 创建条件，先显示菜品分类 按照type升序排列 type值相同按照sort升序排列
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getType);
        queryWrapper.orderByAsc(Category::getSort);
        // 执行查询
        categoryService.page(pageInfo, queryWrapper);
        // 返回结果
        return R.success(pageInfo);
    }

    // 根据ID删除分类
    @DeleteMapping
    public R<String> deleteCategoryById(Long ids) {
        categoryService.remove(ids);

        return R.success("删除成功");
    }

    // 修改分类信息
    @PutMapping
    public R<String> updateCategory(@RequestBody Category category) {
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    // 查询所有菜品分类
    @GetMapping("/list")
    public R<List<Category>> getDishCategories(Category category) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType()).orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }

}
