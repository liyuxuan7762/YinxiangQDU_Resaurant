package com.itheima.reggie.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void addSetmeal(SetmealDto setmealDto);
    void deleteSeameal(String ids);
    void offSaleSetmeal(String ids);
    void startSaleSetmeal(String ids);
    List<SetmealDto> getSetmealByCategoryId(Setmeal setmeal);
    SetmealDto getSetmealDetailById(Long id);
    Page page(int page, int pageSize, String name);
    SetmealDto edit(Long id);
    void updateSetmeal(SetmealDto setmealDto);
    Setmeal getSetmealById(Long id);
    List<DishDto> setmealDetail(Long id);
}
