package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {
    @Resource
    private AddressBookMapper addressBookMapper;

    /**
     * 查询登录用户所有地址
     * @return
     */
    @Override
    public List<AddressBook> list(AddressBook addressBook) {
        return addressBookMapper.list(addressBook);
    }

    /**
     * 新增地址
     * @param addressBook
     */
    @Override
    public void save(AddressBook addressBook) {
        addressBookMapper.save(addressBook);
    }

    /**
     * 设置默认地址
     * @param addressBook
     */
    @Override
    public void setDefault(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @Override
    public AddressBook getById(Long id) {
        return addressBookMapper.getById(id);
    }

    /**
     * 删除地址
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        addressBookMapper.deleteById(id);
    }

    /**
     * 修改地址
     * @param addressBook
     */
    @Override
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    @Override
    public AddressBook getByDefault() {
        Long userId = BaseContext.getCurrentId();
        return addressBookMapper.getByDefault(userId);
    }


}
