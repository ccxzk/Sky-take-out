package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    List<AddressBook> list(AddressBook addressbook);

    void save(AddressBook addressBook);

    void setDefault(AddressBook addressBook);

    AddressBook getById(Long id);

    void deleteById(Long id);

    void update(AddressBook addressBook);
}
