package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class MyTask {
    @Resource
    private OrderMapper orderMapper;

    /**
     * 处理超时订单
     */
    @Scheduled(cron = "0 * * * * ? ")
    public void orderTask(){
        log.info("处理超时订单 {}", LocalDateTime.now());
        // 查询所有待支付订单
        List<Orders> ordersList = orderMapper.getByStatus(Orders.PENDING_PAYMENT);

        // 判断是否存在待支付订单
        if(ordersList != null && !ordersList.isEmpty()){
            for(Orders orders : ordersList){

                // 判断该订单是否超时
                if (Duration.between(orders.getOrderTime(), LocalDateTime.now()).toMinutes() > 15) {
                    orders.setStatus(Orders.CANCELLED);
                    orders.setCancelReason("订单超时，自动取消");
                    orders.setCancelTime(LocalDateTime.now());
                    orderMapper.update(orders);
                }
            }
        }
    }

    /**
     * 处理已派送订单
     */
    @Scheduled(cron = "0 0 1 * * ? ")
    public void orderTask2(){
        log.info("处理已派送订单 {}", LocalDateTime.now());
        // 查询所有派送中的订单
        List<Orders> ordersList = orderMapper.getByStatus(Orders.DELIVERY_IN_PROGRESS);

        // 判断是否存在派送中订单
        if(ordersList != null && !ordersList.isEmpty()){
            for(Orders orders : ordersList){

                if (Duration.between(orders.getOrderTime(), LocalDateTime.now()).toMinutes() > 60) {
                    orders.setStatus(Orders.COMPLETED);
                    orders.setDeliveryTime(orders.getOrderTime().plusHours(1));
                    orderMapper.update(orders);
                }
            }
        }
    }
}
