package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {
    /**
     * 新增菜品
     * @param dish
     */
    @AutoFill(OperationType.INSERT)
    @Options(useGeneratedKeys = true,keyColumn = "id",keyProperty = "id")
    @Insert("insert into dish(name,category_id,price,image,description,status,create_time,update_time,create_user,update_user) values " +
            "(#{name},#{categoryId},#{price},#{image},#{description},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void addDish(Dish dish);

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据菜品名称查询菜品
     * @param name
     * @return
     */
    @Select("select COUNT(*) from dish where name = #{name}")
    int getByName(String name);

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    /**
     * 批量删除菜品
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 修改菜品
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);
}
