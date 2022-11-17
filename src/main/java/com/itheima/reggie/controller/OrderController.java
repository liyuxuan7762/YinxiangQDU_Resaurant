package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.Service.OrdersService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrdersService orderService;

    /**
     * 用户下单
     *
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        orderService.submitOrder(orders);
        return R.success("下单成功");
    }

    /**
     * 根据条件分页查询订单信息
     *
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number, String beginTime, String endTime) {
        Page pageInfo = orderService.page(page, pageSize, number, beginTime, endTime);
        return R.success(pageInfo);
    }


    /**
     * 修改订单状态
     *
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> changeOrdersStatus(@RequestBody Orders orders) {
        orderService.changeOrdersStatus(orders);
        return R.success("修改状态成功");
    }

    /**
     * 移动端用户查看自己的订单信息
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userOrders(int page, int pageSize) {
        Page pageInfo = orderService.userOrdersPage(page, pageSize);
        return R.success(pageInfo);
    }

    /**
     * 用户在移动端点击再来一单
     *
     * @param map
     * @return
     */
    @PostMapping("/again")
    public R<String> again(@RequestBody Map<String, String> map) {
        Long id = Long.parseLong(map.get("id"));
        orderService.again(id);
        return R.success("商品已添加到购物车");
    }
}
