package com.itheima.reggie.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Mapper.OrderDetailMapper;
import com.itheima.reggie.Service.OrderDetailService;
import com.itheima.reggie.entity.OrderDetail;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("orderDetailServiceImpl")
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
    @Override
    public void insertBatch(List<OrderDetail> orderDetailList) {
        super.saveBatch(orderDetailList);
    }
}
