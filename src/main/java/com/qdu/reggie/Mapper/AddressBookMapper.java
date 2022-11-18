package com.qdu.reggie.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qdu.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
