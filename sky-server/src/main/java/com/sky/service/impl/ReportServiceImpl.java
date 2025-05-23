package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Resource
    private OrderMapper ordersMapper;

    @Resource
    private WorkspaceService workspaceService;

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //先获取时间数据
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while  (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //获取营业额数据
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);

            Double turnover = ordersMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover; //如果没有数据，默认0
            turnoverList.add(turnover);
        }

        //封装返回结果
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //先获取总用户
        List<Long> totalUserList = new ArrayList<>();
        long totalUser = ordersMapper.countByCreateTimeBefore(LocalDateTime.of(begin, LocalTime.MIN));

        //获取时间数据
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while  (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }



        //获取新增用户
        List<Integer> newUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);

            Integer newUsers = ordersMapper.sumUserByMap(map);
            newUserList.add(newUsers);

            totalUser += newUsers;
            totalUserList.add(totalUser); // 每天结束后更新总数
        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        // 先获取日期数据
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 获取每日订单总数
        List<Integer> totalOrderCountList = new ArrayList<>();
        Integer totalOrderCount = 0;
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);

            Integer count = ordersMapper.countOrderByMap(map);
            totalOrderCount += count;
            totalOrderCountList.add(count);
        }

        // 获取每日有效订单数
        List<Integer> validOrderCountList = new ArrayList<>();
        Integer validOrderCount = 0;
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", 5);

            Integer count = ordersMapper.countOrderByMap(map);
            validOrderCount += count;
            validOrderCountList.add(count);
        }

        //封装返回结果VO
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(totalOrderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .validOrderCount(validOrderCount)
                .totalOrderCount(totalOrderCount)
                .orderCompletionRate((double) validOrderCount / totalOrderCount)
                .build();
    }

    /**
     * 销量top10统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        //包装一下时间数据
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        //查询top10订单数据
        List<GoodsSalesDTO> goodsSalesDTOList = ordersMapper.getSalesTop10(beginTime, endTime);

        String nameList = StringUtils.join(goodsSalesDTOList.stream()
                .map(GoodsSalesDTO::getName)
                .collect(Collectors.toList()),",");
        String numberList = StringUtils.join(goodsSalesDTOList.stream()
                .map(GoodsSalesDTO::getNumber)
                .collect(Collectors.toList()),",");

        //封装返回结果集VO
       return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

    /**
     * 导出数据
     * @param response
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);

        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(begin,LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));

        //创建输入流
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        //往excel中输入数据
        try {
            //基于模板文件来创建一个新的Excel表
            XSSFWorkbook excel = new XSSFWorkbook(is);

            //创建sheet页
            XSSFSheet sheet = excel.getSheet("Sheet1");

            //时间
            sheet.getRow(1).getCell(1).setCellValue(begin + "至" + end);

            //概览数据
            sheet.getRow(3).getCell(2).setCellValue(businessData.getTurnover()); // 营业额
            sheet.getRow(3).getCell(4).setCellValue(businessData.getOrderCompletionRate()); // 订单完成率
            sheet.getRow(3).getCell(6).setCellValue(businessData.getNewUsers()); // 新增用户数
            sheet.getRow(4).getCell(2).setCellValue(businessData.getValidOrderCount()); // 有效订单数
            sheet.getRow(4).getCell(4).setCellValue(businessData.getUnitPrice()); // 平均客单价


            //明细数据
            XSSFRow row = sheet.getRow(7);
            for (int i = 0; i < 30; i++){
                LocalDate date = begin.plusDays(i);
                businessData = workspaceService.getBusinessData(LocalDateTime.of(date,LocalTime.MIN),LocalDateTime.of(date,LocalTime.MAX));
                row = sheet.getRow(7 + i);

                row.getCell(1).setCellValue(String.valueOf(date)); // 日期
                row.getCell(2).setCellValue(businessData.getTurnover()); // 营业额
                row.getCell(3).setCellValue(businessData.getValidOrderCount()); // 有效订单数
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate()); // 订单完成率
                row.getCell(5).setCellValue(businessData.getUnitPrice()); // 平均客单价
                row.getCell(6).setCellValue(businessData.getNewUsers()); // 新增用户数
            }

            //  将文件输出到浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            //关闭资源
            out.flush();
            out.close();
            excel.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
