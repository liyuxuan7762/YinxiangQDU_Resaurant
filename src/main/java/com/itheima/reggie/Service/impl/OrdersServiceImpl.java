package com.itheima.reggie.Service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Mapper.OrdersMapper;
import com.itheima.reggie.Service.*;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service("orderService")
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService  {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;

    @Override
    @Transactional
    public void submitOrder(Orders orders) { // 前端只给了地址id
        // 1.获取当前用户的id
        Long userId = BaseContext.get();
        // 2.获取当前用户的购物车数据
        List<ShoppingCart> shoppingCarts = shoppingCartService.getShoppingCartByUserId(userId);
        // 3.查询用户的数据
        User user = userService.getUserById(userId);
        // 4.查询地址信息
        AddressBook addressBookById = addressBookService.getAddressBookById(orders.getAddressBookId());
        // 封装orders剩余的数据，并将orders写入到orders表中
        long id = IdWorker.getId(); // 这个id作为订单的主键id和订单号number
        orders.setNumber(String.valueOf(id));
        orders.setId(id);
        orders.setUserId(userId);
        orders.setUserName(user.getName());
        orders.setConsignee(addressBookById.getConsignee());
        orders.setPhone(addressBookById.getPhone());
        orders.setStatus(2);
        orders.setAddress((addressBookById.getProvinceName() == null ? "" : addressBookById.getProvinceName())
                + (addressBookById.getCityName() == null ? "" : addressBookById.getCityName())
                + (addressBookById.getDistrictName() == null ? "" : addressBookById.getDistrictName())
                + (addressBookById.getDetail() == null ? "" : addressBookById.getDetail()));

        // 对于金额这种重要数据 不能完全相信前端传来的数据，因此在后端需要重新验证一遍总金额
        // 这个循环既可以计算总金额，也可以封装订单详情对象
        AtomicInteger amount = new AtomicInteger(0); // 保证在多线程环境下数据的安全
        OrderDetail orderDetail = null;
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart shoppingCart : shoppingCarts) {
            orderDetail = new OrderDetail();
            orderDetail.setAmount(shoppingCart.getAmount());
            orderDetail.setName(shoppingCart.getName());
            orderDetail.setImage(shoppingCart.getImage());
            orderDetail.setNumber(shoppingCart.getNumber());
            orderDetail.setOrderId(orders.getId());
            orderDetail.setDishId(shoppingCart.getDishId());
            orderDetail.setDishFlavor(shoppingCart.getDishFlavor());
            orderDetail.setSetmealId(shoppingCart.getSetmealId());
            orderDetailList.add(orderDetail);
            // 计算总金额
            amount.addAndGet(shoppingCart.getAmount().multiply(new BigDecimal(shoppingCart.getNumber())).intValue());
        }

        // 向order_detail表写入数据
        orderDetailService.insertBatch(orderDetailList);

        // 向order表写数据
        orders.setAmount(new BigDecimal(amount.get()));
        super.save(orders);

        // 清空购物车
        shoppingCartService.clean();
    }
}
