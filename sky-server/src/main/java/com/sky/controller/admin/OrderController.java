package com.sky.controller.admin;


import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/admin/order")
@Api(tags = "订单管理接口")
@Slf4j
public class OrderController {
    @Resource
    private OrderService orderService;

    /**
     * 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("订单搜索：{}", ordersPageQueryDTO);

        PageResult  pageResult = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 订单详情
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    @ApiOperation("订单详情")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id) {
        log.info("订单详情：{}", id);

        OrderVO orderVO = orderService.getOrderDetail(id);
        return Result.success(orderVO);
    }

    /**
     * 取消订单
     * @param orders
     * @return
     */
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancel(@RequestBody Orders orders) {
        log.info("取消订单：{}", orders);

        orderService.cancel(orders);
        return Result.success();
    }

    /**
     * 拒绝订单
     * @param orders
     * @return
     */
    @PutMapping("/rejection")
    @ApiOperation("拒绝订单")
    public Result rejection(@RequestBody Orders orders) {
        log.info("拒绝订单：{}", orders);

        orderService.rejection(orders);
        return Result.success();
    }

    /**
     * 确认订单
     * @param orders
     * @return
     */
    @PutMapping("/confirm")
    @ApiOperation("确认订单")
    public Result confirm(@RequestBody Orders orders) {
        log.info("确认订单：{}", orders);

        orderService.confirm(orders);
        return Result.success();
    }

    /**
     * 订单出餐
     * @param id
     * @return
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("订单出餐")
    public Result delivery(@PathVariable Long id) {
        log.info("订单出餐：{}", id);

        orderService.delivery(id);
        return Result.success();
    }

    /**
     * 订单完成
     * @param id
     * @return
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("订单完成")
    public Result complete(@PathVariable Long id) {
        log.info("订单完成：{}", id);

        orderService.complete(id);
        return Result.success();
    }

    /**
     * 统计订单信息
     * @return
     */
    @GetMapping("/statistics")
    @ApiOperation("统计订单信息")
    public Result<OrderStatisticsVO> statistics() {
        log.info("统计订单信息");

        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

}
