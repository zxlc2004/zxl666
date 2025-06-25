package org.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.springboot.common.Result;
import org.example.springboot.entity.Order;
import org.example.springboot.mapper.OrderMapper;
import org.example.springboot.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "订单管理接口")
@RestController
@RequestMapping("/order")
public class OrderController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderMapper orderMapper;

    @Operation(summary = "创建订单")
    @PostMapping
    public Result<?> createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }

    @Operation(summary = "更新订单状态")
    @PutMapping("/{id}/status")
    public Result<?> updateOrderStatus(@PathVariable Long id, @RequestParam Integer status) {
        return orderService.updateOrderStatus(id, status);
    }


    @Operation(summary = "更新订单状态")
    @PutMapping("/{id}/pay")
    public Result<?> pay(@PathVariable Long id) {
        return orderService.payOrder(id);
    }

    @Operation(summary = "删除订单")
    @DeleteMapping("/{id}")
    public Result<?> deleteOrder(@PathVariable Long id) {
        return orderService.deleteOrder(id);
    }

    @Operation(summary = "根据ID获取订单详情")
    @GetMapping("/{id}")
    public Result<?> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @Operation(summary = "根据用户ID获取订单列表")
    @GetMapping("/user/{userId}")
    public Result<?> getOrdersByUserId(@PathVariable Long userId) {
        return orderService.getOrdersByUserId(userId);
    }

    @Operation(summary = "分页查询订单列表")
    @GetMapping("/page")
    public Result<?> getOrdersByPage(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String status,

            @RequestParam(required = false) Long merchantId,
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer size) {
        return orderService.getOrdersByPage(userId, id,status,merchantId, currentPage, size);
    }

    @Operation(summary = "申请退款")
    @PostMapping("/{id}/refund")
    public Result<?> refundOrder(@PathVariable Long id, @RequestParam String reason) {
        return orderService.refundOrder(id, reason);
    }

    @Operation(summary = "批量删除订单")
    @DeleteMapping("/batch")
    public Result<?> deleteBatch(@RequestParam List<Long> ids) {
        return orderService.deleteBatch(ids);
    }

    @Operation(summary = "更新订单收货信息")
    @PutMapping("/{id}/address")
    public Result<?> updateOrderAddress(
            @PathVariable Long id,
            @RequestParam String address,
            @RequestParam String phone) {
        return orderService.updateOrderAddress(id, address, phone);
    }
    // 更新订单信息
    @Operation(summary = "更新订单信息")
    @PutMapping("/{id}")
    public Result<?> updateOrder(
            @PathVariable Long id,
            @RequestBody Order order) {
        return orderService.updateOrder(id, order);
    }

    @Operation(summary = "获取订单物流信息")
    @GetMapping("/{id}/logistics")
    public Result<?> getOrderLogistics(@PathVariable Long id) {
        return orderService.getOrderLogistics(id);
    }

    @Operation(summary = "处理退款申请")
    @PutMapping("/{id}/handle-refund")
    public Result<?> handleRefund(
            @PathVariable Long id,
            @RequestParam Integer status,
            @RequestParam String remark) {
        return orderService.handleRefund(id, status, remark);
    }

    /**
     * 获取订单状态
     */
    @GetMapping("/{id}/status")
    @Operation(summary = "获取订单状态")
    public Result<Integer> getOrderStatus(@PathVariable Long id) {
        // 查询订单
        Order order = orderMapper.selectById(id);
        if (order == null) {
            return Result.error("-1","订单不存在");
        }
        // 返回订单状态
        return Result.success(order.getStatus());
    }
} 