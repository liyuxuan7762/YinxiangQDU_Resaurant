package com.qdu.reggie.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qdu.reggie.Mapper.OrderDetailMapper;
import com.qdu.reggie.Service.OrderDetailService;
import com.qdu.reggie.entity.OrderDetail;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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


    /**
     * @param id     菜品或套餐ID
     * @param status 0表示查询菜品销量，1表示查询套餐销量
     * @return 销量
     */
    @Override
    public Long getSaleNumByDishId(Long id, Integer status) {
        QueryWrapper<OrderDetail> queryWrapper = new QueryWrapper<>();
        if (status == 0) {
            queryWrapper.eq("dish_id", id);
        } else if (status == 1) {
            queryWrapper.eq("setmeal_id", id);
        }
        queryWrapper.select("count(id) as saleNum");
        Map<String, Object> map = super.getMap(queryWrapper);
        return (Long) map.get("saleNum");
    }
}
