package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.Service.CategoryService;
import com.itheima.reggie.Service.SetmealService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;

    // 添加套餐
    @PostMapping
    public R<String> addSeameal(@RequestBody SetmealDto setmealDto) {
        setmealService.addSetmeal(setmealDto);
        return R.success("添加成功");
    }

    // 根据条件分页查询套餐
    @GetMapping("/page")
    public R<Page> getSetmeals(int page, int pageSize, String name) {
        // 由于Page中查询的字段只有CategoryId，页面上要显示的是分类的名称，因此使用DTO
        // 1.先查出来setmeal的基本信息
        Page<Setmeal> setmealPageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.like(name != null, Setmeal::getName, name);
        setmealService.page(setmealPageInfo, setmealLambdaQueryWrapper);

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

        return R.success(setmealDtoPageInfo);
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

}
