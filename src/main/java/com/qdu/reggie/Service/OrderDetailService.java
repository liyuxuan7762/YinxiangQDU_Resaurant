package com.qdu.reggie.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qdu.reggie.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService extends IService<OrderDetail> {
    void insertBatch(List<OrderDetail> orderDetailList);

    List<OrderDetail> getOrderDetailListByOrderId(Long orderId);

    Long getSaleNumByDishId(Long id, Integer status);

}
