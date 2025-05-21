package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Slf4j
@Api(tags = "C端-地址簿接口")
public class AddressBookController {
    @Resource
    private AddressBookService addressBookService;

    /**
     * 查询登录用户所有地址
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("查询地址")
    public Result<List<AddressBook>> list(){
        AddressBook addressBook = AddressBook.builder()
                .userId(BaseContext.getCurrentId())
                .build();
        List<AddressBook> list = addressBookService.list(addressBook);
        return Result.success(list);
    }

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @PostMapping
    @ApiOperation("新增地址")
    public Result save(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId()); // 设置用户id
        addressBook.setIsDefault(0); // 设置默认地址

        addressBookService.save(addressBook);
        return Result.success();
    }

    /**
     * 修改地址
     * @param addressBook
     * @return
     */
    @PutMapping()
    @ApiOperation("修改地址")
    public Result update(@RequestBody AddressBook addressBook){
        log.info("修改地址：{}", addressBook);
        addressBookService.update(addressBook);
        return Result.success();
    }

    /**
     * 根据id查询地址 (前端回显)
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询地址")
    public Result<AddressBook> getById(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        return Result.success(addressBook);
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    @ApiOperation("设置默认地址")
    public Result setDefault(@RequestBody AddressBook addressBook){
        addressBook.setIsDefault(1); //设为默认地址

        addressBookService.setDefault(addressBook);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("删除地址")
    public Result delete(@RequestParam Long id){
        addressBookService.deleteById(id);
        return Result.success();
    }











}
