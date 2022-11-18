package com.qdu.reggie.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qdu.reggie.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService extends IService<ShoppingCart> {
    ShoppingCart add(ShoppingCart shoppingCart);
    List<ShoppingCart> getShoppingCartByUserId(Long userId);
    void sub(ShoppingCart shoppingCart);
    void clean();
    void addBatch(List<ShoppingCart> shoppingCartList);
}
