package com.itheima.reggie.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {
    void addSetmeal(SetmealDto setmealDto);
    void deleteSeameal(String ids);
    void offSaleSetmeal(String ids);
    void startSaleSetmeal(String ids);
}
