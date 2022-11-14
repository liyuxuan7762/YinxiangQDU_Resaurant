package com.itheima.reggie.controller;

import com.itheima.reggie.Service.OrdersService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrdersService orderService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        orderService.submitOrder(orders);
        return R.success("下单成功");
    }
}
