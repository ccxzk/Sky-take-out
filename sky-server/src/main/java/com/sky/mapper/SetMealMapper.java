package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SetMealMapper {



    /**
     * 根据id查询是否存在套餐
     * @param categoryId
     * @return
     */
    @Select("select count(*) from setmeal_dish where setmeal_id = #{id}")
    int getBySetMealId(Long categoryId);

    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 批量插入套餐菜品关联数据
     * @param setmealDishes
     */
    void insertBatchSetmealDish(List<SetmealDish> setmealDishes);


    /**
     * 新增套餐
     * @param setmeal
     */
    @AutoFill(OperationType.INSERT)
    @Options(useGeneratedKeys = true,keyColumn = "id", keyProperty = "id")
    @Insert("insert into setmeal (category_id, name, price, status, description, image, create_time, update_time, create_user, update_user)"
            +"values (#{categoryId}, #{name}, #{price}, #{status}, #{description}, #{image}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void insertSetmeal(Setmeal setmeal);

    /**
     * 根据套餐id查询套餐分类名称
     * @param id
     * @return
     */
    @Select("select name from category where id = #{id}")
    String getCategoryNameById(Long id);

    /**
     * 根据套餐id批量删除套餐菜品的关联信息
     * @param ids
     */
    void deleteBatchSetmealDish(List<Long> ids);

    /**
     * 根据套餐id删除套餐菜品的关联信息
     * @param id
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteSetmealDish(Long id);

    /**
     * 根据套餐id批量删除套餐
     * @param ids
     */
    void deleteBatchSetmeal(List<Long> ids);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Select("select * from setmeal where id = #{id}")
    Setmeal getSetmealById(Long id);

    /**
     * 根据id查询套餐菜品
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getSetmealDishById(Long id);

    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);
}
