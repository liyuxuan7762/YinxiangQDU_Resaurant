package com.itheima.reggie.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Mapper.ShoppingCartMapper;
import com.itheima.reggie.Service.ShoppingCartService;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.entity.ShoppingCart;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("shoppingCartService")
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    @Override
    public ShoppingCart add(ShoppingCart shoppingCart) {
        // 1.获取用户id
        Long userId = BaseContext.get();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, userId);
        // 2.判断菜品或套餐是否已经存在
        if (shoppingCart.getDishId() != null) {
            lambdaQueryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart one = super.getOne(lambdaQueryWrapper);
        // 3.如果存在就直接加1 如果不存在就添加到购物车，默认数量为1
        if (one != null) {
            // 菜品或套餐已经存在 update
            one.setNumber(one.getNumber() + 1);
            super.updateById(one);
        } else {
            // 不存在 insert
            shoppingCart.setNumber(1);
            shoppingCart.setUserId(userId);
            super.save(shoppingCart);
            one = shoppingCart; // 这一步方便返回的时候 直接返回one 就可以了
        }
        return one;
    }

    @Override
    public List<ShoppingCart> getShoppingCartByUserId(Long userId) {
        return super.list(new LambdaQueryWrapper<ShoppingCart>().eq(ShoppingCart::getUserId, userId));
    }

    @Override
    public void sub(ShoppingCart shoppingCart) {
        // 1.根据用户id和菜品或套餐id定位到记录
        Long userId = BaseContext.get();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        if(shoppingCart.getDishId() != null) {
            // 用户删除菜品
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            // 用户删除套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        // 2.查询到记录后判断记录的num
        ShoppingCart one = super.getOne(queryWrapper);
        if(one.getNumber() > 1) {
            // 更新
            one.setNumber(one.getNumber() - 1);
            super.updateById(one);
        } else {
            // 如果大于1，则直接num-- 然后执行update 否则直接删除该条目
            super.remove(queryWrapper);
        }

    }

    @Override
    public void clean() {
        super.remove(new LambdaQueryWrapper<ShoppingCart>().eq(ShoppingCart::getUserId, BaseContext.get()));
    }
}
