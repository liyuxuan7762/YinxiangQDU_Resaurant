package com.itheima.reggie.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Orders;

public interface OrdersService extends IService<Orders> {
    void submitOrder(Orders orders);
}
