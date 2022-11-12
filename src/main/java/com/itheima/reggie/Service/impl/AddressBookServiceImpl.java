package com.itheima.reggie.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Mapper.AddressBookMapper;
import com.itheima.reggie.Service.AddressBookService;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.entity.AddressBook;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("addressBookService")
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

    @Override
    public void saveAddress(AddressBook addressBook) {
        super.save(addressBook);
    }

    @Override
    public List<AddressBook> queryAddressBookByUserId(Long userId) {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, userId);
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);
        return super.list(queryWrapper);
    }

    @Override
    public void setDefaultAddress(Long id) {
        // 设置默认的地址 一个用户只能有一个默认地址
        // 1.首先根据用户id将该用户所有的地址的默认值都设置为0
        // 这里不是controller 不能从session中获取用户id  所以从ThreadLocal中获取
        Long user_id = BaseContext.get();
        UpdateWrapper<AddressBook> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("is_default", 0);
        updateWrapper.eq("user_id", user_id);
        super.update(updateWrapper);

        // 2.然后根据地址id将对应的地址默认值改为1

        UpdateWrapper<AddressBook> setdefault = new UpdateWrapper<>();
        setdefault.set("is_default", 1);
        setdefault.eq("id", id);
        super.update(setdefault);
    }

    @Override
    public AddressBook getAddressBookById(Long id) {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getId, id);
        return super.getOne(queryWrapper);
    }

    @Override
    public void updateAddressBook(AddressBook addressBook) {
        super.updateById(addressBook);
    }
}
