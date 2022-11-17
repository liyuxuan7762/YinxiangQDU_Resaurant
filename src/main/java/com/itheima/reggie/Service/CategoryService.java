package com.itheima.reggie.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Orders;

import java.util.List;

public interface CategoryService extends IService<Category> {
    void remove(Long ids);
    void saveCategory(Category category);
    Page<Category> queryByPage(int page, int pageSize);
    void removeById(Long id);
    List<Category> getDishCategories(Category category);

}
