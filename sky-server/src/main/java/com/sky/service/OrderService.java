package com.sky.service;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    PageResult pageQuery(int page, int pageSize, Integer status);

    OrderVO getOrderDetail(Long id);

    void cancel(Orders orders);

    void repetition(Long id);

    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    void rejection(Orders orders);

    void confirm(Orders orders);

    void delivery(Long id);

    void complete(Long id);

    OrderStatisticsVO statistics();
}
