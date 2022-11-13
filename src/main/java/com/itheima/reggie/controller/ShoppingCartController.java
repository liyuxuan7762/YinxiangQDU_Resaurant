package com.itheima.reggie.controller;

import com.itheima.reggie.Service.ShoppingCartService;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    // 添加菜品或套餐到购物车
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        ShoppingCart cart = shoppingCartService.add(shoppingCart);
        return R.success(cart);
    }

    // 查看当前用户购物车信息
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        List<ShoppingCart> shoppingCartList = shoppingCartService.getShoppingCartByUserId(BaseContext.get());
        return R.success(shoppingCartList);
    }

    // 删除购物车中条目
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart) {
        shoppingCartService.sub(shoppingCart);
        return R.success("删除成功");
    }

    @DeleteMapping("/clean")
    public R<String> clean() {
        // 清空当期用户的购物车
        shoppingCartService.clean();
        return R.success("清空成功");
    }
}
