package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.entity.Orders;
import com.sky.entity.Setmeal;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {
    @Resource
    private OrderMapper  orderMapper;

    @Resource
    private SetmealMapper setmealMapper;

    @Resource
    private DishMapper dishMapper;

    /**
     * 今日运营总览
     * @return
     */
    @Override
    public BusinessDataVO getBusinessData() {
        // 获取今日时间
        LocalDateTime  begin = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);

        // 封装查询条件
        Map map = new HashMap();
        map.put("begin",begin);
        map.put("end",end);
        map.put("status", Orders.COMPLETED);

        // 获取营业额
        Double turnover = orderMapper.sumByMap(map);
        turnover = turnover == null ? 0.0 : turnover;

        // 获取有效订单
        Integer validOrderCount = orderMapper.countOrderByMap(map);

        // 获取订单总数
        map.remove("status"); // 移除 status 键值对
        Integer totalOrderCount = orderMapper.countOrderByMap(map);

        //  获取新增用户数
        Integer newUsers = orderMapper.sumUserByMap(map);

        // 获取订单完成率
        double orderCompletionRate = (double) validOrderCount / totalOrderCount;

        // 获取平均客单价
        double unitPrice = turnover / validOrderCount;

        //  封装结果集VO
        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }

    /**
     * 订单总览
     * @return
     */
    @Override
    public OrderOverViewVO getOverviewOrders() {
        //获取全部订单
        List<Orders> orders = orderMapper.getByStatus(null);
        Integer allOrders = orders.size();

        int waitingOrders = 0; // 未接订单
        int deliveredOrders = 0; // 待派送订单
        int completedOrders = 0; // 已完成订单
        int cancelledOrders = 0; // 已取消订单

        for(Orders order : orders){
            if(order.getStatus() == Orders.TO_BE_CONFIRMED){waitingOrders++;}
            else if(order.getStatus() == Orders.CONFIRMED){deliveredOrders++;}
            else if(order.getStatus() == Orders.COMPLETED){completedOrders++;}
            else if(order.getStatus() == Orders.CANCELLED){cancelledOrders++;}
        }

        //封装返回结果集VO
        return OrderOverViewVO.builder()
                .allOrders(orders.size())
                .waitingOrders(waitingOrders)
                .completedOrders(completedOrders)
                .deliveredOrders(deliveredOrders)
                .cancelledOrders(cancelledOrders)
                .build();
    }

    /**
     * 套餐总览
     * @return
     */
    @Override
    public SetmealOverViewVO getOverviewSetmeal() {
        //先获取全部套餐
        List<Setmeal> setmeals = setmealMapper.list(new Setmeal());

        int sold = 0; // 已起售套餐
        int discontinued = 0; // 已停售套餐

        for (Setmeal setmeal : setmeals){
            if (setmeal.getStatus() == StatusConstant.ENABLE){
                sold++;
            } else {
                discontinued++;
            }
        }

        //封装结果集VO
        return SetmealOverViewVO.builder()
                .discontinued(discontinued)
                .sold(sold)
                .build();
    }

    /**
     * 菜品管理
     * @return
     */
    @Override
    public DishOverViewVO getOverviewDishes() {
        //先获取全部菜品
        List<Dish> list = dishMapper.list(new Dish());

        int sold = 0; // 已起售菜品
        int discontinued = 0; // 已停售菜品

        for (Dish dish : list){
            if (dish.getStatus() == 1){
                sold++;
            }else {
                discontinued++;
            }
        }
        return DishOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }
}
