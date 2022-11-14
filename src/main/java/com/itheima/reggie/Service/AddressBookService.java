package com.itheima.reggie.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.AddressBook;

import java.util.List;

public interface AddressBookService extends IService<AddressBook> {
    void saveAddress(AddressBook addressBook);
    List<AddressBook> queryAddressBookByUserId(Long userId);

    void setDefaultAddress(Long id);

    AddressBook getAddressBookById(Long id);

    void updateAddressBook(AddressBook addressBook);

    AddressBook getDefaultAddressBook();
}
