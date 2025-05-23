package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/admin/workspace")
@Api(tags = "工作台相关接口")
@Slf4j
public class WorkspaceController {
    @Resource
    private WorkspaceService workspaceService;

    /**
     * 今日数据
     * @return
     */
    @GetMapping("/businessData")
    @ApiOperation("今日数据")
    public Result<BusinessDataVO> getBusinessData(){
        log.info("获取工作台数据");

        // 获取今日时间
        LocalDateTime begin = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);

        return Result.success(workspaceService.getBusinessData(begin,end));
    }

    /**
     * 订单管理
     */
    @GetMapping("/overviewOrders")
    @ApiOperation("订单管理")
    public Result<OrderOverViewVO> getOverviewOrders(){
        log.info("获取订单管理数据");

        return Result.success(workspaceService.getOverviewOrders());
    }

    /**
     * 套餐管理
     * @return
     */
    @GetMapping("/overviewSetmeals")
    @ApiOperation("套餐管理")
    public Result<SetmealOverViewVO> getOverviewSetmeal(){
        log.info("获取套餐管理数据");

        return Result.success(workspaceService.getOverviewSetmeal());
    }


    /**
     * 菜品管理
     * @return
     */
    @GetMapping("/overviewDishes")
    @ApiOperation("菜品管理")
    public Result<DishOverViewVO> getOverviewDishes(){
        log.info("获取菜品管理数据");

        return Result.success(workspaceService.getOverviewDishes());
    }




}
