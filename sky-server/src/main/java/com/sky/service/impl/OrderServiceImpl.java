package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    private OrderMapper orderMapper;

    @Resource
    private ShoppingCartMapper shoppingCartMapper;

    @Resource
    private AddressBookMapper addressBookMapper;

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //异常情况的处理（收货地址为空、超出配送范围、购物车为空）
        AddressBook addressBook = addressBookMapper.getByDefault(BaseContext.getCurrentId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);

        //查询当前用户的购物车数据
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if (shoppingCartList == null || shoppingCartList.isEmpty()) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //将订单信息封装到订单对象中
        Orders orders = Orders.builder()
                .userId(BaseContext.getCurrentId()) // 设置用户id
                .orderTime(LocalDateTime.now()) // 设置下单时间
                .build();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);

        //拷贝详细信息：地址、菜品、套餐信息
        orders.setAddress(addressBook.getDetail()); // 收货地址
        orders.setPhone(addressBook.getPhone()); // 收货人电话
        orders.setConsignee(addressBook.getConsignee()); // 收货人姓名
        orders.setNumber(String.valueOf(System.currentTimeMillis())); // 订单号
        orders.setStatus(Orders.PENDING_PAYMENT); //订单状态
        orders.setPayStatus(Orders.UN_PAID); // 设置支付状态（默认未支付）

        orderMapper.insertOne(orders);


        //订单的详细信息
        List<OrderDetail> orderDetails = new ArrayList<>();
        for(ShoppingCart cart : shoppingCartList){
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetails.add(orderDetail);
        }

        //批量插入订单详细信息
        orderMapper.insertBatch(orderDetails);

        //封装返回结果VO
        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
    }

    /**
     * 历史订单查询
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PageResult pageQuery(int page, int pageSize, Integer status) {
        //先让pageHelper动态拦截
        PageHelper.startPage(page, pageSize);

        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);

        //分页查询
        Page<Orders> pages = orderMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> orderDetailList = new ArrayList<>();
        for(Orders orders : pages){
            //根据订单用户信息查询订单详情
            List<OrderDetail> orderDetails = orderMapper.getOrderDetailById(orders.getId());

            //将订单详情封装到VO中
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);
            orderVO.setOrderDetailList(orderDetails);

            //加入VO队列
            orderDetailList.add(orderVO);
        }

        //封装返回结果集
        PageResult pageResult = new PageResult();
        pageResult.setTotal(pages.getTotal());
        pageResult.setRecords(orderDetailList);

        return pageResult;
    }

    /**
     * 订单详情
     * @param id
     * @return
     */
    @Override
    public OrderVO getOrderDetail(Long id) {
        Orders orders = orderMapper.getById(id);

        //根据id查询订单详情
        List<OrderDetail> orderDetailList = orderMapper.getOrderDetailById(id);

        //将订单详情封装到VO中
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    /**
     * 取消订单
     * @param orders
     */
    @Override
    public void cancel(Orders orders) {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(orders.getId());

        // 校验订单是否存在
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 更新订单状态、取消时间
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelTime(LocalDateTime.now());

        orderMapper.update(orders);
    }

    /**
     * 再来一单
     * @param id
     */
    @Override
    public void repetition(Long id) {
        // 获取当前用户id
        Long userId = BaseContext.getCurrentId();

        // 根据订单id查询订单详情
        List<OrderDetail> orderDetailList = orderMapper.getOrderDetailById(id);

        // 将订单详情对象转换为购物车对象
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x -> {
            ShoppingCart shoppingCart = new ShoppingCart();

            // 将原订单详情里面的菜品信息重新复制到购物车对象中
            BeanUtils.copyProperties(x, shoppingCart, "id");
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());

        //批量插入购物车
        shoppingCartMapper.addBatch(shoppingCartList);
    }

    /**
     * 条件搜索订单
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        // 获取用户id
        Long userId = ordersPageQueryDTO.getUserId();

        // 先用PageHelper动态拦截，设置分页参数
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        // 分页查询
        Page<Orders> page = orderMapper.conditionSearch(ordersPageQueryDTO);

        // 根据用户id查询订单详情
         List<OrderVO> orderVOList = page.getResult().stream().map(x -> {
             List<OrderDetail> orderDetailList = orderMapper.getOrderDetailById(x.getId());
             OrderVO orderVO = new OrderVO();
             BeanUtils.copyProperties(x, orderVO);
             orderVO.setOrderDetailList(orderDetailList);
             return orderVO;
         }).collect(Collectors.toList());

         //封装结果集
         PageResult pageResult = new PageResult();
         pageResult.setTotal(page.getTotal());
         pageResult.setRecords(orderVOList);


        return pageResult;
    }

    /**
     * 订单拒绝
     * @param orders
     */
    @Override
    public void rejection(Orders orders) {
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelTime(LocalDateTime.now());
        orders.setCancelReason("商家取消");
        orderMapper.update(orders);
    }

    /**
     * 订单确认
     * @param orders
     */
    @Override
    public void confirm(Orders orders) {
        orders.setStatus(Orders.CONFIRMED);
        orderMapper.update(orders);
    }

    /**
     * 订单派送
     * @param id
     */
    @Override
    public void delivery(Long id) {
        // 根据id查询订单
        Orders orders = orderMapper.getById(id);

        // 校验订单是否存在，并且状态为3
        if (orders == null || !orders.getStatus().equals(Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        //设置派送状态，并更新订单信息
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(orders);
    }

    /**
     * 订单完成
     * @param id
     */
    @Override
    public void complete(Long id) {
        // 根据id查询订单
        Orders orders = orderMapper.getById(id);

        // 校验订单是否存在，并且状态为4
        if (orders == null || !orders.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }


        //设置状态、派送时间、并更新订单信息
        orders.setStatus(Orders.COMPLETED);
        orders.setDeliveryTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 统计订单信息
     * @return
     */
    @Override
    public OrderStatisticsVO statistics() {
        return orderMapper.statistics();
    }


}
