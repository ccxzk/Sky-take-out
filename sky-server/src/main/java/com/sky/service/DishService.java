package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    void addDish(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    int getByName(String name);

    DishVO getById(Long id);

    void delete(List<Long> ids);

    void update(DishDTO dishDTO);

    void startOrStop(Integer status, Long id);

    List<Dish> list(Long categoryId, String name);

    List<DishVO> listWithFlavor(Dish dish);
}
