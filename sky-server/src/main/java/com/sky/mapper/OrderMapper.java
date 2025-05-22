package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单数据
     * @param orders
     */
    void insertOne(Orders orders);

    /**
     * 批量插入订单详情数据
     * @param orderDetails
     */
    void insertBatch(List<OrderDetail> orderDetails);

    /**
     * 根据订单id查询订单详情
     * @param orderId
     * @return
     */
    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> getOrderDetailById(Long orderId);

    /**
     * 分页查询订单列表
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    void update(Orders orders);

    /**
     * 条件搜索订单
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 统计订单数据
     * @return
     */
    OrderStatisticsVO statistics();

    /**
     * 根据状态查询订单
     * @param status
     * @return
     */
    @Select("select * from orders where status = #{status}")
    List<Orders> getByStatus(Integer status);

    /**
     * 根据时间范围统计营业额数据
      * @param beginTime
     * @return
     */
    long countByCreateTimeBefore(LocalDateTime beginTime);

    /**
     * 根据条件统计营业额
     * @param map
     * @return
     */
    Double sumByMap(Map map);

    /**
     * 根据条件统计用户数量
     * @param map
     * @return
     */
    int sumUserByMap(Map map);

    /**
     * 根据时间范围统计订单数量
     * @param map
     * @return
     */
    Integer countOrderByMap(Map map);

    /**
     * 查询指定时间区间内的销量top10
     * @param beginTime
     * @param endTime
     * @return
     */
    List<GoodsSalesDTO> getSalesTop10(@Param("begin") LocalDateTime beginTime, @Param("end") LocalDateTime endTime);
}
