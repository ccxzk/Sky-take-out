package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 更新购物车
     * @param shoppingCart
     */
    @Update("update shopping_cart set number = #{number},amount = #{amount} where name = #{name}")
    void update(ShoppingCart shoppingCart);

    /**
     * 根据id查询购物车数据
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 新增购物车数据
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart (name,user_id,dish_id,setmeal_id,dish_flavor,number,amount,image,create_time) " +
            "values (#{name},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{image},#{createTime})")
    void add(ShoppingCart shoppingCart);

    /**
     * 删除购物车数据
     * @param userId
     */
    @Update("delete from shopping_cart where user_id = #{userId}")
    void delete(Long userId);
}
