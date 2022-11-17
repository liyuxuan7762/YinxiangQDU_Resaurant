package com.itheima.reggie.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitOrder(Orders orders) {
        // 前端只给了地址id
        // 1.获取当前用户的id
        Long userId = BaseContext.get();
        // 2.获取当前用户的购物车数据
        List<ShoppingCart> shoppingCarts = shoppingCartService.getShoppingCartByUserId(userId);
        // 3.查询用户的数据
        User user = userService.getUserById(userId);
        // 4.查询地址信息
        AddressBook addressBookById = addressBookService.getAddressBookById(orders.getAddressBookId());
        // 封装orders剩余的数据，并将orders写入到orders表中
        // 这个id作为订单的主键id和订单号number
        long id = IdWorker.getId();
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

    @Override
    public Page page(int page, int pageSize, String number, String beginTime, String endTime) {
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(number != null, Orders::getNumber, number)
                .gt(beginTime != null, Orders::getOrderTime, beginTime)
                .lt(endTime != null, Orders::getOrderTime, endTime);
        return super.page(pageInfo, queryWrapper);
    }

    @Override
    public void changeOrdersStatus(Orders orders) {
        super.updateById(orders);
    }

    @Override
    public Page userOrdersPage(int page, int pageSize) {
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Long userId = BaseContext.get();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, userId);
        queryWrapper.orderByDesc(Orders::getOrderTime);
        return super.page(pageInfo, queryWrapper);
    }

    /**
     * 用户点击再来一单
     *
     * @param id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void again(Long id) {
        /**
         *  前端点击再来一单是直接跳转到购物车的，所以为了避免数据有问题，再跳转之前我们需要把购物车的数据给清除
         * ①通过orderId获取订单明细
         * ②把订单明细的数据的数据塞到购物车表中，不过在此之前要先把购物车表中的数据给清除(清除的是当前登录用户的购物车表中的数据)，
         * 不然就会导致再来一单的数据有问题；
         * (这样可能会影响用户体验，但是对于外卖来说，用户体验的影响不是很大，电商项目就不能这么干了)
         */
        // 1.根据orderId获取所有的dishId或setmealId
        List<OrderDetail> orderDetailList = orderDetailService.getOrderDetailListByOrderId(id);
        // 2.遍历orderDetailList 创建购物车list 保存的时候需要判断当前的菜品是否停售 如果停售则跳过
        List<ShoppingCart> shoppingCartList = new ArrayList<>();
        ShoppingCart shoppingCart = null;
        for (OrderDetail orderDetail : orderDetailList) {
            if (orderDetail.getDishId() != null) {
                // 这一项是菜品
                Dish dish = dishService.getDishById(orderDetail.getDishId());
                if (dish.getStatus() == 0) {
                    // 本次菜品停售 进行下一次循环
                    continue;
                } else {
                    // 菜品未停售
                    shoppingCart = new ShoppingCart();
                    shoppingCart.setDishId(orderDetail.getDishId());
                    shoppingCart.setDishFlavor(orderDetail.getDishFlavor());
                }
            }
            if (orderDetail.getSetmealId() != null) {
                Setmeal setmeal = setmealService.getSetmealById(orderDetail.getSetmealId());
                if (setmeal.getStatus() == 0) {
                    // 本次菜品停售 进行下一次循环
                    continue;
                } else {
                    // 菜品未停售
                    shoppingCart = new ShoppingCart();
                    shoppingCart.setSetmealId(orderDetail.getSetmealId());
                }
            }
            shoppingCart.setName(orderDetail.getName());
            shoppingCart.setImage(orderDetail.getImage());
            shoppingCart.setUserId(BaseContext.get());
            shoppingCart.setNumber(orderDetail.getNumber());
            shoppingCart.setAmount(orderDetail.getAmount());

            shoppingCartList.add(shoppingCart);
        }
        // 3.清空购物车
        shoppingCartService.clean();
        // 4.将新的菜品添加到购物车
        shoppingCartService.addBatch(shoppingCartList);

    }
}
