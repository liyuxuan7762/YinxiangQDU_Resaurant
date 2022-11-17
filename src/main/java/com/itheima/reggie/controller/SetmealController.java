package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.Service.CategoryService;
import com.itheima.reggie.Service.SetmealService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    // 添加套餐
    @PostMapping
    public R<String> addSeameal(@RequestBody SetmealDto setmealDto) {
        setmealService.addSetmeal(setmealDto);
        return R.success("添加成功");
    }

    // 根据条件分页查询套餐
    @GetMapping("/page")
    public R<Page> getSetmeals(int page, int pageSize, String name) {
        Page pageInfo = setmealService.page(page, pageSize, name);
        return R.success(pageInfo);
    }

    // 删除套餐信息
    @DeleteMapping
    public R<String> deleteSetmeal(String ids) {
        setmealService.deleteSeameal(ids);
        return R.success("删除成功");
    }

    // 停售套餐
    @PostMapping("/status/0")
    public R<String> offSaleSetmeal(String ids) {
        setmealService.offSaleSetmeal(ids);
        return R.success("停售成功");
    }

    // 起售套餐
    @PostMapping("/status/1")
    public R<String> startSaleSetmeal(String ids) {
        setmealService.startSaleSetmeal(ids);
        return R.success("起售成功");
    }

    // 前台根据套餐id查询套餐
    @GetMapping("/list")
    public R<List<SetmealDto>> getSetmealById(Setmeal setmeal) {
        List<SetmealDto> list = setmealService.getSetmealByCategoryId(setmeal);
        return R.success(list);
    }

    // 修改页面数据回显
    @GetMapping("/{id}")
    public R<SetmealDto> edit(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.edit(id);
        return R.success(setmealDto);
    }

    @PutMapping
    public R<String> updateSetmeal(@RequestBody SetmealDto setmealDto) {
        setmealService.updateSetmeal(setmealDto);
        return R.success("保存成功");
    }

    /**
     * 主页用户点击套餐 显示套餐中包含的菜品
     * @param id
     */
    @GetMapping("/dish/{id}")
    public R<List<DishDto>>setmealDetail(@PathVariable(name = "id") Long id) {
        List<DishDto> dishDtoList = setmealService.setmealDetail(id);
        return R.success(dishDtoList);
    }
}
