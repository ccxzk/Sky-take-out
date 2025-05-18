package com.sky.service.impl;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Resource
    private DishMapper dishMapper;

    @Resource
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品
     * @param dishDTO
     */
    @Override
    public void addDish(DishDTO dishDTO) {
        // 添加菜品
        Dish dish = Dish.builder()
                .status(StatusConstant.ENABLE) // 默认开启
                .build();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.addDish(dish);

        //  添加菜品口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && !flavors.isEmpty()) {
            for (DishFlavor dishFlavor : flavors) {
                dishFlavor.setDishId(dish.getId());
                dishFlavorMapper.addDishFlavor(dishFlavor);
            }
        }
    }
}
