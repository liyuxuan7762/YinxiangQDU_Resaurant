package com.itheima.reggie.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Mapper.OrderDetailMapper;
import com.itheima.reggie.Service.OrderDetailService;
import com.itheima.reggie.entity.OrderDetail;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 李宇轩
 */
@Service("orderDetailServiceImpl")
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
    @Override
    public void insertBatch(List<OrderDetail> orderDetailList) {
        super.saveBatch(orderDetailList);
    }

    @Override
    public List<OrderDetail> getOrderDetailListByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId, orderId);
        return super.list(queryWrapper);
    }
}
