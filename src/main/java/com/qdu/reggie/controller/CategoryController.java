package com.qdu.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qdu.reggie.Service.CategoryService;
import com.qdu.reggie.common.R;
import com.qdu.reggie.entity.Category;
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
        categoryService.saveCategory(category);
        return R.success("添加成功");
    }

    // 分页查询分类信息
    @GetMapping("/page")
    public R<Page> getCategories(int page, int pageSize) {
        Page<Category> pageInfo = categoryService.queryByPage(page, pageSize);
        // 返回结果
        return R.success(pageInfo);
    }

    // 根据ID删除分类
    @DeleteMapping
    public R<String> deleteCategoryById(Long ids) {
        categoryService.removeById(ids);
        return R.success("删除成功");
    }

    // 修改分类信息
    @PutMapping
    public R<String> updateCategory(@RequestBody Category category) {
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    // 查询所有菜品分类 前台传递参数 type=查询菜品分类 type=2查询套餐分类
    @GetMapping("/list")
    public R<List<Category>> getDishCategories(Category category) {
        List<Category> list = categoryService.getDishCategories(category);
        return R.success(list);
    }

}
