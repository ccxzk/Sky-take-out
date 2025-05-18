package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishFlavorMapper {
    @Insert("insert into dish_flavor(dish_id,name,value) values (#{dishId},#{name},#{value})")
    void addDishFlavor(DishFlavor dishFlavor);
}
