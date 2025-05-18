package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 新增口味数据
     * @param dishFlavors
     */
    void addBatchDishFlavor(List<DishFlavor> dishFlavors);

    /**
     * 根据菜品id批量删除口味数据
     * @param dishIds
     */
    void deleteBatchByDishId(List<Long> dishIds);

    /**
     * 根据菜品id删除口味数据
     * @param dishId
     */
    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void deleteByDishId(Long dishId);

    /**
     * 根据菜品id查询口味数据
     * @param dishId
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> getByDishId(Long dishId);

}
