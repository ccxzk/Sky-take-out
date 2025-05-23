package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Api(tags = "数据统计接口")
@Slf4j
public class ReportController {
    @Resource
    private ReportService reportService;

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate begin,
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate end) {
        log.info("查询{}到{}的营业额数据", begin, end);
        TurnoverReportVO vo = reportService.getTurnoverStatistics(begin, end);
        return Result.success(vo);
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")
    public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate begin,
                                               @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate end) {
        log.info("查询{}到{}的用户数据", begin, end);
        UserReportVO vo = reportService.getUserStatistics(begin, end);
        return Result.success(vo);
    }

    /**
     * 订单数据统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计")
    public Result<OrderReportVO> ordersStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate begin,
                                                 @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate end) {
        log.info("查询{}到{}的订单数据", begin, end);
        OrderReportVO vo = reportService.getOrderStatistics(begin, end);
        return Result.success(vo);
    }

    /**
     * 销量top10统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/top10")
    @ApiOperation("查询销量排名top10")
    public Result<SalesTop10ReportVO> getTop10(@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate begin,
                                               @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate end) {
        log.info("查询{}到{}的top10", begin, end);
        SalesTop10ReportVO vo = reportService.getTop10(begin, end);
        return Result.success(vo);
    }


    @GetMapping("/export")
    @ApiOperation("导出数据")
    public void export(HttpServletResponse response) {
        log.info("导出数据");

        reportService.exportBusinessData(response);

    }
}
