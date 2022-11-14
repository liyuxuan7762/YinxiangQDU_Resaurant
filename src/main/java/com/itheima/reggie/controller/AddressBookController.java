package com.itheima.reggie.controller;

import com.itheima.reggie.Service.AddressBookService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController("/addressBook")
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    @PostMapping
    public R<AddressBook> addAddress(@RequestBody AddressBook addressBook, HttpSession session) {
        // 设置UserId 实现用户和地址的绑定
        Long userId = (Long) session.getAttribute("user");
        addressBook.setUserId(userId);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    @GetMapping("/list")
    public R<List<AddressBook>> getAddressBookByUserId(HttpSession session) {
        // 1.从session中获取用户id
        Long userId = (Long) session.getAttribute("user");
        // 2.根据用户id查询所有的地址
        List<AddressBook> addressBookList = addressBookService.queryAddressBookByUserId(userId);
        // 3.返回地址
        return R.success(addressBookList);
    }

    @PutMapping("/default")
    public R<String> setDefaultAddress(@RequestBody AddressBook addressBook) {
        addressBookService.setDefaultAddress(addressBook.getId());
        return R.success("设置成功");
    }

    @GetMapping("/{id}")
    public R<AddressBook> getAddressBookById(@PathVariable (name = "id") Long id) {
        AddressBook addressBook = addressBookService.getAddressBookById(id);
        return R.success(addressBook);
    }

    @PutMapping
    public R<String> updateAddressBook(@RequestBody AddressBook addressBook) {
        addressBookService.updateAddressBook(addressBook);
        return R.success("修改成功");
    }

    // 查询用户默认地址 用于在用户下单的时候去判断用户是否有默认地址，如果没有则跳转到添加地址的页面
    @GetMapping("/default")
    public R<AddressBook> getDefaultAddress() {
        AddressBook defaultAddressBook = addressBookService.getDefaultAddressBook();
        if(defaultAddressBook == null) {
            return R.error("没有找到默认地址");
        } else {
            return R.success(defaultAddressBook);
        }
    }
}
